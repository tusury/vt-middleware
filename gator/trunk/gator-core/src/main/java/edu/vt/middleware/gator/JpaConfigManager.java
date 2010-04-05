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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    return em.createQuery(queryString).getResultList();
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
    return em.find(type, id);
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
      return (ProjectConfig) query.getSingleResult();
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
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public boolean exists(final Config config)
  {
    return find(config.getClass(), config.getId()) != null;
  }


  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final ProjectConfig project)
  {
    final EntityManager em = getEntityManager();
    final Set<ClientConfig> removedClients = new HashSet<ClientConfig>();
    logger.debug("Saving " + project);
    ProjectConfig liveProject;
    project.setModifiedDate(Calendar.getInstance());
    if (project.isNew()) {
      em.persist(project);
      liveProject = find(ProjectConfig.class, project.getId());
    } else {
      // Determine removed clients
      final ProjectConfig pDb = find(ProjectConfig.class, project.getId());
      for (ClientConfig client : pDb.getClients()) {
        if (project.getClient(client.getId()) == null) {
          removedClients.add(client);
        }
      }
      liveProject = em.merge(project);
    }
    // Notify registered listeners of project changes
    for (ConfigChangeListener listener : getConfigChangeListeners()) {
      listener.projectChanged(this, liveProject);
    }
    // Notify registered listeners of removed clients
    for (ClientConfig client : removedClients) {
      if (liveProject.getClient(client.getId()) == null) {
        for (ConfigChangeListener listener : getConfigChangeListeners()) {
          listener.clientRemoved(this, liveProject, client.getName());
        }
      }
    }
  }


  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  public void delete(final ProjectConfig project)
  {
    final EntityManager em = getEntityManager();
    logger.debug("Deleting " + project);
    ProjectConfig liveProject = project;
    if (!em.contains(project)) {
      liveProject = find(ProjectConfig.class, project.getId());
    }
    em.remove(liveProject);
    for (ConfigChangeListener listener : getConfigChangeListeners()) {
      listener.projectRemoved(this, liveProject);
    }
  }


  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  public void savePermissions(
    final ProjectConfig project,
    final String sid,
    final int bits)
  {
    final EntityManager em = getEntityManager();
    logger.debug(
      String.format(
        "Setting permissions for %s to %s on %s.",
        sid,
        bits,
        project));
    final PermissionConfig perm = project.getPermission(sid);
    if (perm == null) {
      project.addPermission(new PermissionConfig(sid, bits));
    } else {
      perm.setPermissionBits(bits);
    }
    project.setModifiedDate(Calendar.getInstance());
    em.merge(project);
  }


  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  public void deletePermissions(
    final ProjectConfig project,
    final int permissionId)
  {
    final EntityManager em = getEntityManager();
    logger.debug(
      String.format(
        "Deleting permission ID=%s from %s.",
        project,
        permissionId));
    project.removePermission(project.getPermission(permissionId));
    project.setModifiedDate(Calendar.getInstance());
    em.merge(project);
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
