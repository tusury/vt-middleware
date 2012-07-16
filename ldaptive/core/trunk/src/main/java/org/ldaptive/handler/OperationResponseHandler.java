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

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;
import org.ldaptive.Response;

/**
 * Provides post processing of operation responses.
 *
 * @param  <Q>  type of ldap request
 * @param  <T>  type of ldap result contained in the response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface OperationResponseHandler<Q extends Request, T>
  extends Handler<Q, Response<T>>
{


  /**
   * Process the response from an ldap operation.
   *
   * @param  conn  the operation was performed on
   * @param  request  used to perform the operation
   * @param  response  produced from an operation
   *
   * @return  handler result
   *
   * @throws  LdapException  if an error occurs processing the operation
   * response
   */
  HandlerResult<Response<T>> process(
    Connection conn, Q request, Response<T> response)
    throws LdapException;
}
