/*
  $Id: AsyncRequestHandler.java 2593 2013-01-29 20:21:24Z dfisher $

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2593 $
  Updated: $Date: 2013-01-29 15:21:24 -0500 (Tue, 29 Jan 2013) $
*/
package org.ldaptive.async.handler;

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;
import org.ldaptive.async.AsyncRequest;
import org.ldaptive.handler.Handler;
import org.ldaptive.handler.HandlerResult;

/**
 * Provides post processing of an ldap async request.
 *
 * @author  Middleware Services
 * @version  $Revision: 2593 $ $Date: 2013-01-29 15:21:24 -0500 (Tue, 29 Jan 2013) $
 */
public interface AsyncRequestHandler extends Handler<Request, AsyncRequest>
{


  /** {@inheritDoc} */
  @Override
  HandlerResult<AsyncRequest> process(
    Connection conn,
    Request request,
    AsyncRequest asyncRequest)
    throws LdapException;
}
