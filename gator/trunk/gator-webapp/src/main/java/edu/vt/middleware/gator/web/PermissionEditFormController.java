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
package edu.vt.middleware.gator.web;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.validation.PermissonValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Handles additions and changes to project security permissions.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes("perm")
public class PermissionEditFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "permEdit";

  @Autowired
  @NotNull
  private PermissonValidator validator;
  
  
  @InitBinder
  public void initValidator(final WebDataBinder binder)
  {
    if (binder.getTarget() != null &&
        validator.supports(binder.getTarget().getClass()))
    {
      binder.setValidator(validator);
    }
  }


  @RequestMapping(
      value = "/project/{projectName}/perm/add.html",
      method = RequestMethod.GET)
  public String getNewPermisssion(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    // Touch permissions so they are available during validation
    project.getPermissions();
    final PermissionConfig perm = new PermissionConfig();
    perm.setProject(project);
    model.addAttribute("perm", perm);
    return VIEW_NAME;
  }


  @RequestMapping(
      value = "/project/{projectName}/perm/{permId}/edit.html",
      method = RequestMethod.GET)
  public String getPermisssion(
      @PathVariable("projectName") final String projectName,
      @PathVariable("permId") final int permId,
      final Model model)
  {
    final PermissionConfig perm =
      getProject(projectName).getPermission(permId);
    if (perm == null) {
      throw new IllegalArgumentException(
        String.format("Permisssion ID=%s not found in project '%s'.",
            permId, projectName));
    }
    model.addAttribute("perm", perm);
    return VIEW_NAME;
  }


  @RequestMapping(
      value = {
          "/project/{projectName}/perm/add.html",
          "/project/{projectName}/perm/{permId}/edit.html"
      },
      method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String savePermission(
      @Valid @ModelAttribute("perm") final PermissionConfig perm,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final ProjectConfig project = perm.getProject();
    // Operate on the database version of the project which contains
    // existing permissions.
    // MUST do this otherwise perms will be whatever user entered on form.
    configManager.savePermissions(
      configManager.find(ProjectConfig.class, project.getId()),
      perm.getName(),
      perm.getPermissionBits());
    return String.format(
        "redirect:/secure/project/%s/edit.html", project.getName());
  }

}
