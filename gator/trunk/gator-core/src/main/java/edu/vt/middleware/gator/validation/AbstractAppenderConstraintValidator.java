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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;

/**
 * Base class for category validators that examine appenders.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public abstract class AbstractAppenderConstraintValidator<A extends Annotation>
	implements ConstraintValidator<A, CategoryConfig>
{
  private String messagePrefix;
  
  
  public void setMessagePrefix(final String prefix)
  {
    if (prefix.endsWith(" ")) {
      messagePrefix = prefix;
    } else if (prefix.endsWith(":")) {
      messagePrefix = prefix + " ";
    } else {
      messagePrefix = prefix + ": ";
    }
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
        context.buildConstraintViolationWithTemplate(
            messagePrefix + invalid.toString()).addNode(
            "appenders").addConstraintViolation();
      }
      return false;
    } else {
      return true;
    }
  }
  
  protected abstract void validateInternal(
      CategoryConfig category, Set<AppenderConfig> invalid);
}
