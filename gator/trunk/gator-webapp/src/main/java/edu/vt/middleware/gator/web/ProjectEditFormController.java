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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import edu.vt.middleware.gator.ProjectConfig;

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
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes("project")
public class ProjectEditFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "projectEdit";


  @RequestMapping(value = "/project/add.html", method = RequestMethod.GET)
  public String getNewProject(final Model model)
  {
    model.addAttribute("project", new ProjectConfig());
    return VIEW_NAME;
  }


  @RequestMapping(
      value = "/project/{projectName}/edit.html",
      method = RequestMethod.GET)
  public String getProject(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    model.addAttribute("project", getProject(projectName));
    return VIEW_NAME;
  }


  @RequestMapping(
      value = {
          "/project/add.html",
          "/project/{projectName}/edit.html"
      },
      method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String saveProject(
      @Valid @ModelAttribute("project") final ProjectConfig project,
      final BindingResult result,
      final HttpServletRequest request)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    if (project.isNew()) {
      // Add all permissions to new project for current user principal
      project.addPermission(
        ControllerHelper.createAllPermissions(
          request.getUserPrincipal().getName()));
    }
    logger.debug("Saving " + project);
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html", project.getName());
  }
}
