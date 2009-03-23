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

import edu.vt.middleware.gator.ProjectConfig;

/**
 * Handles making a deep copy of an existing project; new project must
 * have unique name.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ProjectCopyFormController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    return new CopyProjectSpec();
  }


  /** {@inheritDoc} */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request)
    throws Exception
  {
    final Map<String, Object> data = new HashMap<String, Object>();
    data.put("projects", configManager.findAll(ProjectConfig.class));
    return data;
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
    final CopyProjectSpec spec = (CopyProjectSpec) command;
    final ProjectConfig source = configManager.find(
      ProjectConfig.class,
      spec.sourceProjectId);
    final ProjectConfig existing = configManager.findProject(
      spec.getNewProjectName());
    if (existing != null) {
      errors.rejectValue(
        "newProjectName",
        "error.project.uniqueName",
        new Object[] {existing.getName()},
        "Project name must be unique.");
      return showForm(request, errors, getFormView());
    }
    final ProjectConfig newProject = ControllerHelper.cloneProject(source);
    newProject.setName(spec.getNewProjectName());
    // Add all permissions to new project for current user principal
    newProject.addPermission(
      ControllerHelper.createAllPermissions(
        request.getUserPrincipal().getName()));
    configManager.save(newProject);
    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), newProject));
  }


  /**
   * Form backing object for project copy controller.
   *
   * @author Middleware
   * @version $Revision$
   *
   */
  public static class CopyProjectSpec
  {
    /** ID of project to be copied to create a new one */
    private int sourceProjectId;
   
    /** Name of project created by copying */
    private String newProjectName;

    /**
     * @return the sourceProjectId
     */
    public int getSourceProjectId()
    {
      return sourceProjectId;
    }

    /**
     * @param sourceProjectId the sourceProjectId to set
     */
    public void setSourceProjectId(int sourceProjectId)
    {
      this.sourceProjectId = sourceProjectId;
    }

    /**
     * @return the newProjectName
     */
    public String getNewProjectName()
    {
      return newProjectName;
    }

    /**
     * @param newProjectName the newProjectName to set
     */
    public void setNewProjectName(String newProjectName)
    {
      this.newProjectName = newProjectName;
    }
    
    
  }
}
