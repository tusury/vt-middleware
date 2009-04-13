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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.util.RequestParamExtractor;

/**
 * Handles changes to client configuration.
 *
 * @author Marvin S. Addison
 *
 */
public class ClientEditFormController extends BaseFormController
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
    ClientConfig client = project.getClient(
      RequestParamExtractor.getClientId(request));
    if (client == null) {
      client = new ClientConfig();
      client.setProject(project);
    }
    return client;
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
    return refData;
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
    final ClientConfig client = (ClientConfig) command;
    final ProjectConfig project = client.getProject();
    // For new clients or name changes, ensure name is unique within project
    final ClientConfig clientFromDb = configManager.find(
        ClientConfig.class,
        client.getId());
    ProjectConfig checkProject = null;
    if (clientFromDb == null) {
      checkProject = project;
    } else if (!clientFromDb.getName().equals(client.getName())) {
      checkProject = clientFromDb.getProject();
    }
    if (checkProject.getClient(client.getName()) != null) {
      errors.rejectValue(
        "name",
        "error.client.uniqueName",
        new Object[] {client.getName()},
      "Client name must be unique in project.");
    }
    // Ensure this client does not exist in any other projects
    final List<ProjectConfig> otherProjects =
      configManager.findProjectsByClientName(client.getName());
    for (ProjectConfig p : otherProjects)
    {
      if (!p.equals(project)) {
        errors.rejectValue(
          "name",
          "error.client.globallyUnique",
          new Object[] {client.getName(), p.getName()},
          "A client is only allowed in a single project.");
      }
    }
    if (errors.hasErrors()) {
      return showForm(request, response, errors);
    } else {
      if (clientFromDb == null) {
        project.addClient(client);
      }
      configManager.save(project);
      return new ModelAndView(
          ControllerHelper.filterViewName(getSuccessView(), project));
    }

  }
}
