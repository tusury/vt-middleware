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
package org.ldaptive.async.handler;

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;
import org.ldaptive.async.AsyncRequest;
import org.ldaptive.handler.Handler;
import org.ldaptive.handler.HandlerResult;

/**
 * Provides post search handling of an ldap async request.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface AsyncRequestHandler extends Handler<Request, AsyncRequest>
{


  /** {@inheritDoc} */
  @Override
  HandlerResult<AsyncRequest> handle(
    Connection conn,
    Request request,
    AsyncRequest asyncRequest)
    throws LdapException;
}
