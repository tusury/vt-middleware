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

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.RequestParamExtractor;

/**
 * Handles deletion of appender configuration elements.
 *
 * @author Marvin S. Addison
 *
 */
public class AppenderDeleteFormController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    final AppenderConfig appender = configManager.find(
      AppenderConfig.class,
      RequestParamExtractor.getAppenderId(request));
    if (appender == null) {
	    throw new IllegalArgumentException(
	        "Illegal attempt to delete non-existent appender.");
    }
    final DeleteSpec spec = new DeleteSpec();
    spec.setId(appender.getId());
    spec.setName(appender.getName());
    spec.setTypeName("Appender");
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
    final AppenderConfig appender = configManager.find(
      AppenderConfig.class,
      spec.getId());
    final ProjectConfig project = appender.getProject();
    project.removeAppender(appender);
    for (CategoryConfig cat : project.getCategories()) {
      cat.getAppenders().remove(appender);
    }
    configManager.save(project);
    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), project));
  }
}
