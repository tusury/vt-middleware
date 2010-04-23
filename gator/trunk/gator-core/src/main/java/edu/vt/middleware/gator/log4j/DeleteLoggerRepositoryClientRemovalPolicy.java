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
import org.apache.log4j.Hierarchy;
import org.apache.log4j.spi.LoggerRepository;

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


  /** {@inheritDoc} */
  public void clientRemoved(
    final String clientName,
    final LoggingEventHandler handler)
  {
    logger.info("Deleting logger repository for client " + clientName);
    final LoggerRepository repository = handler.getRepository();
    repository.shutdown();
    if (repository instanceof Hierarchy) {
      // Clear internal storage of loggers/categories since categories may
      // have changed dramatically and we want to purge unused categories
      // for reasonable memory usage since repositories may be very long lived.
      ((Hierarchy) repository).clear();
    }
  }

}
