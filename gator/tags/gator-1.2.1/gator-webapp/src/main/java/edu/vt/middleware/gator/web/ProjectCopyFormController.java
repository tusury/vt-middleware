/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.web;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.validation.ProjectCopyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Handles making a deep copy of an existing project; new project must have
 * unique name.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes({ "spec", "projects" })
public class ProjectCopyFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "projectCopy";

  @Autowired @NotNull
  private ProjectCopyValidator validator;


  @InitBinder
  public void initValidator(final WebDataBinder binder)
  {
    if (
      binder.getTarget() != null &&
        validator.supports(binder.getTarget().getClass())) {
      binder.setValidator(validator);
    }
  }


  @ModelAttribute("projects")
  public List<ProjectConfig> getAllProjects()
  {
    return configManager.findAll(ProjectConfig.class);
  }


  @RequestMapping(
    value = "/project/copy.html",
    method = RequestMethod.GET
  )
  public String getCopyProjectSpec(final Model model)
  {
    model.addAttribute("spec", new CopySpec(ProjectConfig.class));
    return VIEW_NAME;
  }


  /** {@inheritDoc}. */
  @Transactional(propagation = Propagation.REQUIRED)
  @RequestMapping(
    value = "/project/copy.html",
    method = RequestMethod.POST
  )
  public String copy(
    @Valid
    @ModelAttribute("spec")
    final CopySpec spec,
    final BindingResult result,
    final HttpServletRequest request)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }

    final ProjectConfig source = configManager.find(
      ProjectConfig.class,
      spec.getSourceId());
    final ProjectConfig newProject = ControllerHelper.cloneProject(source);
    newProject.setName(spec.getName());
    // Add all permissions to new project for current user principal
    newProject.addPermission(
      ControllerHelper.createAllPermissions(
        request.getUserPrincipal().getName()));
    configManager.save(newProject);
    return
      String.format(
        "redirect:/secure/project/%s/edit.html",
        newProject.getName());
  }
}
