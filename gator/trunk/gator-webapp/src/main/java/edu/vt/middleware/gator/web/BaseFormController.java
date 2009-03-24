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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.vt.middleware.gator.ConfigManager;

/**
 * Base class for all form controllers that operate on projects.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class BaseFormController
  extends SimpleFormController
  implements InitializingBean
{
  protected ConfigManager configManager;


  /**
   * Sets the configuration manager.
   * @param manager Configuration manager;
   */
  public void setConfigManager(final ConfigManager manager)
  {
    this.configManager = manager;
  }


  /** {@inheritDoc} */
  public void afterPropertiesSet() throws Exception
  {
    Assert.notNull(configManager, "ConfigManager is required.");
  }


  /** {@inheritDoc} */
  @Override
  protected void initBinder(HttpServletRequest request,
      ServletRequestDataBinder binder) throws Exception
  {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }
}
