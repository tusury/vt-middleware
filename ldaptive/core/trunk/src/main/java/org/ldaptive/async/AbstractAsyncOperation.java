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
package org.ldaptive.async;

import org.ldaptive.AbstractOperation;
import org.ldaptive.Connection;
import org.ldaptive.Request;
import org.ldaptive.handler.AsyncRequestHandler;

/**
 * Base class for asynchronous ldap operations.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractAsyncOperation<Q extends Request, S>
  extends AbstractOperation<Q, S>
{

  /** Handlers to process async requests. */
  private AsyncRequestHandler[] asyncRequestHandlers;


  /**
   * Creates a new abstract async operation.
   *
   * @param  conn  to use for this operation
   */
  public AbstractAsyncOperation(final Connection conn)
  {
    super(conn);
  }


  /**
   * Returns the async request handlers.
   *
   * @return  async request handlers
   */
  public AsyncRequestHandler[] getAsyncRequestHandlers()
  {
    return asyncRequestHandlers;
  }


  /**
   * Sets the async request handlers.
   *
   * @param  handlers  async request handlers
   */
  public void setAsyncRequestHandlers(final AsyncRequestHandler... handlers)
  {
    asyncRequestHandlers = handlers;
  }
}
