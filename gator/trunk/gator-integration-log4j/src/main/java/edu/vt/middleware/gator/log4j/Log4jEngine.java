/*
  $Id: $

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.log4j;

import java.io.File;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.server.ConfigurationException;
import edu.vt.middleware.gator.server.EngineException;
import edu.vt.middleware.gator.server.LoggingEngine;
import edu.vt.middleware.gator.server.status.ClientStatus;
import edu.vt.middleware.gator.server.status.LoggerStatus;

/**
 * Adapter for handling log4j configuration and logging events.
 *
 * @author  Marvin S. Addison
 * @version  $Revision: $
 *
 */
public class Log4jEngine implements LoggingEngine
{
  /** Layout used to provide a string representation of log events. */
  private final Layout toStringLayout = new TTCCLayout();

  /** Maps projects to logger repositories for 1:1 relationship. */
  private final Map<ProjectConfig, LoggerRepository> projectRepositoryMap =
    new HashMap<ProjectConfig, LoggerRepository>();

  /** Maps clients to logger repositories for 1:1 relationship. */
  private final Map<InetAddress, LoggerRepository> clientRepositoryMap =
    new HashMap<InetAddress, LoggerRepository>();

  /** Client logs will be written below here. */
  private final String rootDirectory;
  

  /**
   * Creates a new instance.
   *
   * @param  directory Directory on the server below which log events are
   * written.  In particular, <code>FileAppender</code>s path attributes are
   * modified such that this directory path is prepended to root all logs
   * below the given directory.
   */
  public Log4jEngine(final String directory) {
    rootDirectory = directory;
  }


  /** {@inheritDoc} */
  public void start() throws EngineException
  {
    final File d = new File(rootDirectory);
    if (!d.exists()) {
      if (!d.mkdirs()) {
	      throw new EngineException(
	          "Error creating root directory " + rootDirectory);
      }
    }
  }
 
 
  /**
   * Determines whether the given event is supported.
   *
   * @return  True if object is an instance of <code>LoggingEvent</code>,
   * false otherwise.
   */
  public boolean supports(Object event)
  {
    return event instanceof LoggingEvent;
  }


  /** {@inheritDoc} */
  public void register(final InetAddress client, final ProjectConfig project)
    throws EngineException
  {
    LoggerRepository repository = projectRepositoryMap.get(project);
    if (repository == null) {
      synchronized (projectRepositoryMap) {
        if (repository == null) {
          repository = new Hierarchy(new RootLogger(Level.ALL));
          projectRepositoryMap.put(project, repository);
        }
      }
    }
    synchronized (clientRepositoryMap) {
      if (clientRepositoryMap.containsKey(client)) {
        throw new EngineException(client + " is already registered.");
      }
      clientRepositoryMap.put(client, repository);
    }
    configureInternal(project, repository);
  }


  /**
   * Configures a log4j <code>Hierarchy</code> based on the given configuration.
   *
   * @param  project  Project configuration.
   *
   * @throws  ConfigurationException  On configuration errors.
   */
  public void configure(final ProjectConfig project)
      throws ConfigurationException
  {
    final LoggerRepository repository = projectRepositoryMap.get(project);
    if (repository == null) {
      throw new ConfigurationException(
          "LoggerRepository not found for " + project);
    }
    configureInternal(project, repository);
  }


  /** {@inheritDoc} */
  public void configure(final ClientStatus clientStatus)
  {
    final InetAddress client = clientStatus.getClient();
    final LoggerRepository repository = clientRepositoryMap.get(client);
    if (repository == null) {
      throw new IllegalStateException(
          "LoggerRepository not found for " + client);
    }
    clientStatus.getPropertyMap().put("threshold", repository.getThreshold());
    clientStatus.getLoggers().add(
        createLoggerStatus(repository.getRootLogger()));
    final Enumeration<?> loggerEnum = repository.getCurrentLoggers();
    while (loggerEnum.hasMoreElements()) {
      clientStatus.getLoggers().add(
          createLoggerStatus((Logger) loggerEnum.nextElement()));
    }
  }


  /**
   * Passes the given logging event object to the underlying log4j engine
   * to be handled.  The logging event MUST be an instance of
   * <code>org.apache.log4j.spi.LoggingEvent</code>.
   *
   * @param  sender  Internet address of sender.
   * @param  event  Logging event.
   *
   * @throws  EngineException  On errors.
   */
  public void handleEvent(final InetAddress sender, final Object event)
      throws EngineException
  {
    if (!supports(event)) {
      throw new EngineException(event + " not supported");
    }
    MDC.put("host", sender.getHostName());
    MDC.put("ip", sender.getHostAddress());
    final LoggingEvent loggingEvent = (LoggingEvent) event;
    final LoggerRepository repository = clientRepositoryMap.get(sender);
    if (repository == null) {
      throw new EngineException("LoggerRepository not found for " + sender);
    }
    final Logger svrLogger = repository.getLogger(loggingEvent.getLoggerName());
    final Level level = svrLogger.getEffectiveLevel();
    if (loggingEvent.getLevel().isGreaterOrEqual(level)) {
      svrLogger.callAppenders(loggingEvent);
    }
  }


  /**
   * Shuts down the underlying log4j <code>Hierarchy</code>.
   */
  public void shutdown()
  {
    for (LoggerRepository repo : projectRepositoryMap.values()) {
      repo.shutdown();
    }
    projectRepositoryMap.clear();
    clientRepositoryMap.clear();
  }


  /**
   * Renders a <code>LoggingEvent</code> using the log4j <code>TTCCLayout</code>.
   *
   * @param  event  Instance of <code>LoggingEvent</code>.
   */
  public String toString(final Object event)
  {
    if (!supports(event)) {
      throw new IllegalArgumentException(event + " not supported");
    }
    return toStringLayout.format((LoggingEvent) event);
  }


  /**
   * Applies the given project configuration to the provided log4j
   * <code>LoggerHierarchy</code>.
   * 
   * @param  config  Project configuration.
   * @param  repository  Log4j repository.
   *
   * @throws  ConfigurationException  On configuration errors.
   */
  protected void configureInternal(
      final ProjectConfig project, final LoggerRepository repository)
      throws ConfigurationException 
  {
    // Map of appender names to log4j appenders
    final Map<String, Appender> appenderMap = new HashMap<String, Appender>();
    for (AppenderConfig appender : project.getAppenders()) {
      appenderMap.put(
        appender.getName(),
        Log4jConfigUtils.toLog4jAppender(
          project,
          appender,
          rootDirectory));
    }
    for (CategoryConfig category : project.getCategories()) {
      Logger logger = null;
      if (category.isRoot()) {
        logger = repository.getRootLogger();
      } else {
        logger = repository.getLogger(category.getName());
      }
      logger.setAdditivity(category.getAdditivity());
      logger.setLevel(Level.toLevel(category.getLevel()));
      for (AppenderConfig catAppender : category.getAppenders()) {
        final Appender a = appenderMap.get(catAppender.getName());
        if (a == null) {
          throw new ConfigurationException(
            String.format(
              "Category %s references appender %s that does not exist in " +
              "project %s.",
              category.getName(),
              catAppender.getName(),
              project.getName()));
        }
        logger.addAppender(a);
      }
    }
  }


  /**
   * Creates a {@link LoggerStatus} instance from a log4j {@link Logger} instance.
   * 
   * @param  logger  Log4j logger.
   *
   * @return  Logger information for Web display.
   */
  private LoggerStatus createLoggerStatus(final Logger logger)
  {
    final LoggerStatus lstat = new LoggerStatus();
    lstat.getPropertyMap().put("category", logger.getName());
    lstat.getPropertyMap().put("level", logger.getLevel());
    lstat.getPropertyMap().put("effectiveLevel", logger.getEffectiveLevel());
    lstat.getPropertyMap().put("additivity", logger.getAdditivity());
    Category current = logger;
    boolean isAdditive = false;
    do {
      @SuppressWarnings("rawtypes")
      final Enumeration e = current.getAllAppenders();
      while (e.hasMoreElements()) {
        lstat.getAppenders().add(((Appender) e.nextElement()).getName());
      }
      isAdditive = current.getAdditivity();
      current = current.getParent();
    } while (current != null && isAdditive);
    
    return lstat;
  }
}
