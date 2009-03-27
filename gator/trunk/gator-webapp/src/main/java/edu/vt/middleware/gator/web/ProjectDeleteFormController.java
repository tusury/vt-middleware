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

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.util.RequestParamExtractor;

/**
 * Handles project deletion.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class ProjectDeleteFormController extends BaseDeleteFromController
{
  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    if (project == null) {
      throw new IllegalArgumentException(
          "Illegal attempt to delete non-existent project.");
    }
    final DeleteSpec spec = new DeleteSpec();
    spec.setId(project.getId());
    spec.setName(project.getName());
    return spec;
  }


  /** {@inheritDoc} */
  @Override
  protected ModelAndView onSubmit(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object command,
      final BindException errors)
      throws Exception
  {
    final DeleteSpec spec = (DeleteSpec) command;
    if (!validate(errors, spec)) {
      return showForm(request, errors, getFormView());
    }
    final ProjectConfig projectFromDb = configManager.find(
      ProjectConfig.class,
      spec.getId());
    configManager.delete(projectFromDb);
    return new ModelAndView(getSuccessView());
  }
}
