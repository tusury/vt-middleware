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
public class DeleteLoggerRepositoryClientRemovalPolicy implements
    ClientRemovalPolicy
{
  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());

  /** {@inheritDoc} */
  public void clientRemoved(
    final String clientName,
    final LoggingEventHandler handler)
  {
    logger.info("Deleting logger repository for client " + clientName);
    handler.getRepository().resetConfiguration();
  }

}
