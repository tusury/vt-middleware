/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Middleware.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.web;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.RequestParamExtractor;

/**
 * Handles copying an existing appender to a new one with a different name.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AppenderCopyFormController extends BaseFormController
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
    return new CopyAppenderSpec();
  }


  /** {@inheritDoc} */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request)
    throws Exception
  {
    final Map<String, Object> data = new HashMap<String, Object>();
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    data.put("project", project);
    data.put("appenders", project.getAppenders());
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
    final CopyAppenderSpec spec = (CopyAppenderSpec) command;
    final AppenderConfig source = configManager.find(
      AppenderConfig.class,
      spec.getSourceAppenderId());
    final ProjectConfig project = source.getProject();
    // Ensure appender name is unique within project
    for (AppenderConfig appender : project.getAppenders()) {
      if (appender.getName().equals(spec.getNewName())) {
        errors.rejectValue(
            "newName",
            "error.appender.uniqueName",
            new Object[] { appender.getName() },
            "Appender name must be unique.");
        return showForm(request, errors, getFormView());
      }
    }
    final AppenderConfig newAppender = ControllerHelper.cloneAppender(source);
    newAppender.setName(spec.getNewName());
    project.addAppender(newAppender);
    project.setModifiedDate(Calendar.getInstance());
    configManager.save(project);
    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), project));
  }


  /**
   * Form backing object for {@link AppenderCopyFormController}.
   *
   * @author Middleware
   * @version $Revision$
   *
   */
  public static class CopyAppenderSpec
  {
    /** ID of project to which appenders belong */
    private int projectId;

    /** ID of appender to be copied */
    private int sourceAppenderId;
   
    /** Name of new appender created from source */
    private String newName;


    /**
     * @return Parent project ID.
     */
    public int getProjectId()
    {
      return projectId;
    }

    /**
     * @param id Parent project ID.
     */
    public void setProjectId(final int id)
    {
      this.projectId = id;
    }

    /**
     * @return ID of appender to be copied.
     */
    public int getSourceAppenderId()
    {
      return sourceAppenderId;
    }

    /**
     * @param id ID of appender to be copied.
     */
    public void setSourceAppenderId(final int id)
    {
      this.sourceAppenderId = id;
    }

    /**
     * @return Name of new appender created from copy.
     */
    public String getNewName()
    {
      return newName;
    }

    /**
     * @param name Name of new appender created from copy.
     */
    public void setNewName(final String name)
    {
      this.newName = name;
    }
    
    
  }
}
