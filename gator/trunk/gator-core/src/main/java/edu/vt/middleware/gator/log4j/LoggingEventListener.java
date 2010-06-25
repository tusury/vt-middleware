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

import org.apache.log4j.spi.LoggingEvent;

/**
 * For publisher/subscriber pattern handling of logging events to arbitrary
 * interested 3rd parties.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface LoggingEventListener
{

  /**
   * Handler method that will be invoked on upon receipt of the given logging
   * event.
   *
   * @param  sender  LoggingEventHandler that processed the event and is
   * rebroadcasting it to registered listeners.
   * @param  event  Logging event that was received.
   */
  void eventReceived(LoggingEventHandler sender, LoggingEvent event);
}
