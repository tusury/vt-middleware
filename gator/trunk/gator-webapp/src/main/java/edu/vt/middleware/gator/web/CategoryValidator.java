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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Validator for {@link CategoryEditFormController}.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class CategoryValidator implements Validator
{

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public boolean supports(final Class clazz)
  {
    return
      CategoryEditFormController.CategoryWrapper.class.isAssignableFrom(clazz);
  }

  /** {@inheritDoc} */
  public void validate(final Object target, final Errors errors)
  {
    final CategoryEditFormController.CategoryWrapper wrapper =
      (CategoryEditFormController.CategoryWrapper) target;
   
    final CategoryConfig category = wrapper.getCategory();
    final ProjectConfig project = category.getProject();
    for (int id : wrapper.getAppenderIds()) {
      final AppenderConfig appender = project.getAppender(id);
      if (appender == null) {
        errors.rejectValue(
          "appenderIds",
          "error.category.appenderNotExistsInProject",
          new Object[] {id, category.getName()},
          "One of the selected appenders does not exist in project.");
      }
      if (!project.getAppenderPolicy().allow(category, appender)) {
        errors.rejectValue(
          "appenderIds",
          "error.category.appenderPolicy",
          new Object[] {
            appender.getName(),
            category.getName(),
            project.getAppenderPolicy().getClass().getSimpleName(),
          },
          "The appender policy for the project forbids appender " +
            appender.getName());
      }
    }
  }

}
