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

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Controller for performing the same edits to multiple categories in one step.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class CategoryBulkEditFormController extends BaseFormController
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
    return new BulkCategoryEditFormData(project);
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
    refData.put("availableCategories", project.getCategories());
    refData.put("availableAppenders", project.getAppenders());
    refData.put("logLevels", CategoryConfig.LOG_LEVELS);
    return refData;
  }
  
  /** {@inheritDoc} */
  @Override
  protected ModelAndView onSubmit(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object command, final BindException errors)
      throws Exception
  {
    final BulkCategoryEditFormData formData =
      (BulkCategoryEditFormData) command;
    for (int categoryId : formData.getCategoryIds()) {
      final CategoryConfig category = configManager.find(
        CategoryConfig.class,
        categoryId);
      if (category != null) {
        category.setLevel(formData.getLevel());
        if (formData.isClearExistingAppenders()) {
          category.getAppenders().clear();
        }
        for (int appenderId : formData.getAppenderIds()) {
          final AppenderConfig appender = configManager.find(
              AppenderConfig.class,
              appenderId);
          if (appender != null) {
            category.getAppenders().add(appender);
          }
        }
        configManager.save(category);
      }
    }
    return new ModelAndView(
      ControllerHelper.filterViewName(getSuccessView(), formData.getProject()));
  }


  /**
   * Form binding object for this controller.
   *
   * @author Marvin S. Addison
   *
   */
  public static class BulkCategoryEditFormData
  {
    private ProjectConfig project;

    private int[] categoryIds;

    private int[] appenderIds;
    
    private String level;
    
    private boolean clearExistingAppenders;
    
   
    /**
     * Creates a new instance for the given project.
     * @param project Project to which categories belong.
     */
    public BulkCategoryEditFormData(final ProjectConfig project)
    {
      this.project = project;
    }
    
    /**
     * @return the project
     */
    public ProjectConfig getProject()
    {
      return project;
    }

    /**
     * @return the categoryIds
     */
    public int[] getCategoryIds()
    {
      return categoryIds;
    }

    /**
     * @param categoryIds the categoryIds to set
     */
    public void setCategoryIds(final int[] categoryIds)
    {
      this.categoryIds = categoryIds;
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
     * @return the level
     */
    public String getLevel()
    {
      return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(String level)
    {
      this.level = level;
    }

    /**
     * @param clearExistingAppenders the clearExistingAppenders to set
     */
    public void setClearExistingAppenders(boolean clearExistingAppenders)
    {
      this.clearExistingAppenders = clearExistingAppenders;
    }

    /**
     * @return the clearExistingAppenders
     */
    public boolean isClearExistingAppenders()
    {
      return clearExistingAppenders;
    }
  }
}
