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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ProjectConfig;

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
  protected ModelAndView onSubmit(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object command, final BindException errors)
      throws Exception
  {
    final ClientConfig client = (ClientConfig) command;
    final ProjectConfig project = client.getProject();
    if (!configManager.exists(client)) {
      project.addClient(client);
    }
    configManager.save(client);
    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), project));
  }
}
