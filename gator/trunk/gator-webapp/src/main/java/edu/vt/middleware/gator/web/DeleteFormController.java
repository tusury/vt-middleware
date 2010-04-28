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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.Config;
import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.validation.PermissonValidator;

/**
 * Handles deletion of project configuration elements.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes("spec")
public class DeleteFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "deleteForm";

  @RequestMapping(
      value = "/project/{projectName}/{configType}/{id}/delete.html",
      method = RequestMethod.GET)
  public String getDeleteSpec(
      @PathVariable("projectName") final String projectName,
      @PathVariable("configType") final String configType,
      @PathVariable("id") final int id,
      final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    final Config config = getConfig(project, configType, id);
    if (config == null) {
      throw new IllegalArgumentException(
          "Illegal attempt to delete non-existent " + configType);
    }
    final DeleteSpec spec = new DeleteSpec();
    spec.setConfigToBeDeleted(config);
    spec.setProject(project);
    spec.setTypeName(
        configType.substring(0, 1).toUpperCase() + configType.substring(1));
    model.addAttribute("spec", spec);
    return VIEW_NAME;
  }


  @RequestMapping(
      value = "/project/{projectName}/{configType}/{id}/delete.html",
      method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String deleteConfig(
      @PathVariable("configType") final String configType,
      @Valid @ModelAttribute("spec") final DeleteSpec spec,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final Config config = spec.getConfigToBeDeleted();
    final ProjectConfig project = spec.getProject();
    validateConfig(project, config, result);
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    logger.debug(String.format("Deleting %s from %s", config, project));
    removeConfig(project, config);
    logger.debug("Saving " + project);
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html#%s",
        project.getName(),
        configType);
  }


  private static Config getConfig(
      final ProjectConfig project,
      final String typeName,
      final int id)
  {
    if ("appender".equals(typeName)) {
      return project.getAppender(id);
    } else if ("category".equals(typeName)) {
      return project.getCategory(id);
    } else if ("client".equals(typeName)) {
      return project.getClient(id);
    } else if ("perm".equals(typeName)) {
      return project.getPermission(id);
    } else {
      throw new IllegalArgumentException(typeName + " not supported.");
    }
  }

  
  private void removeConfig(final ProjectConfig project, final Config config)
  {
    if (config instanceof AppenderConfig) {
      project.removeAppender((AppenderConfig)config);
    } else if (config instanceof CategoryConfig) {
      project.removeCategory((CategoryConfig)config);
    } else if (config instanceof ClientConfig) {
      project.removeClient((ClientConfig)config);
    } else if (config instanceof PermissionConfig) {
      project.removePermission((PermissionConfig)config);
    } else {
      throw new IllegalArgumentException(config + " not supported.");
    }
  }
  
  private void validateConfig(
      final ProjectConfig project,
      final Config config,
      final BindingResult result)
  {
    if (config instanceof PermissionConfig) {
      // Operate on DB version of project for permissions checking
      final ProjectConfig pFromDb = configManager.find(
          ProjectConfig.class, project.getId()); 
      if (PermissonValidator.isLastFullPermissions(pFromDb, config.getId())) {
        result.reject(
            "error.permission.deleteLastAllPermissions",
            "Cannot delete last permission.");
      }
    }
  }
}
