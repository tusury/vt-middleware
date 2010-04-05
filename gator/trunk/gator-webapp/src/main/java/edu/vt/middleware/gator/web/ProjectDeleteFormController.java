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

import javax.validation.Valid;

import edu.vt.middleware.gator.ProjectConfig;

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

/**
 * Handles project deletion.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes("spec")
public class ProjectDeleteFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "deleteForm";

  @RequestMapping(
      value = "/project/{projectName}/delete.html",
      method = RequestMethod.GET)
  public String getDeleteSpec(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    final ProjectConfig project = configManager.findProject(projectName);
    if (project == null) {
      throw new IllegalArgumentException(
          "Illegal attempt to delete non-existent project.");
    }
    final DeleteSpec spec = new DeleteSpec();
    spec.setTypeName("Project");
    spec.setProject(project);
    spec.setConfigToBeDeleted(project);
    model.addAttribute("spec", spec);
    return VIEW_NAME;
  }


  @RequestMapping(
      value = "/project/{projectName}/delete.html",
      method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String deleteProject(
      @Valid @ModelAttribute("spec") final DeleteSpec spec,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final ProjectConfig project = (ProjectConfig) spec.getConfigToBeDeleted();
    logger.debug("Deleting " + project);
    configManager.delete(project);
    return "redirect:/secure/project/list.html";
  }
}
