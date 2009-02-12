/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.log4j;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ParamConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.util.FileHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Log4j configuration handler that configures a logger hierarchy
 * via JDBC.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class JdbcConfigurator implements Configurator, InitializingBean
{
  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());
  
  /** Client logs will be written below here */
  protected String clientRootLogDirectory;
  
  /** Project configuration manager */
  protected ConfigManager configManager;


  /** {@inheritDoc} */
  public void setConfigManager(final ConfigManager manager)
  {
    this.configManager = manager;
  }


  /** {@inheritDoc} */
  public void setClientRootLogDirectory(final String dir) {
    this.clientRootLogDirectory = dir;
  }


  /** {@inheritDoc} */
  public void afterPropertiesSet() throws Exception
  {
    Assert.notNull(configManager, "ConfigManager is required.");
    Assert.notNull(clientRootLogDirectory,
        "ClientRootLogDirectory is required.");
  }


  /** {@inheritDoc} */
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public void configure(
    final InetAddress addr,
    final LoggerRepository repository)
    throws UnknownClientException, ConfigurationException
  {
    final Set<ProjectConfig> projects = getProjects(addr);
    // Client must be registered with at least one project
    if (projects.size() == 0) {
      throw new UnknownClientException(
          String.format("%s not registered with any projects.", addr));
    }
    for (ProjectConfig p : projects) {
      configure(p, repository);
    }
  }


  /** {@inheritDoc} */
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED)
  public void configure(
    final ProjectConfig project,
    final LoggerRepository repository)
    throws ConfigurationException
  {
    repository.resetConfiguration();
    // Map of appender names to log4j appenders
    final Map<String, Appender> appenderMap = new HashMap<String, Appender>();
    for (AppenderConfig appender : project.getAppenders()) {
      appenderMap.put(appender.getName(), toLog4jAppender(project, appender));
    }
    for (CategoryConfig category : project.getCategories()) {
      Logger logger = null;
      if (category.isRoot()) {
        logger = repository.getRootLogger();
      } else {
        logger = repository.getLogger(category.getName());
      }
      logger.removeAllAppenders();
      logger.setLevel(category.getLog4jLevel());
      for (AppenderConfig catAppender : category.getAppenders()) {
        final Appender a = appenderMap.get(catAppender.getName());
        if (a == null) {
          throw new ConfigurationException(String.format(
              "Category %s references appender %s that does not exist in " +
              "project %s.",
              category.getName(), catAppender.getName(), project.getName()));
        }
        logger.addAppender(a);
      }
    }
  }


  /**
   * Gets all the projects of which the host possessing the given IP address
   * is a member.
   * @param addr IP address.
   * @return Set of projects of which addr is a member.
   */
  private Set<ProjectConfig> getProjects(final InetAddress addr) {
    final Set<ProjectConfig> projects = new HashSet<ProjectConfig>();
   
    // Add projects that contain the given client by host or IP address
    projects.addAll(
      configManager.findProjectsByClientName(addr.getHostAddress()));
    projects.addAll(
      configManager.findProjectsByClientName(addr.getHostName()));
    return projects;
  }

 
  /**
   * Creates a Log4j appender from an {@link AppenderConfig} domain object.
   * @param project Project configuration to which appender belongs.
   * @param appenderCfg Source of appender configuration.
   * @return Log4j appender that is semantically simlar to given appender
   * configuration.
   * @throws ConfigurationException On configuration errors.
   */
  private Appender toLog4jAppender(final ProjectConfig project,
      final AppenderConfig appenderCfg) throws ConfigurationException
  {
    if (appenderCfg.getAppenderClassName() == null) {
      throw new ConfigurationException("Appender class name required.");
    }
    if (appenderCfg.getName() == null) {
      throw new ConfigurationException("Appender name required.");
    }
    final Appender appender =
      instantiate(Appender.class, appenderCfg.getAppenderClassName());
    appender.setName(appenderCfg.getName());
    if (appenderCfg.getErrorHandlerClassName() != null) {
      final ErrorHandler errHandler = instantiate(
        ErrorHandler.class,
        appenderCfg.getErrorHandlerClassName());
      activate(errHandler);
	    appender.setErrorHandler(errHandler);
    }
    if (appenderCfg.getLayoutClassName() != null) {
	    final Layout layout =
	      instantiate(Layout.class, appenderCfg.getLayoutClassName());
	    for (ParamConfig param : appenderCfg.getLayoutParams()) {
	      setProperty(layout, param.getName(), param.getValue());
	    }
	    activate(layout);
	    appender.setLayout(layout);
    }
    for (ParamConfig param : appenderCfg.getAppenderParams()) {
      // Check for the "file" property of FileAppender and subclasses
      // and prefix path with clientRootLogDirectory
      String value = param.getValue();
      if (param.getName().equalsIgnoreCase("file")) {
        value = FileHelper.pathCat(
          clientRootLogDirectory,
          project.getName(),
          value);
      }
      setProperty(appender, param.getName(), value);
    }
    activate(appender);
    return appender;
  }


  /**
   * Instantiate a new instance of the given class name.
   * @param <T> Type of class to instantiate.
   * @param clazz Class of type to instantiate.
   * @param name Fully-qualified class name.
   * @return New instance of specified class.
   * @throws ConfigurationException On configuration errors.
   */
  private <T> T instantiate(final Class<T> clazz, final String name)
    throws ConfigurationException
  {
    Class<?> c;
    try {
      logger.debug("Instantiating new instance of " + name);
      c = Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new ConfigurationException(
          String.format("Class %s not found.", name));
    }
    try {
      return clazz.cast(c.newInstance());
    } catch (InstantiationException e) {
      throw new ConfigurationException(String.format(
          "Cannot instantiate %s.", name), e);
    } catch (IllegalAccessException e) {
      throw new ConfigurationException(String.format(
          "Cannot instantiate %s -- constructor not accessible.", name), e);
    } catch (ClassCastException e) {
      throw new ConfigurationException(String.format(
          "%s is not an instance of %s", name, clazz), e);
    }
  }
 
  /**
   * Sets the value of a property on a target object.  Property setter is
   * expected to follow JavaBean naming convention.
   * @param target Target of setter invocation.
   * @param property Name of property to set.  Without "set" prefix.
   * @param value Property value.
   * @throws ConfigurationException On configuration errors.
   */
  private void setProperty(final Object target, final String property,
    final String value) throws ConfigurationException
  {
    final PropertySetter ps = new PropertySetter(target);
    logger.debug(String.format(
        "Setting property %s on instance of %s",
        property,
        target.getClass()));
    ps.setProperty(property, value);
  }

  /**
   * Activates any log4j object that implements {@link OptionHandler}
   * by calling the {@link OptionHandler#activateOptions()} method.
   * @param o Object that possibly implements {@link OptionHandler}.
   */
  private void activate(final Object o)
  {
    if (o instanceof OptionHandler) {
      ((OptionHandler) o).activateOptions();
    }
  }
}
