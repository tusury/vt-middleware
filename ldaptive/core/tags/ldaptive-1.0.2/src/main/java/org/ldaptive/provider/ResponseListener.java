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

import org.ldaptive.Response;
import org.ldaptive.async.AsyncRequest;

/**
 * Response listener.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ResponseListener
{


  /**
   * Invoked when an asynchronous operation has begun.
   *
   * @param  request  to abandon this operation
   */
  void asyncRequestReceived(AsyncRequest request);


  /**
   * Invoked when a response is received from a provider indicating the
   * operation has completed.
   *
   * @param  response  containing the result
   */
  void responseReceived(Response<Void> response);


  /**
   * Invoked when an exception is thrown from a provider indicating the
   * operation cannot be completed.
   *
   * @param  exception  thrown from the async operation
   */
  void exceptionReceived(Exception exception);
}
