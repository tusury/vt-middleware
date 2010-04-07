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

import java.util.Set;

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
  extends AbstractAppenderConstraintValidator<AppenderConstraint>
{

  /** {@inheritDoc} */
  public void initialize(AppenderConstraint annotation)
  {
    setMessage(annotation.message());
  }


  protected void validateInternal(
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
