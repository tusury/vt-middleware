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
package edu.vt.middleware.gator.web.support;

import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.ParamConfig;

/**
 * Property editor for {@link AppenderParamConfig} objects.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AppenderParamArrayEditor extends AbstractParamArrayEditor
{
  /**
   * {@inheritDoc}
   */
  @Override
  public Object getValue() {
    return paramList.toArray(new AppenderParamConfig[paramList.size()]);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(final Object value) {
    if (!AppenderParamConfig[].class.isInstance(value)) {
      throw new IllegalArgumentException(String.format(
          "%s is not an instance of ParamConfig[].", value));
    }
    paramList.clear();
    for (AppenderParamConfig param : (AppenderParamConfig[]) value) {
      paramList.add(param);
    }
    super.setValue(value);
  }


  /** {@inheritDoc} */
  protected ParamConfig newParam()
  {
    return new AppenderParamConfig();
  }

}
