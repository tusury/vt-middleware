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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Collects and contains a finite number of logging events in a blocking
 * queue and exposes the queue to be consumed by other components.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class LoggingEventCollector implements LoggingEventListener
{
  /** Internal blocking queue */
  private final BlockingQueue<LoggingEvent> eventQueue;
 
  /**
   * Flag that indicates whether to throw an exception if an event is
   * received when the buffer is full.
   */
  private boolean throwOnFull;
 
 
  /**
   * Creates a new instance with the given capacity.
   * @param capacity Maximum number of logging events that can be held.
   */
  public LoggingEventCollector(final int capacity)
  {
    eventQueue = new ArrayBlockingQueue<LoggingEvent>(capacity);
  }

  /**
   * @return the throwOnFull
   */
  public boolean isThrowOnFull()
  {
    return throwOnFull;
  }

  /**
   * Sets the flag that determines behavior on a full buffer condition.
   * @param doThrow Set to true to throw an exception when an event is received
   *  on a full buffer condition, false to silently ignore it.
   */
  public void setThrowOnFull(final boolean doThrow)
  {
    this.throwOnFull = doThrow;
  }

  /**
   * Gets the blocking queue that contains collected logging events.
   * @return Queue containing collected events.
   */
  public BlockingQueue<LoggingEvent> getEventQueue()
  {
    return eventQueue;
  }

  /**
   * Adds the given logging event to the internal buffer if there is adequate
   * space available, otherwise the behavior is governed by
   * {@link #setThrowOnFull()}.
   * @param event Logging event to collect if space permits.
   */
  public void eventReceived(final LoggingEvent event)
  {
    if (!eventQueue.offer(event)) {
      if (throwOnFull) {
        throw new IllegalStateException(
          "Cannot add event because buffer is full.");
      }
    }
  }
}
