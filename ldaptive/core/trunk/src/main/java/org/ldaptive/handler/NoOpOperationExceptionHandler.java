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
package org.ldaptive.handler;

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;
import org.ldaptive.Response;

/**
 * Returns a handler result containing the response passed to {@link
 * #handle(Connection, Request, Response)}.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoOpOperationExceptionHandler<Q extends Request, S>
  implements OperationExceptionHandler<Q, S>
{


  /** {@inheritDoc} */
  @Override
  public HandlerResult<Response<S>> handle(
    final Connection conn,
    final Q request,
    final Response<S> response)
    throws LdapException
  {
    return new HandlerResult<Response<S>>(response);
  }
}
