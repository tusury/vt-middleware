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
package edu.vt.middleware.gator.validation;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;

/**
 * Validates the {@link AppenderConstraint} constraint.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AppenderConstraintValidator
  implements ConstraintValidator<AppenderConstraint, CategoryConfig>
{
  private String message;
  
  
  public void setMessage(final String prefix)
  {
    if (prefix.endsWith(" ")) {
      message = prefix;
    } else if (prefix.endsWith(":")) {
      message = prefix + " ";
    } else {
      message = prefix + ": ";
    }
  }

  /** {@inheritDoc} */
  public void initialize(AppenderConstraint annotation)
  {
    setMessage(annotation.message());
  }

  /** {@inheritDoc} */
  public boolean isValid(
      final CategoryConfig value,
      final ConstraintValidatorContext context)
  {
    final Set<AppenderConfig> invalid = new HashSet<AppenderConfig>();
    validateInternal(value, invalid);
    if (invalid.size() > 0) {
      if (context != null) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
            message + invalid.toString()).addNode(
            "appenders").addConstraintViolation();
      }
      return false;
    } else {
      return true;
    }
  }

  private void validateInternal(
      final CategoryConfig category,
      final Set<AppenderConfig> invalid)
  {
    if (category.getAppenders() != null) {
      for (AppenderConfig appender : category.getAppenders()) {
        if (category.getProject().getAppender(appender.getId()) == null) {
          invalid.add(appender);
        }
      }
    }
  }
}
