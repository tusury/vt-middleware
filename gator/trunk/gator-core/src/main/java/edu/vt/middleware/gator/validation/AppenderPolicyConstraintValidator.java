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
import edu.vt.middleware.gator.AppenderPolicy;
import edu.vt.middleware.gator.CategoryConfig;

/**
 * Validates the {@link AppenderPolicyConstraint} constraint.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AppenderPolicyConstraintValidator
  extends AbstractAppenderConstraintValidator<AppenderPolicyConstraint>
{
  /** {@inheritDoc} */
  public void initialize(AppenderPolicyConstraint annotation)
  {
    setMessage(annotation.message());
  }


  protected void validateInternal(
      final CategoryConfig category,
      final Set<AppenderConfig> invalid)
  {
    final AppenderPolicy policy = category.getProject().getAppenderPolicy();
    if (category.getAppenders() != null) {
      for (AppenderConfig appender : category.getAppenders()) {
        if (!policy.allow(category, appender)) {
          invalid.add(appender);
        }
      }
    }
  }
}
