/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
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
 * Provides handling of operation responses.
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


  /** {@inheritDoc} */
  @Override
  HandlerResult<Response<T>> handle(
    Connection conn,
    Q request,
    Response<T> response)
    throws LdapException;
}
