/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.server;

import java.net.InetAddress;

/**
 * Exception thrown when an unauthorized client attempts to connect to the
 * {@link SocketServer}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class UnauthorizedClientException extends Exception
{

  /** UnauthorizedClientException.java. */
  private static final long serialVersionUID = 671881103602771371L;

  /** IP information of unauthorized client. */
  private InetAddress client;


  /**
   * Creates a new instance for the given unauthorized client and a description
   * of the reason for rejection.
   *
   * @param  client  IP information of rejected client.
   * @param  msg  Error text.
   */
  public UnauthorizedClientException(final InetAddress client, final String msg)
  {
    super(msg);
    this.client = client;
  }

  /**
   * @return  IP information of unauthorized client that attempted to connect to
   * {@link SocketServer} but was rejected.
   */
  public InetAddress getClient()
  {
    return client;
  }
}
