/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
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
 * Describes a property that is constrained to be unique among others of some
 * arbitrary scope.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNameValidator.class)
public @interface UniqueName
{

  /** Message displayed on validation failure */
  String message() default "Name must be unique.";

  /** Validation groups */
  Class<?>[] groups() default {};

  /** Contains validation metadata */
  Class<? extends Payload>[] payload() default {};
}
