/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * JPA-based configuration manager.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class JpaConfigManager implements ConfigManager, InitializingBean
{
  /** Logger instance */
  private final Log logger = LogFactory.getLog(getClass());

  /** Creates entity manager instances for persistence operations */
  private EntityManagerFactory entityManagerFactory;
  
  /** Holds registered listeners of configuration change events */
  private List<ConfigChangeListener> configChangeListeners;


  /**
   * @param factory The entity manager factory to set.
   */
  public void setEntityManagerFactory(final EntityManagerFactory factory)
  {
    entityManagerFactory = factory;
  }


  /**
   * Gets the listeners that will receive project configuration change messages.
   * @return listeners Registered listeners.
   */
  public List<ConfigChangeListener> getConfigChangeListeners() {
    if (configChangeListeners == null) {
      configChangeListeners = new ArrayList<ConfigChangeListener>();
    }
    return configChangeListeners;
  }


  /** {@inheritDoc} */
  public void setConfigChangeListeners(
      final List<ConfigChangeListener> listeners) {
    this.configChangeListeners = listeners;
  }


  /** {@inheritDoc} */
  public void afterPropertiesSet() throws Exception
  {
    Assert.notNull(entityManagerFactory, "EntityManagerFactory is required");
  }


  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public <T extends Config> List<T> findAll(final Class<T> type)
  {
    final EntityManager em = getEntityManager();
    final String queryString = String.format(
      "SELECT t FROM %s t",
      type.getSimpleName());
    logger.debug("Executing query " + queryString);
    final List<T> results = em.createQuery(queryString).getResultList();
    if (type.equals(ProjectConfig.class)) {
      for (T result : results) {
        // Touch project permissions to force lazy load
        getProject(result).getPermissions().size();
      }
    }
    return results;
  }


  /** {@inheritDoc} */
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public <T extends Config> T find(final Class<T> type, final int id)
  {
    final EntityManager em = getEntityManager();
    logger.debug(
      String.format("Querying for %s ID=%s", type.getSimpleName(), id));
    final T result = em.find(type, id);
    if (result != null) {
      // Touch project permissions to force lazy load
      getProject(result).getPermissions().size();
    }
    return result;
  }


  /** {@inheritDoc} */
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public ProjectConfig findProject(final String name)
  {
    final EntityManager em = getEntityManager();
    final String queryString =
      "SELECT p FROM ProjectConfig p WHERE p.name = :name";
    final Query query = em.createQuery(queryString);
    query.setParameter("name", name);
    logger.debug("Executing query " + queryString);
    logger.debug("Query params: name=" + name);
    try {
      final ProjectConfig project = (ProjectConfig) query.getSingleResult();
      if (project != null) {
	      // Touch permissions to force lazy load
	      project.getPermissions().size();
      }
      return project;
    } catch (NoResultException e) {
      return null;
    }
  }


  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public List<ProjectConfig> findProjectsByClientName(final String name)
  {
    final EntityManager em = getEntityManager();
    final String queryString =
      "SELECT p FROM ProjectConfig p, IN(p.clientsInternal) c " +
        "WHERE c.name = :name";
    final Query query = em.createQuery(queryString);
    query.setParameter("name", name);
    logger.debug("Executing query " + queryString);
    logger.debug("Query params: name=" + name);
    return query.getResultList();
  }


  /** {@inheritDoc} */
  public ProjectConfig getProject(final Config config) {
    ProjectConfig project = null;
    if (config instanceof ProjectConfig) {
      project = (ProjectConfig) config;
    } else if(config instanceof AppenderConfig) {
      project = ((AppenderConfig) config).getProject();
    } else if(config instanceof CategoryConfig) {
      project = ((CategoryConfig) config).getProject();
    } else if(config instanceof ClientConfig) {
      project = ((ClientConfig) config).getProject();
    } else if(config instanceof ParamConfig) {
      project = ((ParamConfig) config).getAppender().getProject();
    } else if(config instanceof PermissionConfig) {
      project = ((PermissionConfig) config).getProject();
    }
    return project;
  }


  /** {@inheritDoc} */
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public boolean exists(final Config config)
  {
    return find(config.getClass(), config.getId()) != null;
  }


  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final Config config)
  {
    final EntityManager em = getEntityManager();
    logger.debug("Saving " + config);
    if (config.getId() == 0) {
      em.persist(config);
    } else {
      em.merge(config);
    }
    final ProjectConfig project = find(
      ProjectConfig.class,
      getProject(config).getId());
    for (ConfigChangeListener listener : getConfigChangeListeners()) {
      listener.projectChanged(this, project);
    }
  }


  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  public void delete(final Config ... objects)
  {
    final EntityManager em = getEntityManager();
    for (Config o : objects) {
	    logger.debug("Deleting " + o);
	    em.remove(o);
	    if (o instanceof ProjectConfig) {
	      for (ConfigChangeListener listener : getConfigChangeListeners()) {
	        listener.projectRemoved(this, (ProjectConfig) o);
	      }
	    }
    }
  }
  
 
  /**
   * Creates an entity manager from the factory.
   * @return New entity manager.
   */
  protected EntityManager getEntityManager()
  {
    // Gets the thread-bound entity manager or creates a new one
    // if none is bound to current thread
    return SharedEntityManagerCreator.createSharedEntityManager(
      entityManagerFactory);
  }
}
