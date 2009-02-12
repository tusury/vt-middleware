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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Handles deletion of client configurations.
 *
 * @author Marvin S. Addison
 *
 */
public class ClientDeleteViewController extends BaseViewController
{
  /** {@inheritDoc} */
  protected ModelAndView handleRequestInternal(
      final HttpServletRequest request,
      final HttpServletResponse response) throws Exception
  {
    final ClientConfig client = configManager.find(
      ClientConfig.class,
      RequestParamExtractor.getClientId(request));
    if (client == null) {
      throw new IllegalArgumentException(
        "Illegal attempt to delete non-existent client.");
    }
    final ProjectConfig project = client.getProject();
    project.removeClient(client);
    configManager.delete(client);
    return new ModelAndView(
        ControllerHelper.filterViewName(getViewName(), project));
  }
}
