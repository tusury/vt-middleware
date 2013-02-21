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
package org.ldaptive.provider;

import org.ldaptive.control.RequestControl;
import org.ldaptive.control.ResponseControl;

/**
 * Handles provider specific request and response controls.
 *
 * @param  <T>  type of provider control
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ControlHandler<T>
{


  /**
   * Returns the OID of the supplied control.
   *
   * @param  control  to return oid for
   *
   * @return  control oid
   */
  String getOID(T control);


  /**
   * Converts the supplied control to a provider specific request control.
   *
   * @param  requestControl  to convert
   *
   * @return  provider specific controls
   */
  T handleRequest(RequestControl requestControl);


  /**
   * Converts the supplied provider control to a response control.
   *
   * @param  responseControl  to convert
   *
   * @return  control
   */
  ResponseControl handleResponse(T responseControl);
}
