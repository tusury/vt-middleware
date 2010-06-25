/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.log4j;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.ParamConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.util.FileHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.OptionHandler;

/**
 * Utility class with static methods to create and configure log4j configuration
 * objects.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public final class Log4jConfigUtils
{

  /** Logger instance. */
  private static final Log LOGGER = LogFactory.getLog(Log4jConfigUtils.class);

  /** Default protected constructor of utility class. */
  protected Log4jConfigUtils() {}

  /**
   * Creates a Log4j appender from an {@link AppenderConfig} domain object.
   *
   * @param  project  Project configuration to which appender belongs.
   * @param  appenderCfg  Source of appender configuration.
   * @param  rootDirectory  Full path to appender root directory.
   *
   * @return  Log4j appender that is semantically simlar to given appender
   * configuration.
   *
   * @throws  ConfigurationException  On configuration errors.
   */
  public static Appender toLog4jAppender(
    final ProjectConfig project,
    final AppenderConfig appenderCfg,
    final String rootDirectory)
    throws ConfigurationException
  {
    if (appenderCfg.getAppenderClassName() == null) {
      throw new ConfigurationException("Appender class name required.");
    }
    if (appenderCfg.getName() == null) {
      throw new ConfigurationException("Appender name required.");
    }

    final Appender appender = instantiate(
      Appender.class,
      appenderCfg.getAppenderClassName());
    appender.setName(appenderCfg.getName());
    if (appenderCfg.getErrorHandlerClassName() != null) {
      final ErrorHandler errHandler = instantiate(
        ErrorHandler.class,
        appenderCfg.getErrorHandlerClassName());
      activate(errHandler);
      appender.setErrorHandler(errHandler);
    }
    if (appenderCfg.getLayoutClassName() != null) {
      final Layout layout = instantiate(
        Layout.class,
        appenderCfg.getLayoutClassName());
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
        value = FileHelper.pathCat(rootDirectory, project.getName(), value);
      }
      setProperty(appender, param.getName(), value);
    }
    activate(appender);
    return appender;
  }


  /**
   * Instantiate a new instance of the given class name.
   *
   * @param  <T>  Type of class to instantiate.
   * @param  clazz  Class of type to instantiate.
   * @param  name  Fully-qualified class name.
   *
   * @return  New instance of specified class.
   *
   * @throws  ConfigurationException  On configuration errors.
   */
  public static <T> T instantiate(final Class<T> clazz, final String name)
    throws ConfigurationException
  {
    Class<?> c;
    try {
      LOGGER.trace("Instantiating new instance of " + name);
      c = Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new ConfigurationException(
        String.format("Class %s not found.", name));
    }
    try {
      return clazz.cast(c.newInstance());
    } catch (InstantiationException e) {
      throw new ConfigurationException(
        String.format("Cannot instantiate %s.", name),
        e);
    } catch (IllegalAccessException e) {
      throw new ConfigurationException(
        String.format(
          "Cannot instantiate %s -- constructor not accessible.",
          name),
        e);
    } catch (ClassCastException e) {
      throw new ConfigurationException(
        String.format("%s is not an instance of %s", name, clazz),
        e);
    }
  }


  /**
   * Sets the value of a property on a target object. Property setter is
   * expected to follow JavaBean naming convention.
   *
   * @param  target  Target of setter invocation.
   * @param  property  Name of property to set. Without "set" prefix.
   * @param  value  Property value.
   *
   * @throws  ConfigurationException  On configuration errors.
   */
  public static void setProperty(
    final Object target,
    final String property,
    final String value)
    throws ConfigurationException
  {
    final PropertySetter ps = new PropertySetter(target);
    LOGGER.trace(
      String.format(
        "Setting property %s on instance of %s",
        property,
        target.getClass()));
    ps.setProperty(property, value);
  }


  /**
   * Activates any log4j object that implements {@link OptionHandler} by calling
   * the {@link OptionHandler#activateOptions()} method.
   *
   * @param  o  Object that possibly implements {@link OptionHandler}.
   */
  public static void activate(final Object o)
  {
    if (o instanceof OptionHandler) {
      ((OptionHandler) o).activateOptions();
    }
  }
}
