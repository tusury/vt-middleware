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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Ensures the appenders defined on a category meet the appender policy
 * defined for the owning project.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AppenderPolicyConstraintValidator.class)
public @interface AppenderPolicyConstraint
{
  /** Message that prefixes the list of invalid appenders */
  String message() default 
    "Following appenders do not satisfy project appender policy: ";

  /** Validation groups */
  Class<?>[] groups() default { };

  /** Contains validation metadata */
  Class<? extends Payload>[] payload() default {};
}
