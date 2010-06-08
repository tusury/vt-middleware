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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

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
@Controller
@RequestMapping("/secure")
@SessionAttributes({ "bulkData", "projectAppenders", "projectCategories" })
public class CategoryBulkEditFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "categoryBulkEdit";


  /**
   * @return Array of available logger levels, e.g. ERROR, INFO, DEBUG.
   */
  @ModelAttribute("logLevels")
  public String[] getLogLevels()
  {
    return CategoryConfig.LOG_LEVELS;
  }


  @RequestMapping(
      value = "/project/{projectName}/category/bulk_edit.html",
      method = RequestMethod.GET)
  public String getBulkData(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    model.addAttribute("bulkData", new BulkCategoryEditFormData(project));
    model.addAttribute("projectAppenders", project.getAppenders());
    model.addAttribute("projectCategories", project.getCategories());
    return VIEW_NAME;
  }
 
  
  @RequestMapping(
      value = "/project/{projectName}/category/bulk_edit.html",
      method = RequestMethod.POST)
  public String saveChanges(
      @Valid @ModelAttribute("bulkData") final BulkCategoryEditFormData bulkData,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final ProjectConfig project = bulkData.getProject();
    for (int categoryId : bulkData.getCategoryIds()) {
      final CategoryConfig category = project.getCategory(categoryId);
      if (category != null) {
        category.setLevel(bulkData.getLevel());
        if (bulkData.isClearExistingAppenders()) {
          category.getAppenders().clear();
        }
        for (int appenderId : bulkData.getAppenderIds()) {
          final AppenderConfig appender = project.getAppender(appenderId);
          if (appender != null) {
            category.getAppenders().add(appender);
          }
        }
      }
    }
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html#category", project.getName());
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
