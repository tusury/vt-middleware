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
package edu.vt.middleware.gator.server;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Copied and extended from {@link SocketNode} class. This class has the added
 * feature of publishing events about closing client sockets.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LoggingEventHandler implements Runnable
{

  /** Special category name signifying a log event received by this instance. */
  public static final String LOG_EVENT_CATEGORY = "LOG_EVENT";

  /** Logger instance. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Special logger to capture LoggingEvents received by this instance. */
  protected final Log eventLogger = LogFactory.getLog(LOG_EVENT_CATEGORY);

  /** Subscribers that get notified when logging events are received. */
  protected final Set<LoggingEventListener> loggingEventListeners =
    new HashSet<LoggingEventListener>();

  /** Subscribers that get notified when underlying socket closes. */
  protected final Set<SocketCloseListener> socketCloseListeners =
    new HashSet<SocketCloseListener>();

  protected boolean isRunning;

  protected Socket socket;

  protected LoggingEngine loggingEngine;

  protected Executor executor;
  
  private final Date startTime = new Date();
  
  private long loggingEventCount;


  /**
   * Creates new instance that accepts logging events as serialized {@link
   * LoggingEvent} objects from the given socket, and writes events to the given
   * logger repository.
   *
   * @param  socket  Socket to read from.
   * @param  repo  Source of loggers to write to.
   * @param  eventExecutor  Responsible for sending events to registered
   * listeners.
   */
  public LoggingEventHandler(
    final Socket socket,
    final LoggingEngine engine,
    final Executor eventExecutor)
  {
    if (socket == null) {
      throw new IllegalArgumentException("Socket cannot be null.");
    }
    if (engine == null) {
      throw new IllegalArgumentException("LoggingEngine cannot be null.");
    }
    if (eventExecutor == null) {
      throw new IllegalArgumentException("ExecutorService cannot be null.");
    }
    this.socket = socket;
    this.loggingEngine = engine;
    this.executor = eventExecutor;
  }

  /** @return  the loggingEventListeners */
  public Set<LoggingEventListener> getLoggingEventListeners()
  {
    return loggingEventListeners;
  }

  /** @return  the socketCloseListeners */
  public Set<SocketCloseListener> getSocketCloseListeners()
  {
    return socketCloseListeners;
  }

  /** @return  the repository */
  public LoggingEngine getLoggingEngine()
  {
    return loggingEngine;
  }

  /**
   * Gets the socket used to read logging events from remote clients.
   *
   * @return  Socket used to read logging events.
   */
  public Socket getSocket()
  {
    return socket;
  }

  /**
   * Gets the IP address of the remote host for which this handler services log
   * events.
   *
   * @return  IP address.
   */
  public InetAddress getRemoteAddress()
  {
    return socket.getInetAddress();
  }

  /**
   * @return  Date/time this logging event handler started.
   */
  public Date getStartTime()
  {
    return startTime;
  }

  /**
   * @return  Total number of logging events received by this handler since
   * startup.
   */
  public long getLoggingEventCount()
  {
    return loggingEventCount;
  }

  /** Shuts down the loop that handles logging event messages. */
  public void shutdown()
  {
    isRunning = false;
    closeSocketIfNecessary();
  }

  /** {@inheritDoc}. */
  public void run()
  {
    isRunning = true;
    logger.info("Ready to handle remote logging events from socket.");

    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(
        new BufferedInputStream(socket.getInputStream()));
      while (isRunning) {
        final Object event;
        try {
          event = ois.readObject();
        } catch (ClassNotFoundException e) {
          logger.warn(String.format("Unknown logging event of type %s. " +
              "Install integration module for corresponding logging engine " +
              "to resolve this problem.",
              e.getMessage()));
          break;
        }
        loggingEventCount++;
        if (loggingEngine.supports(event)) {
          if (eventLogger.isTraceEnabled()) {
            eventLogger.info(
                "Read logging event from socket: " +
                    loggingEngine.toString(event));
          }
          loggingEngine.handleEvent(getRemoteAddress(), event);
        } else {
          logger.warn(loggingEngine + " does not support " + event);
        }

        // Attempt to call registered listeners
        for (LoggingEventListener listener : getLoggingEventListeners()) {
          executor.execute(new LoggingEventReceivedEvent(listener, event));
        }
      }
    } catch (EOFException e) {
      logger.info("End of stream detected. Quitting.");
    } catch (SocketException e) {
      logger.info("Underlying socket is closed. Quitting.");
    } catch (Exception e) {
      logger.error("Unexpected exception. Quitting.", e);
    } finally {
      closeStreamIfNecessary(ois);
      closeSocketIfNecessary();
      loggingEngine.shutdown();
      loggingEngine = null;
      isRunning = false;
    }
  }

  /** {@inheritDoc}. */
  @Override
  public String toString()
  {
    return "LoggingEventHandler for " + getRemoteAddress();
  }

  private void closeStreamIfNecessary(final InputStream in)
  {
    if (in != null) {
      try {
        in.close();
      } catch (Exception e) {
        logger.error("Error closing input stream.", e);
      }
    }
  }

  private void closeSocketIfNecessary()
  {
    if (socket != null) {
      if (!socket.isClosed()) {
        try {
          logger.info("Closing client socket.");
          socket.shutdownInput();
          socket.close();
        } catch (Exception e) {
          logger.error("Error closing client socket.", e);
        }
      }
      // We expect the socket to be unusable in any case at this point
      // which we can safely consider "closed" in this context
      for (SocketCloseListener listener : socketCloseListeners) {
        executor.execute(new SocketCloseEvent(listener, socket));
      }
      socket = null;
    }
  }

  private class LoggingEventReceivedEvent implements Runnable
  {
    private LoggingEventListener listener;

    private Object event;

    public LoggingEventReceivedEvent(
      final LoggingEventListener listener,
      final Object event)
    {
      this.listener = listener;
      this.event = event;
    }

    /** {@inheritDoc}. */
    public void run()
    {
      try {
        listener.eventReceived(LoggingEventHandler.this, event);
      } catch (Exception e) {
        logger.error("Error invoking " + listener, e);
      }
    }
  }

  private class SocketCloseEvent implements Runnable
  {
    private SocketCloseListener listener;

    private Socket socket;

    public SocketCloseEvent(
      final SocketCloseListener listener,
      final Socket socket)
    {
      this.listener = listener;
      this.socket = socket;
    }

    /** {@inheritDoc}. */
    public void run()
    {
      try {
        listener.socketClosed(LoggingEventHandler.this, socket);
      } catch (Exception e) {
        logger.error("Error invoking " + listener, e);
      }
    }
  }
}
