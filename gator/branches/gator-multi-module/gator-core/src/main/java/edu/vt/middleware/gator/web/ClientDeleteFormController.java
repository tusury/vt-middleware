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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.RequestParamExtractor;

/**
 * Handle deletion of client configuration elements.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ClientDeleteFormController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    final ClientConfig client = configManager.find(
      ClientConfig.class,
      RequestParamExtractor.getClientId(request));
    if (client == null) {
      throw new IllegalArgumentException(
          "Illegal attempt to delete non-existent client.");
    }
    final DeleteSpec spec = new DeleteSpec();
    spec.setId(client.getId());
    spec.setName(client.getName());
    spec.setTypeName("Client");
    return spec;
  }


  /** {@inheritDoc} */
  @Override
  protected ModelAndView onSubmit(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object command, final BindException errors)
      throws Exception
  {
    final DeleteSpec spec = (DeleteSpec) command;
    if (!spec.getConfirmationFlag()) {
      errors.reject(
          "error.delete.confirmation",
          "Delete confirmation required.");
      return showForm(request, errors, getFormView());
    }
    final ClientConfig client = configManager.find(
      ClientConfig.class,
      spec.getId());
    final ProjectConfig project = client.getProject();
    project.removeClient(client);
    configManager.save(project);
    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), project));
  }
}
