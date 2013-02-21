/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.extended;

/**
 * Provides common implementation for extended responses.
 *
 * @param  <T>  type of response value
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractExtendedResponse<T> implements ExtendedResponse<T>
{

  /** Response value. */
  private T value;


  /** {@inheritDoc} */
  @Override
  public T getValue()
  {
    return value;
  }


  /**
   * Sets the response value for this extended operation.
   *
   * @param  t  response value
   */
  protected void setValue(final T t)
  {
    value = t;
  }
}
