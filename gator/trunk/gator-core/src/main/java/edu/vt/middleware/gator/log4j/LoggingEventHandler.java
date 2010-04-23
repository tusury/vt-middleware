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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

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
  
  protected ObjectInputStream ois;
  
  protected Thread runner;
  


  /**
   * Creates new instance that accepts logging events as serialized
   * {@link LoggingEvent} objects from the given socket, and writes events
   * to the given logger repository.
   * @param socket Socket to read from.
   * @param repo Source of loggers to write to.
   */
  public LoggingEventHandler(final Socket socket, final LoggerRepository repo)
  {
    if (socket == null) {
      throw new IllegalArgumentException("Socket cannot be null.");
    }
    if (repo == null) {
      throw new IllegalArgumentException("LoggerRepository cannot be null.");
    }
    this.socket = socket;
    this.repository = repo;
    try {
      ois = new ObjectInputStream(
          new BufferedInputStream(socket.getInputStream()));
    }
    catch(IOException e) {
      throw new IllegalStateException(
        "Failed creating ObjectInputStream on " + socket,
        e);
    }
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
   * @return  Thread that executes the {@link run()} method.
   */
  public Thread getRunner()
  {
    return runner;
  }

  /**
   * Shuts down the loop that handles logging event messages.
   */
  public void shutdown()
  {
    isRunning = false;
    closeStreamIfNecessary();
  }

  /** {@inheritDoc} */
  public void run() {
    isRunning = true;
    runner = Thread.currentThread();
    logger.info("Ready to handle remote logging events from socket.");
    final Layout eventTraceLayout = new TTCCLayout();
    try {
      while(isRunning) {
        final LoggingEvent event = (LoggingEvent) ois.readObject();
        if (logger.isTraceEnabled()) {
          logger.info("Read logging event from socket: " +
              eventTraceLayout.format(event));
        }
        final Logger remoteLogger =
          repository.getLogger(event.getLoggerName());
        final Level level = remoteLogger.getEffectiveLevel();
        if(event.getLevel().isGreaterOrEqual(level)) {
          remoteLogger.callAppenders(event);
        }
        // Attempt to call registered listeners
        for (LoggingEventListener listener : getLoggingEventListeners()) {
          try {
            listener.eventReceived(event);
          } catch (Exception e) {
            logger.error(
                "Failed executing LoggingEventListener#eventReceived() on " +
                listener,
                e);
          }
        }
      }
    } catch(EOFException e) {
      logger.info("End of stream detected. Quitting.");
    } catch(SocketException e) {
      logger.info("Underlying socket is closed. Quitting.");
    } catch(Exception e) {
      logger.error("Unexpected exception. Quitting.", e);
    } finally {
      closeStreamIfNecessary();
      closeSocketIfNecessary();
      repository.shutdown();
      repository = null;
      isRunning = false;
    }
  }
  
  private void closeStreamIfNecessary()
  {
    if (ois != null) {
      try {
        ois.close();
      } catch(Exception e) {
        logger.error("Error closing object input stream.", e);
      } finally {
        ois = null;
      }
    }
  }
  
  private void closeSocketIfNecessary()
  {
    if (socket != null) {
      try {
        logger.info("Closing client socket.");
        socket.close();
      } catch(Exception e) {
        logger.error("Error closing client socket.", e);
      }
      // We expect the socket to be unusable in any case at this point
      // which we can safely consider "closed" here
      for (SocketCloseListener listener : socketCloseListeners) {
        try {
          listener.socketClosed(this, socket);
        } catch (Exception e) {
          logger.error("Error invoking " + listener, e);
        }
      }
      socket = null;
    }
  }
}
