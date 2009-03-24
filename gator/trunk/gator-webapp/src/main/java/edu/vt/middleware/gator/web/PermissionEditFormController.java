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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.util.RequestParamExtractor;

/**
 * Handles additions and changes to project security permissions.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PermissionEditFormController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    if (project == null) {
      throw new IllegalArgumentException("Project not found.");
    }
    PermissionConfig perm = project.getPermission(
      RequestParamExtractor.getPermissionId(request));
    if (perm == null) {
      perm = new PermissionConfig();
      perm.setProject(project);
    }
    return perm;
  }


  /** {@inheritDoc} */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request)
      throws Exception
  {
    final Map<String, Object> refData = new HashMap<String, Object>();
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    refData.put("project", project);
    return refData;
  }


  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  protected ModelAndView onSubmit(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object command, final BindException errors)
      throws Exception
  {
    final PermissionConfig perm = (PermissionConfig) command;
    final ProjectConfig project = perm.getProject();
    if (perm.getId() > 0) {
      final PermissionConfig permFromDb = configManager.find(
        PermissionConfig.class,
        perm.getId());
      if (permFromDb != null) {
        final boolean isLastFullPerm = ControllerHelper.isLastFullPermissions(
            permFromDb.getProject(), permFromDb.getId());
        if (isLastFullPerm) {
          errors.reject(
              "error.edit.lastAllPermissions",
          "Cannot modify last permission entry with full permissions.");
          return showForm(request, errors, getFormView());
        }
      }
    }
    // Operate on the database version of the project which contains
    // existing permissions.
    // MUST do this otherwise perms will be whatever user entered on form.
    configManager.savePermissions(
      configManager.find(ProjectConfig.class, project.getId()),
      perm.getName(),
      perm.getPermissionBits());
    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), project));
  }
}
