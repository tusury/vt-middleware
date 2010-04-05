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
    final Config config = configManager.find(getConfigClass(configType), id);
    if (config == null) {
      throw new IllegalArgumentException(
          "Illegal attempt to delete non-existent " + configType);
    }
    final DeleteSpec spec = new DeleteSpec();
    spec.setConfigToBeDeleted(config);
    spec.setProject(getProject(config));
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
      @Valid @ModelAttribute("spec") final DeleteSpec spec,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final Config config = spec.getConfigToBeDeleted();
    final ProjectConfig project = configManager.find(
        ProjectConfig.class, spec.getProject().getId());
    logger.debug(String.format("Deleting %s from %s", config, project));
    removeConfig(project, config);
    logger.debug("Saving " + project);
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html", project.getName());
  }


  private static Class<? extends Config> getConfigClass(String typeName)
  {
    if ("appender".equals(typeName)) {
      return AppenderConfig.class;
    } else if ("category".equals(typeName)) {
      return CategoryConfig.class;
    } else if ("client".equals(typeName)) {
      return ClientConfig.class;
    } else if ("permission".equals(typeName)) {
      return PermissionConfig.class;
    } else {
      throw new IllegalArgumentException(typeName + " not supported.");
    }
  }


  private static ProjectConfig getProject(final Config config)
  {
    if (config instanceof AppenderConfig) {
      return ((AppenderConfig)config).getProject();
    } else if (config instanceof CategoryConfig) {
      return ((CategoryConfig)config).getProject();
    } else if (config instanceof ClientConfig) {
      return ((ClientConfig)config).getProject();
    } else if (config instanceof PermissionConfig) {
      return ((PermissionConfig)config).getProject();
    } else {
      throw new IllegalArgumentException(config + " not supported.");
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
}
