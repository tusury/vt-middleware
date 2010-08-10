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

import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Log4j configuration handler that configures a logger hierarchy from a
 * Gator project configuration.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ProjectConfigurator implements Configurator, InitializingBean
{

  /** Logger instance. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Client logs will be written below here. */
  protected String clientRootLogDirectory;



  /** {@inheritDoc}. */
  public void setClientRootLogDirectory(final String dir)
  {
    this.clientRootLogDirectory = dir;
  }


  /** {@inheritDoc}. */
  public void afterPropertiesSet()
    throws Exception
  {
    Assert.notNull(
      clientRootLogDirectory,
      "ClientRootLogDirectory is required.");
  }


  /** {@inheritDoc}. */
  @Transactional(
    readOnly = true,
    propagation = Propagation.REQUIRED
  )
  public void configure(
    final ProjectConfig project,
    final LoggerRepository repository)
    throws ConfigurationException
  {
    repository.resetConfiguration();

    // Map of appender names to log4j appenders
    final Map<String, Appender> appenderMap = new HashMap<String, Appender>();
    for (AppenderConfig appender : project.getAppenders()) {
      appenderMap.put(
        appender.getName(),
        Log4jConfigUtils.toLog4jAppender(
          project,
          appender,
          clientRootLogDirectory));
    }
    for (CategoryConfig category : project.getCategories()) {
      Logger logger = null;
      if (category.isRoot()) {
        logger = repository.getRootLogger();
      } else {
        logger = repository.getLogger(category.getName());
      }
      logger.setAdditivity(category.getAdditivity());
      logger.setLevel(category.getLog4jLevel());
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

}
