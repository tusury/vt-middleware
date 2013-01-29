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
import org.ldaptive.async.AsyncRequest;

/**
 * Provides post processing of an ldap async request.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
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
