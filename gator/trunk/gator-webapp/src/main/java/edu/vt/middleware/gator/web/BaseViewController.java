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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import edu.vt.middleware.gator.ConfigManager;

/**
 * Base class for all controllers that simply display views.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class BaseViewController
  extends ParameterizableViewController
  implements InitializingBean
{
  protected ConfigManager configManager;


  /**
   * Sets the configuration manager.
   * @param helper Configuration manager;
   */
  public void setConfigManager(final ConfigManager helper)
  {
    this.configManager = helper;
  }


  /** {@inheritDoc} */
  public void afterPropertiesSet() throws Exception
  {
    Assert.notNull(configManager, "ConfigManager is required.");
  }
}
