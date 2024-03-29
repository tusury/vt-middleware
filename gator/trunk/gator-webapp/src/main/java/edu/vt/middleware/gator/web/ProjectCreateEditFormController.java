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
package edu.vt.middleware.gator.web;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.server.LoggingEngine;
import edu.vt.middleware.gator.server.SocketServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller that handles editing projects.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes("project")
public class ProjectCreateEditFormController extends AbstractFormController
{
  public static final String CREATE_VIEW = "projectCreate";
  public static final String EDIT_VIEW = "projectEdit";
 
  @Autowired
  private SocketServer socketServer;


  @RequestMapping(
    value = "/project/add.html",
    method = RequestMethod.GET
  )
  public String getNewProject(final Model model)
  {
    model.addAttribute("project", new ProjectConfig());
    addLoggingEngines(model);
    return CREATE_VIEW;
  }


  @RequestMapping(
    value = "/project/{projectName}/edit.html",
    method = RequestMethod.GET
  )
  public String getProject(
    @PathVariable("projectName") final String projectName,
    final Model model)
  {
    model.addAttribute("project", getProject(projectName));
    addLoggingEngines(model);
    return EDIT_VIEW;
  }


  @RequestMapping(
    value = {
        "/project/add.html",
        "/project/{projectName}/edit.html"
    },
    method = RequestMethod.POST
  )
  @Transactional(propagation = Propagation.REQUIRED)
  public String saveProject(
    @Valid
    @ModelAttribute("project")
    final ProjectConfig project,
    final BindingResult result,
    final Principal currentUser,
    final HttpServletRequest request)
  {
    if (result.hasErrors()) {
      return project.isNew() ? CREATE_VIEW : EDIT_VIEW;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Saving " + project);
    }
    if (project.isNew()) {
      // Add all permissions to new project for current user principal
      project.addPermission(
        ControllerHelper.createAllPermissions(currentUser.getName()));
    }
    configManager.save(project);
    return
      String.format("redirect:/secure/project/%s/edit.html", project.getName());
  }


  /**
   * Add available logging engine names to model.
   *
   * @param  model  Controller model data.
   */
  private void addLoggingEngines(final Model model)
  {
    final Map<String, String> engineMap = new HashMap<String, String>();
    for (LoggingEngine engine : socketServer.getLoggingEngines()) {
      engineMap.put(
          engine.getClass().getName(),
          engine.getClass().getSimpleName());
    }
    model.addAttribute("loggingEngineMap", engineMap);
  }
}
