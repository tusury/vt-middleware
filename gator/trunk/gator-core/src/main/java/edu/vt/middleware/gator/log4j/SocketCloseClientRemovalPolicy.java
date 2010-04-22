/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.log4j;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Closes the network socket used to send remote logging events to the server
 * when a client is removed from a project.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class SocketCloseClientRemovalPolicy
  extends DeleteLoggerRepositoryClientRemovalPolicy
{
  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());

  /** {@inheritDoc} */
  public void clientRemoved(
      final String clientName,
      final LoggingEventHandler handler)
  {
    super.clientRemoved(clientName, handler);
    try {
      logger.info("Closing socket for client " + clientName);
      handler.getSocket().close();
    } catch (IOException e) {
      logger.error("Caught IOException on socket close: " + e);
    }
  }

}
