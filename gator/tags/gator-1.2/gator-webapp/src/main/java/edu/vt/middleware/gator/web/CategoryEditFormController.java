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

import java.beans.PropertyEditorSupport;

import javax.validation.Valid;

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

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Handles logging category configuration.
 *
 * @author Marvin S. Addison
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes({ "category", "projectAppenders" })
public class CategoryEditFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "categoryEdit";


  @InitBinder
  public void initCategoryEditors(final WebDataBinder binder)
  {
    binder.registerCustomEditor(
        AppenderConfig.class,
        "appenders",
        new PropertyEditorSupport()
        {
          private AppenderConfig value;

          public Object getValue()
          {
            return value;
          }
          
          public void setValue(final Object o)
          {
            value = (AppenderConfig) o;
          }
          
          public String getAsText()
          {
            return Integer.toString(value.getId());
          }

          public void setAsText(final String text)
          {
            value = configManager.find(
                AppenderConfig.class, Integer.parseInt(text));
          }
        });
  }


  /**
   * @return Array of available logger levels, e.g. ERROR, INFO, DEBUG.
   */
  @ModelAttribute("logLevels")
  public String[] getLogLevels()
  {
    return CategoryConfig.LOG_LEVELS;
  }


  @RequestMapping(
      value = "/project/{projectName}/category/add.html",
      method = RequestMethod.GET)
  public String getNewCategory(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    // Touch categories so they are available during validation
    project.getCategories();
    final CategoryConfig category = new CategoryConfig();
    category.setProject(project);
    model.addAttribute("category", category);
    model.addAttribute("projectAppenders", project.getAppenders());
    return VIEW_NAME;
  }


  @RequestMapping(
      value = "/project/{projectName}/category/{categoryId}/edit.html",
      method = RequestMethod.GET)
  public String getCategory(
      @PathVariable("projectName") final String projectName,
      @PathVariable("categoryId") final int categoryId,
      final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    final CategoryConfig category = project.getCategory(categoryId);
    if (category == null) {
      throw new IllegalArgumentException(
          String.format("Category ID=%s not found in project '%s'.",
              categoryId, projectName));
    }
    model.addAttribute("category", category);
    model.addAttribute("projectAppenders", project.getAppenders());
    return VIEW_NAME;
  }


  @RequestMapping(
      value = {
          "/project/{projectName}/category/add.html",
          "/project/{projectName}/category/{categoryId}/edit.html"
      },
      method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String saveCategory(
      @Valid @ModelAttribute("category") final CategoryConfig category,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final ProjectConfig project = category.getProject();
    if (!configManager.exists(category)) {
      project.addCategory(category);
    }
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html#category", project.getName());
  }
}
