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
package edu.vt.middleware.gator.log4j;


/**
 * Strategy pattern interface for implementing policy describing behavior on
 * removal of client from a project.
 *
 * @author  Middleware Services
 * @version  $Revision: $
 */
public interface ClientRemovalPolicy
{

  /**
   * Callback method to enforce policy on the given client when it is removed
   * from a project.
   *
   * @param  clientName  Client that was removed from a project.
   * @param  handler  The logging event handler for the given client; provides a
   * number of useful properties for data that may be used for policy
   * enforcement.
   */
  void clientRemoved(String clientName, LoggingEventHandler handler);
}
