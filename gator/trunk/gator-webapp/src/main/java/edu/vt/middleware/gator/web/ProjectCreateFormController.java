/*
  $Id: $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ProjectConfig;

/**
 * Handles creation of new projects.
 *
 * @author Middleware
 * @version $Revision: $
 *
 */
public class ProjectCreateFormController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  protected ModelAndView onSubmit(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object command,
      final BindException errors)
      throws Exception
  {
    final ProjectConfig project = (ProjectConfig) command;
    // Ensure project name is unique
    if (configManager.findProject(project.getName()) != null) {
      errors.rejectValue(
          "name",
          "error.project.uniqueName",
          new Object[] {project.getName()},
      "Project name must be unique.");
      return showForm(request, errors, getFormView());
    }
    // Add all permissions to new project for current user principal
    project.addPermission(
      ControllerHelper.createAllPermissions(
        request.getUserPrincipal().getName()));
    configManager.save(project);
    return new ModelAndView(
      ControllerHelper.filterViewName(getSuccessView(), project));
  }
}
