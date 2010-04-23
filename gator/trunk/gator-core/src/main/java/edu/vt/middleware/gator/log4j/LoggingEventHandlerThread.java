/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Middleware.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.log4j;

import java.net.InetAddress;

import org.apache.log4j.MDC;

/**
 * Extension of thread that injects information into the log4j mapped diagnostic
 * context (MDC) for the current thread about the remote host whose logging
 * events are being handled.  It allows pattern layouts to use the following
 * additional placeholders:
 * <ul>
 * <li>%X{host}</li>
 * <li>%X{ip}</li>
 * </ul>
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class LoggingEventHandlerThread extends Thread
{
  /** IP address of remote host whose events are being handled */
  private InetAddress remoteAddress;


  /**
   * Creates a new logging event handler thread for the host at the given
   * address.
   * @param target Runnable object controlled by this thread.
   * @param addr IP address of host whose logging events are being handled.
   */
  public LoggingEventHandlerThread(
    final Runnable target,
    final InetAddress addr)
  {
    super(target, "gator-handler-log4j-" + addr.getHostAddress());
    remoteAddress = addr;
  }
  
  
  /** {@inheritDoc} */
  @Override
  public void run()
  {
    MDC.put("host", remoteAddress.getHostName());
    MDC.put("ip", remoteAddress.getHostAddress());
    super.run();
  }
}
