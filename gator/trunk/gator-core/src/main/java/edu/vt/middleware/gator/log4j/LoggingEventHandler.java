/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.log4j;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Copied and extended from {@link SocketNode} class.  This class has the
 * added feature of publishing events about closing client sockets.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class LoggingEventHandler implements Runnable
{
  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Subscribers that get notified when logging events are received */
  protected final Set<LoggingEventListener> loggingEventListeners =
    new HashSet<LoggingEventListener>();

  /** Subscribers that get notified when underlying socket closes */
  protected final Set<SocketCloseListener> socketCloseListeners =
    new HashSet<SocketCloseListener>();

  protected boolean isRunning;

  protected Socket socket;
  
  protected LoggerRepository repository;
 
  protected Executor executor;


  /**
   * Creates new instance that accepts logging events as serialized
   * {@link LoggingEvent} objects from the given socket, and writes events
   * to the given logger repository.
   * @param socket Socket to read from.
   * @param repo Source of loggers to write to.
   * @param eventExecutor Responsible for sending events to registered
   * listeners.
   */
  public LoggingEventHandler(
      final Socket socket,
      final LoggerRepository repo,
      final Executor eventExecutor)
  {
    if (socket == null) {
      throw new IllegalArgumentException("Socket cannot be null.");
    }
    if (repo == null) {
      throw new IllegalArgumentException("LoggerRepository cannot be null.");
    }
    if (eventExecutor == null) {
      throw new IllegalArgumentException("ExecutorService cannot be null.");
    }
    this.socket = socket;
    this.repository = repo;
    this.executor = eventExecutor;
  }

  /**
   * @return the loggingEventListeners
   */
  public Set<LoggingEventListener> getLoggingEventListeners()
  {
    return loggingEventListeners;
  }

  /**
   * @return the socketCloseListeners
   */
  public Set<SocketCloseListener> getSocketCloseListeners()
  {
    return socketCloseListeners;
  }
 
  /**
   * @return the repository
   */
  public LoggerRepository getRepository()
  {
    return repository;
  }
  
  /**
   * Gets the socket used to read logging events from remote clients.
   * @return Socket used to read logging events.
   */
  public Socket getSocket()
  {
    return socket;
  }
 
  /**
   * Gets the IP address of the remote host for which this handler services
   * log events.
   * @return IP address.
   */
  public InetAddress getRemoteAddress()
  {
    return socket.getInetAddress();
  }

  /**
   * Shuts down the loop that handles logging event messages.
   */
  public void shutdown()
  {
    isRunning = false;
    closeSocketIfNecessary();
  }

  /** {@inheritDoc} */
  public void run() {
    isRunning = true;
    logger.info("Ready to handle remote logging events from socket.");
    final Layout eventTraceLayout = new TTCCLayout();
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(
          new BufferedInputStream(socket.getInputStream()));
      while(isRunning) {
        final LoggingEvent event = (LoggingEvent) ois.readObject();
        if (logger.isTraceEnabled()) {
          logger.info("Read logging event from socket: " +
              eventTraceLayout.format(event));
        }
        final Logger serverLogger =
          repository.getLogger(event.getLoggerName());
        final Level level = serverLogger.getEffectiveLevel();
        if(event.getLevel().isGreaterOrEqual(level)) {
          serverLogger.callAppenders(event);
        }
        // Attempt to call registered listeners
        for (LoggingEventListener listener : getLoggingEventListeners()) {
          executor.execute(new LoggingEventReceivedEvent(listener, event));
        }
      }
    } catch(EOFException e) {
      logger.info("End of stream detected. Quitting.");
    } catch(SocketException e) {
      logger.info("Underlying socket is closed. Quitting.");
    } catch(Exception e) {
      logger.error("Unexpected exception. Quitting.", e);
    } finally {
      closeStreamIfNecessary(ois);
      closeSocketIfNecessary();
      repository.shutdown();
      repository = null;
      isRunning = false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return "LoggingEventHandler for " + socket.getInetAddress();
  }
  
  private void closeStreamIfNecessary(final InputStream in)
  {
    if (in != null) {
      try {
        in.close();
      } catch(Exception e) {
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
	      } catch(Exception e) {
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
    
    private LoggingEvent event;
    
    public LoggingEventReceivedEvent(
        final LoggingEventListener listener, final LoggingEvent event)
    {
      this.listener = listener;
      this.event = event;
    }

    /** {@inheritDoc} */
    public void run()
    {
      try {
        listener.eventReceived(event);
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
        final SocketCloseListener listener, final Socket socket)
    {
      this.listener = listener;
      this.socket = socket;
    }

    /** {@inheritDoc} */
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
