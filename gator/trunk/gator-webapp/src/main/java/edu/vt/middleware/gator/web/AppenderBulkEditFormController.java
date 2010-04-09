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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.LayoutParamConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.ParametersEditor;

/**
 * Handles changes to multiple appenders in a single operation.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes({ "bulkData", "projectAppenders" })
public class AppenderBulkEditFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "appenderBulkEdit";


  @InitBinder
  public void initAppenderEditors(final WebDataBinder binder)
  {
    logger.trace("Registering custom data binders for appender bulk edits.");
    // Set requiredType to null to avoid property value conversion done
    // by Spring that causes ClassCastException in this case
    binder.registerCustomEditor(
      null,
      "appenderParams",
      new ParametersEditor<AppenderParamConfig>(AppenderParamConfig.class));
    binder.registerCustomEditor(
      null,
      "layoutParams",
      new ParametersEditor<LayoutParamConfig>(LayoutParamConfig.class));
  }


  @RequestMapping(
      value = "/project/{projectName}/appender/bulk_edit.html",
      method = RequestMethod.GET)
  public String getBulkData(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    model.addAttribute("bulkData", new BulkAppenderEditFormData(project.getName()));
    model.addAttribute("projectAppenders", project.getAppenders());
    return VIEW_NAME;
  }


  @RequestMapping(
      value = "/project/{projectName}/appender/bulk_edit.html",
      method = RequestMethod.POST)
  public String saveChanges(
      @Valid @ModelAttribute("bulkData") final BulkAppenderEditFormData bulkData,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final ProjectConfig project = getProject(bulkData.getProjectName());
    for (int id : bulkData.getAppenderIds()) {
      final AppenderConfig appender = project.getAppender(id);

      if (bulkData.getAppenderClassName() != null) {
	      appender.setAppenderClassName(bulkData.getAppenderClassName());
      }

      if (bulkData.getErrorHandlerClassName() != null ||
          bulkData.isApplyBlankErrorHandlerClass())
      {
	      appender.setErrorHandlerClassName(bulkData.getErrorHandlerClassName());
      }
      
      if (bulkData.getLayoutClassName() != null ||
          bulkData.isApplyBlankLayoutClass())
      {
	      appender.setLayoutClassName(bulkData.getLayoutClassName());
      }

      if (bulkData.isClearAppenderParams()) {
        appender.removeAllAppenderParams();
      }
      for (AppenderParamConfig param : bulkData.getAppenderParams()) {
        appender.addAppenderParam(ControllerHelper.cloneParameter(param));
      }

      if (bulkData.isClearLayoutParams()) {
        appender.removeAllLayoutParams();
      }
      for (LayoutParamConfig param : bulkData.getLayoutParams()) {
        appender.addLayoutParam(ControllerHelper.cloneParameter(param));
      }
    }
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html", project.getName());
  }


  /**
   * Form binding object for this controller.
   *
   * @author Marvin S. Addison
   *
   */
  public static class BulkAppenderEditFormData
  {
    private String projectName;
    
    private int[] appenderIds;
    
    private String appenderClassName;
    
    private String errorHandlerClassName;
    
    private boolean applyBlankErrorHandlerClass;
    
    private String layoutClassName;
    
    private boolean applyBlankLayoutClass;
    
    private boolean clearAppenderParams;

    private Set<AppenderParamConfig> appenderParams =
      new LinkedHashSet<AppenderParamConfig>();
    
    private boolean clearLayoutParams;

    private Set<LayoutParamConfig> layoutParams =
      new LinkedHashSet<LayoutParamConfig>();


    /**
     * Creates a new instance for the given project.
     * @param name Name of project whose appenders are to be changed.
     */
    public BulkAppenderEditFormData(final String name)
    {
      this.projectName = name;
    }

    /**
     * @return the appenderIds
     */
    public int[] getAppenderIds()
    {
      return appenderIds;
    }

    /**
     * @param appenderIds the appenderIds to set
     */
    public void setAppenderIds(final int[] appenderIds)
    {
      this.appenderIds = appenderIds;
    }

    /**
     * @return the appenderClassName
     */
    public String getAppenderClassName()
    {
      return appenderClassName;
    }

    /**
     * @param appenderClassName the appenderClassName to set
     */
    public void setAppenderClassName(final String appenderClassName)
    {
      this.appenderClassName = appenderClassName;
    }

    /**
     * @return the errorHandlerClassName
     */
    public String getErrorHandlerClassName()
    {
      return errorHandlerClassName;
    }

    /**
     * @param errorHandlerClassName the errorHandlerClassName to set
     */
    public void setErrorHandlerClassName(final String errorHandlerClassName)
    {
      this.errorHandlerClassName = errorHandlerClassName;
    }

    /**
     * @return the applyBlankErrorHandlerClass
     */
    public boolean isApplyBlankErrorHandlerClass()
    {
      return applyBlankErrorHandlerClass;
    }

    /**
     * @param applyBlankErrorHandlerClass the applyBlankErrorHandlerClass to set
     */
    public void setApplyBlankErrorHandlerClass(boolean applyBlankErrorHandlerClass)
    {
      this.applyBlankErrorHandlerClass = applyBlankErrorHandlerClass;
    }

    /**
     * @return the layoutClassName
     */
    public String getLayoutClassName()
    {
      return layoutClassName;
    }

    /**
     * @param layoutClassName the layoutClassName to set
     */
    public void setLayoutClassName(final String layoutClassName)
    {
      this.layoutClassName = layoutClassName;
    }

    /**
     * @return the applyBlankLayoutClass
     */
    public boolean isApplyBlankLayoutClass()
    {
      return applyBlankLayoutClass;
    }

    /**
     * @param applyBlankLayoutClass the applyBlankLayoutClass to set
     */
    public void setApplyBlankLayoutClass(boolean applyBlankLayoutClass)
    {
      this.applyBlankLayoutClass = applyBlankLayoutClass;
    }

    /**
     * @return the clearAppenderParams
     */
    public boolean isClearAppenderParams()
    {
      return clearAppenderParams;
    }

    /**
     * @param clearAppenderParams the clearAppenderParams to set
     */
    public void setClearAppenderParams(boolean clearAppenderParams)
    {
      this.clearAppenderParams = clearAppenderParams;
    }

    /**
     * @return the appenderParams
     */
    public Set<AppenderParamConfig> getAppenderParams()
    {
      return appenderParams;
    }

    /**
     * @param appenderParams the appenderParams to set
     */
    public void setAppenderParams(final Set<AppenderParamConfig> appenderParams)
    {
      this.appenderParams = appenderParams;
    }

    /**
     * @return the clearLayoutParams
     */
    public boolean isClearLayoutParams()
    {
      return clearLayoutParams;
    }

    /**
     * @param clearLayoutParams the clearLayoutParams to set
     */
    public void setClearLayoutParams(boolean clearLayoutParams)
    {
      this.clearLayoutParams = clearLayoutParams;
    }

    /**
     * @return the layoutParams
     */
    public Set<LayoutParamConfig> getLayoutParams()
    {
      return layoutParams;
    }

    /**
     * @param layoutParams the layoutParams to set
     */
    public void setLayoutParams(final Set<LayoutParamConfig> layoutParams)
    {
      this.layoutParams = layoutParams;
    }

    /**
     * @return the project name
     */
    public String getProjectName()
    {
      return projectName;
    }
  }
}
