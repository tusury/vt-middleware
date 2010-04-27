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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;

/**
 * Implementation of {@link ThreadPoolExecutor} that always creates new threads
 * for handling {@link LoggingEventHandler} tasks.  The primary purpose of this
 * class is to set up the name and, if possible, {@link MDC} of each worker
 * thread.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class LoggingEventHandlerExecutor implements Executor
{
  /** Number of ms to wait for each worker thread operations to terminate */
  protected static final int STOP_TIMEOUT = 2000;

  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());

  /** List of running threads */
  private List<LoggingEventHandlerThread> runningThreads;
  
  
  /**
   * Creates a new instance.
   */
  public LoggingEventHandlerExecutor()
  {
    runningThreads = new ArrayList<LoggingEventHandlerThread>();
  }


  /** {@inheritDoc} */
  public void execute(final Runnable command)
  {
    if (!(command instanceof LoggingEventHandler)) {
      throw new IllegalArgumentException("Only LoggingEventHandler supported.");
    }
    final LoggingEventHandlerThread runner =
      new LoggingEventHandlerThread((LoggingEventHandler) command);
    runningThreads.add(runner);
    runner.start();
  }
  

  /**
   * Attempts to perform a clean shutdown of all running
   * {@link LoggingEventHandler} tasks.
   */
  public void shutdown()
  {
    for (LoggingEventHandlerThread runner : runningThreads) {
      runner.getLoggingEventHandler().shutdown();
      try {
        runner.join(STOP_TIMEOUT);
      } catch (InterruptedException e) {
        logger.warn("Timed out waiting for LoggingEventHandler shutdown");
      } catch (Exception e) {
        logger.warn("Error on logging event handler shutdown: " + e.getMessage());
      }
    }
  }


  /**
   * Extends a thread with additional thread local data about handler.
   *
   * @author Middleware
   * @version $Revision$
   *
   */
  protected class LoggingEventHandlerThread extends Thread
  {
    /** Handler run by this instance */
    private LoggingEventHandler loggingEventHandler;


    /**
     * @return The handler executed by this thread.
     */
    public LoggingEventHandler getLoggingEventHandler()
    {
      return loggingEventHandler;
    }


    /**
     * Creates a new logging event handler thread for the host at the given
     * address.
     * @param target Runnable object controlled by this thread.
     * @param addr IP address of host whose logging events are being handled.
     */
    public LoggingEventHandlerThread(final LoggingEventHandler handler)
    {
      super(handler, "gator-handler-log4j-" +
          handler.getRemoteAddress().getHostAddress());
      this.loggingEventHandler = handler;
    }
    
    
    /** {@inheritDoc} */
    @Override
    public void run()
    {
      MDC.put("host", loggingEventHandler.getRemoteAddress().getHostName());
      MDC.put("ip", loggingEventHandler.getRemoteAddress().getHostAddress());
      try {
	      super.run();
      } catch (Exception e) {
        logger.error("Runnable threw unhandled exception", e);
      } finally {
        runningThreads.remove(this);
      }
    }
  }

}
