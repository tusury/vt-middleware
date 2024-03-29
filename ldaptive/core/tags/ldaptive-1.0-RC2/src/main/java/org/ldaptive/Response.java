/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import java.util.Arrays;
import org.ldaptive.control.ResponseControl;

/**
 * Wrapper class for all operation responses.
 *
 * @param  <T>  type of ldap result contained in this response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Response<T> implements Message<ResponseControl>
{

  /** Operation response. */
  private final T result;

  /** Operation result code. */
  private final ResultCode code;

  /** Response controls. */
  private final ResponseControl[] controls;


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   * @param  rc  result code
   */
  public Response(final T t, final ResultCode rc)
  {
    result = t;
    code = rc;
    controls = null;
  }


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   * @param  rc  result code
   * @param  c  response controls
   */
  public Response(final T t, final ResultCode rc, final ResponseControl[] c)
  {
    result = t;
    code = rc;
    controls = c;
  }


  /**
   * Returns the result of the ldap operation.
   *
   * @return  operation result
   */
  public T getResult()
  {
    return result;
  }


  /**
   * Returns the result code of the ldap operation.
   *
   * @return  operation result code
   */
  public ResultCode getResultCode()
  {
    return code;
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl[] getControls()
  {
    return controls;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::result=%s, resultCode=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        result,
        code,
        Arrays.toString(controls));
  }
}
