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
import java.util.concurrent.Executor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class JpaConfigManager implements ConfigManager
{
  /** Logger instance */
  private final Log logger = LogFactory.getLog(getClass());
 
  /** Responsible for publishing events to registered listeners */
  private Executor eventExecutor;

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
   * @param  executor  Executor service used to publish events.
   */
  public void setEventExecutor(final Executor executor)
  {
    this.eventExecutor = executor;
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
  public void init() throws Exception
  {
    Assert.notNull(entityManagerFactory, "EntityManagerFactory is required");
    Assert.notNull(eventExecutor, "EventExecutor cannot be null.");
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
    
    // Touch all collections to lazy load dependent data so complete project
    // configuration is available to event handlers
    liveProject.getAppenders();
    liveProject.getCategories();
    liveProject.getClients();
    liveProject.getPermissions();
    
    // Fire events on a separate thread so we do not disrupt client thread
    // (e.g. avoid subscriber blocking)
    for (ConfigChangeListener listener : getConfigChangeListeners()) {
      eventExecutor.execute(
          new ProjectChangedEvent(listener, liveProject, removedClients));
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
    // Touch all collections to lazy load dependent data so complete project
    // configuration is available to event handlers
    liveProject.getAppenders();
    liveProject.getCategories();
    liveProject.getClients();
    liveProject.getPermissions();

    em.remove(liveProject);
    
    // Fire events on a separate thread so we do not disrupt client thread
    // (e.g. avoid subscriber blocking)
    for (ConfigChangeListener listener : getConfigChangeListeners()) {
      eventExecutor.execute(new ProjectRemovedEvent(listener, liveProject));
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
  
  
  private abstract class AbstractEvent
  {
    protected ConfigChangeListener listener;
    protected ProjectConfig project;

    protected AbstractEvent(
        final ConfigChangeListener listener,
        final ProjectConfig project)
    {
      this.listener = listener;
      this.project = project;
    }
  }
  
  
  private class ProjectChangedEvent extends AbstractEvent implements Runnable
  {
    private final Set<ClientConfig> removedClients;

    public ProjectChangedEvent(
        final ConfigChangeListener listener,
        final ProjectConfig project,
        final Set<ClientConfig> removedClients)
    {
      super(listener, project);
      this.removedClients = removedClients;
    }


    /** {@inheritDoc} */
    public void run()
    {
      // Send client removed events before project changed events for efficiency
      // Presumably, one of the main reasons to want to receive removed clients
      // notices is to release resources, so we want to do this first to prevent
      // config changes to clients that may have been removed.
      for (ClientConfig client : removedClients) {
        listener.clientRemoved(
            JpaConfigManager.this, project, client.getName());
      }
      listener.projectChanged(JpaConfigManager.this, project);
    }
  }


  private class ProjectRemovedEvent extends AbstractEvent implements Runnable
  {
    public ProjectRemovedEvent(
        final ConfigChangeListener listener,
        final ProjectConfig project)
    {
      super(listener, project);
    }


    /** {@inheritDoc} */
    public void run()
    {
      listener.projectRemoved(JpaConfigManager.this, project);
    }
  }
}
