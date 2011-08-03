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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Closes the network socket used to send remote logging events to the server
 * when a client is removed from a project.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SocketCloseClientRemovalPolicy implements ClientRemovalPolicy
{

  /** Logger instance. */
  protected final Log logger = LogFactory.getLog(getClass());


  /** {@inheritDoc}. */
  public void clientRemoved(
    final String clientName,
    final LoggingEventHandler handler)
  {
    logger.info("Shutting down logging event handler to close client socket.");
    handler.shutdown();
  }

}
