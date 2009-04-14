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

import org.springframework.validation.BindException;

/**
 * Superclass for all delete controllers.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class BaseDeleteFromController extends BaseFormController
{
  /**
   * Validates the given delete specification.
   * @param errors Form binding errors.
   * @param spec To be validated.
   * @return True if validation succeeded, false otherwise.
   */
  protected boolean validate(final BindException errors, final DeleteSpec spec)
  {
    if (spec.getConfirmationFlag()) {
      return true;
    } else {
      errors.reject(
        "error.delete.confirmation",
        "Delete confirmation required.");
      return false;
    }
  }
}
