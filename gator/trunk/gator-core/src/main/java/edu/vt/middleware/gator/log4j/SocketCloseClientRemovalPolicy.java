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
public class SocketCloseClientRemovalPolicy implements ClientRemovalPolicy 
{
  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());
  
  /** Default number of milliseconds to wait for event handler shutdown */
  protected static final int DEFAULT_WAIT_MS = 10000;
 
  /** Number of milliseconds to wait for event handler shutdown */
  private int maxShutdownWaitTime = DEFAULT_WAIT_MS;


  /**
   * @param  maxWaitMilliseconds  Maximum number of milliseconds to wait for
   * logging event handler to shutdown and close logger repository.
   */
  public void setMaxShutdownWaitTime(final int maxWaitMilliseconds)
  {
    this.maxShutdownWaitTime = maxWaitMilliseconds;
  }


  /** {@inheritDoc} */
  public void clientRemoved(
      final String clientName,
      final LoggingEventHandler handler)
  {
    logger.info("Shutting down logging event handler to close client socket.");
    handler.shutdown();
    try {
      handler.getRunner().join(maxShutdownWaitTime);
	    logger.info("Logging event handler shutdown completed successfully.");
    } catch (InterruptedException e) {
      logger.warn("Timed out waiting for LoggingEventHandler shutdown");
    } catch (Exception e) {
      logger.warn("Error on logging event handler shutdown: " + e.getMessage());
    }
  }

}
