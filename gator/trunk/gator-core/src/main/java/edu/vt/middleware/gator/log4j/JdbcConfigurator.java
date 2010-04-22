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
    configure(getProject(addr), repository);
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
   * Gets first project to which the host possessing the given IP address
   * is a member.
   * @param addr IP address.
   * @return First project to which the client at the given IP address
   * is a member.
   * @throws UnknownClientException If given client is not a member of any
   * projects.
   */
  private ProjectConfig getProject(final InetAddress addr)
    throws UnknownClientException {
    final Set<ProjectConfig> projects = new HashSet<ProjectConfig>();
   
    // Add projects that contain the given client by host or IP address
    projects.addAll(
      configManager.findProjectsByClientName(addr.getHostAddress()));
    projects.addAll(
      configManager.findProjectsByClientName(addr.getHostName()));
    if (projects.size() > 0) {
      return projects.iterator().next();
    } else {
      throw new UnknownClientException(
          String.format("%s not registered with any projects.", addr));
    }
  }
}
