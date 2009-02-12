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
package edu.vt.middleware.gator.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.ParamConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.log4j.SocketServer;
import edu.vt.middleware.gator.util.FileHelper;

import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles delivering an XML view of the project configuration that could be
 * parsed by the log4j {@link DOMConfigurator}.
 *
 * @author Marvin S. Addison
 *
 */
public class XmlConfigViewController extends BaseViewController
{
  private SocketServer socketServer;
 
  
  /**
   * Sets the socket server whose configuration is needed to create the log4j
   * XML configuration.
   * @param server Socket server.
   */
  public void setSocketServer(final SocketServer server)
  {
    socketServer = server;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void afterPropertiesSet() throws Exception
  {
    super.afterPropertiesSet();
    Assert.notNull(socketServer, "SocketServer is required.");
  }


  /** {@inheritDoc} */
  protected ModelAndView handleRequestInternal(
      final HttpServletRequest request,
      final HttpServletResponse response) throws Exception
  {
    final Map<String, Object> model = new HashMap<String, Object>();
    final String name = RequestParamExtractor.getProjectName(request);
    if (name == null) {
      throw new IllegalArgumentException("No project name specified.");
    }
    ProjectConfig project = configManager.findProject(name);
    if (project == null) {
      throw new IllegalArgumentException(name + " does not exist.");
    }
    // Update file appender paths to be suitable for clients
    // Work on a clone so the original is unchanged
    project = ControllerHelper.cloneProject(project);
    for (AppenderConfig appender : project.getAppenders()) {
      final ParamConfig fileParam = appender.getAppenderParam("file");
      if (fileParam != null) {
        final String clientAppenderPath = FileHelper.pathCat(
          project.getClientLogDir(),
          fileParam.getValue());
        logger.debug("Updated appender file path to " + clientAppenderPath);
        fileParam.setValue(clientAppenderPath);
      }
    }
    model.put("project", project);
    model.put("socketServer", socketServer);
    return new ModelAndView(getViewName(), "model", model);
  }
}
