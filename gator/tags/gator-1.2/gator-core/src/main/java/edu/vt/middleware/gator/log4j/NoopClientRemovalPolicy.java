/*
  $Id: $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.log4j;


/**
 * Do nothing when a client is removed from a project.
 *
 * @author Middleware
 * @version $Revision: $
 *
 */
public class NoopClientRemovalPolicy implements ClientRemovalPolicy
{

  /** {@inheritDoc} */
  public void clientRemoved(
    final String clientName,
    final LoggingEventHandler handler ) {}

}
