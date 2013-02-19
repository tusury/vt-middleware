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
import org.ldaptive.Request;
import org.ldaptive.handler.Handler;
import org.ldaptive.handler.HandlerResult;

/**
 * Provides post search handling of an exception thrown by an async operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ExceptionHandler extends Handler<Request, Exception>
{


  /** {@inheritDoc} */
  @Override
  HandlerResult<Exception> handle(
    Connection conn,
    Request request,
    Exception exception);
}
