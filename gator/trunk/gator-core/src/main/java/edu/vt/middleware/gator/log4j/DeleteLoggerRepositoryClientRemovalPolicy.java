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
 * Deletes the log4j {@link LoggerRepository} associated with the client
 * when it is removed from a project so that no more logging events are
 * processed on the server.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class DeleteLoggerRepositoryClientRemovalPolicy
  implements ClientRemovalPolicy
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
    logger.info("Deleting logger repository for client " + clientName);
    handler.shutdown();
    try {
      handler.getRunner().join(maxShutdownWaitTime);
    } catch (InterruptedException e) {
      logger.warn("Timed out waiting for LoggingEventHandler shutdown");
    }
  }

}
