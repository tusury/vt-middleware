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

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Base class for all validators that perform JSR-303 bean validation in
 * addition to other validation operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractAnnotationValidator implements Validator
{
  @Autowired @NotNull
  private javax.validation.Validator validator;


  /**
   * Performs JSR-303 bean validation.
   *
   * @param  bean
   * @param  errors
   */
  /** {@inheritDoc}. */
  public void validate(final Object target, final Errors errors)
  {
    for (ConstraintViolation<Object> v : validator.validate(target)) {
      errors.rejectValue(v.getPropertyPath().toString(), "", v.getMessage());
    }
  }
}
