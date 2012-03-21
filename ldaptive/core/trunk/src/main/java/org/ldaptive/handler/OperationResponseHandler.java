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
package org.ldaptive.handler;

import org.ldaptive.Response;

/**
 * Provides post processing of operation responses.
 *
 * @param  <T>  type of ldap result contained in the response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface OperationResponseHandler<T>
{


  /**
   * Process the response from an ldap operation.
   *
   * @param  response  produced from an operation
   */
  void process(Response<T> response);
}
