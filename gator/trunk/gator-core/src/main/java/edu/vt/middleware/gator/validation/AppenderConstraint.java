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
 * Applies basic validation to the appenders defined on a logger category.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AppenderConstraintValidator.class)
public @interface AppenderConstraint
{
  /** Message that prefixes list of invalid appenders */
  String message() default "Following appenders do not belong to project: ";

  /** Validation groups */
  Class<?>[] groups() default { };

  /** Contains validation metadata */
  Class<? extends Payload>[] payload() default {};
}
