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
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
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

import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.validation.ClientValidator;

/**
 * Handles changes to client configuration.
 *
 * @author Marvin S. Addison
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes("client")
public class ClientEditFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "clientEdit";

  @Autowired
  @NotNull
  private ClientValidator validator;
  
  
  @InitBinder
  public void initValidator(final WebDataBinder binder)
  {
    if (binder.getTarget() != null &&
        validator.supports(binder.getTarget().getClass()))
    {
      binder.setValidator(validator);
    }
  }


  @RequestMapping(
      value = "/project/{projectName}/client/add.html",
      method = RequestMethod.GET)
  public String getNewClient(
      @PathVariable("projectName") final String projectName,
      final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    final ClientConfig client = new ClientConfig();
    client.setProject(project);
    model.addAttribute("client", client);
    return VIEW_NAME;
  }


  @RequestMapping(
      value = "/project/{projectName}/client/{clientId}/edit.html",
      method = RequestMethod.GET)
  public String getClient(
      @PathVariable("projectName") final String projectName,
      @PathVariable("clientId") final int clientId,
      final Model model)
  {
    final ClientConfig client =
      getProject(projectName).getClient(clientId);
    if (client == null) {
      throw new IllegalArgumentException(
        String.format("Client ID=%s not found in project '%s'.",
            clientId, projectName));
    }
    model.addAttribute("client", client);
    return VIEW_NAME;
  }


  @RequestMapping(
      value = {
          "/project/{projectName}/client/add.html",
          "/project/{projectName}/client/{clientId}/edit.html"
      },
      method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String saveClient(
      @Valid @ModelAttribute("client") final ClientConfig client,
      final BindingResult result)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final ProjectConfig project = client.getProject();
    if (!configManager.exists(client)) {
      project.addClient(client);
    }
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html#client", project.getName());
  }
}
