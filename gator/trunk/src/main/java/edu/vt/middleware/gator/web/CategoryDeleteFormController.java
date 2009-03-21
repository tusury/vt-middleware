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

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.RequestParamExtractor;

/**
 * Handles deletion of category configuration elements.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class CategoryDeleteFormController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    final CategoryConfig category = configManager.find(
      CategoryConfig.class,
      RequestParamExtractor.getCategoryId(request));
    if (category == null) {
      throw new IllegalArgumentException(
          "Illegal attempt to delete non-existent category.");
    }
    final DeleteSpec spec = new DeleteSpec();
    spec.setId(category.getId());
    spec.setName(category.getName());
    spec.setTypeName("Category");
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
    final CategoryConfig category = configManager.find(
      CategoryConfig.class,
      spec.getId());
    final ProjectConfig project = category.getProject();
    project.removeCategory(category);
    project.setModifiedDate(Calendar.getInstance());
    configManager.save(project);
    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), project));
  }
}
