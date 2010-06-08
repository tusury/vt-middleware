/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
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
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public abstract class AbstractFormController extends AbstractController
{
  /**
   * Registers custom editors used throughout all form controllers.
   *
   * @param binder Data binder
   */
  @InitBinder
  public void initCommonEditors(final WebDataBinder binder)
  {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

}
