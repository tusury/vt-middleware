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

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.ParamConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.log4j.SocketServer;
import edu.vt.middleware.gator.util.FileHelper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles delivering an XML view of the project configuration that could be
 * parsed by the log4j {@link DOMConfigurator}.
 *
 * @author Marvin S. Addison
 *
 */
@Controller
public class XmlConfigViewController extends AbstractController
{
  /** IP address socket server is bound to */
  protected String bindAddress = SocketServer.DEFAULT_BIND_ADDRESS;

  /** Port socket server will listen on */
  protected int port = SocketServer.DEFAULT_PORT;
 
  
  /**
   * Sets the bind address on which incoming connections will be accepted.
   * @param ipAddress Dotted IP address of bind address.
   */
  public void setBindAddress(final String ipAddress)
  {
    bindAddress = ipAddress;
  }


  /**
   * Sets the port on which to listen for client connections.
   * @param n Listening port number.
   */
  public void setPort(final int n)
  {
    this.port = n;
  }


  @RequestMapping(
      value = "/project/{projectName}/log4j.xml",
      method = RequestMethod.GET) 
  public String getLog4jXml(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    ProjectConfig project = getProject(projectName);
    // Update file appender paths to be suitable for clients
    // Work on a clone so the original is unchanged
    project = ControllerHelper.cloneProject(project);
    for (AppenderConfig appender : project.getAppenders()) {
      final ParamConfig fileParam = appender.getAppenderParam("file");
      if (fileParam != null) {
        final String clientAppenderPath = FileHelper.pathCat(
          project.getClientLogDir(),
          fileParam.getValue());
        fileParam.setValue(clientAppenderPath);
      }
    }
    model.addAttribute("project", project);
    model.addAttribute("bindAddress", bindAddress);
    model.addAttribute("port", port);
    return "log4jXml";
  }
}
