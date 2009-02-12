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
package edu.vt.middleware.gator.support;

import edu.vt.middleware.gator.LayoutParamConfig;
import edu.vt.middleware.gator.ParamConfig;

/**
 *
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class LayoutParamArrayEditor extends AbstractParamArrayEditor
{
  /**
   * {@inheritDoc}
   */
  @Override
  public Object getValue() {
    return paramList.toArray(new LayoutParamConfig[paramList.size()]);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(final Object value) {
    if (!LayoutParamConfig[].class.isInstance(value)) {
      throw new IllegalArgumentException(String.format(
          "%s is not an instance of ParamConfig[].", value));
    }
    paramList.clear();
    for (LayoutParamConfig param : (LayoutParamConfig[]) value) {
      paramList.add(param);
    }
    super.setValue(value);
  }


  /** {@inheritDoc} */
  protected ParamConfig newParam()
  {
    return new LayoutParamConfig();
  }
}
