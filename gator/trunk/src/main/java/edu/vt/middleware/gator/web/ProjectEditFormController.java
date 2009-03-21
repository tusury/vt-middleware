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

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.RequestParamExtractor;

/**
 * Controller that handles editing projects.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class ProjectEditFormController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    if (project == null) {
      project = new ProjectConfig();
      // Add all permissions to new project for current user principal
      project.addPermission(
        ControllerHelper.createAllPermissions(
          request.getUserPrincipal().getName()));
    }
    return project;
  }


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
    final boolean isNew = !configManager.exists(project);
    // Ensure project name is unique
    if (isNew || nameChanged(project)) {
      if (configManager.findProject(project.getName()) != null) {
        errors.rejectValue(
          "name",
          "error.project.uniqueName",
          new Object[] {project.getName()},
          "Project name must be unique.");
        return showForm(request, errors, getFormView());
      }
    }
    project.setModifiedDate(Calendar.getInstance());
    configManager.save(project);
    return new ModelAndView(
      ControllerHelper.filterViewName(getSuccessView(), project));
  }
 
 
  /**
   * Determines whether the name of the given project has changed from
   * what is recorded in the DB.
   * @param project Project to evaluate.
   * @return True if name of given project is different from that in the DB,
   * false otherwise.  Returns false if project does not exist in DB.
   */
  private boolean nameChanged(final ProjectConfig project)
  {
    final ProjectConfig projectFromDb = configManager.find(
      ProjectConfig.class,
      project.getId());
    if (projectFromDb != null) {
      return !projectFromDb.getName().equals(project.getName());
    } else {
      return false;
    }
  }
}
