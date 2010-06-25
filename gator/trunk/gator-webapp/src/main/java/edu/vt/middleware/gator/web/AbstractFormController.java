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
package edu.vt.middleware.gator.web;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Base class for all form controllers that operate on projects.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractFormController extends AbstractController
{

  /**
   * Registers custom editors used throughout all form controllers.
   *
   * @param  binder  Data binder
   */
  @InitBinder
  public void initCommonEditors(final WebDataBinder binder)
  {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

}
