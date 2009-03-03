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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.springframework.util.Assert;

import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ConfigChangeListener;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Log4j server that accepts connections from clients publishing log events via
 * the log4j SocketAppender.  Based on the SocketServer class provided in the
 * log4j distribution, but does configuration via database instead of
 * filesystem.  Additionally this class was designed with Spring configuration
 * in mind.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class SocketServer
  implements ConfigChangeListener, SocketCloseListener, Runnable
{
  /** Default port number to listen for connections on */
  public static final int DEFAULT_PORT = 8000;
 
  /** Default bind address is loopback address */
  public static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";
 
  /** Number of ms to wait for socket server to stop */
  protected static final int STOP_TIMEOUT = 10000;

  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());
  
  /**
   * Maps clients to their repositories to facilitate updating repository
   * configuration based on project configuration changes.
   */
  protected final Map<InetAddress, LoggerRepository> clientRepoMap =
    new HashMap<InetAddress, LoggerRepository>();

  /** IP address/host name server will bind to */
  protected String bindAddress = DEFAULT_BIND_ADDRESS;
 
  /** Server bind/listing address */
  protected InetAddress inetBindAddress;

  /** Port server will listen on */
  protected int port = DEFAULT_PORT;
 
  /** Flag indicating whether to start on intialization */
  protected boolean startOnInit;
 
  /** Log4j configuration handler */
  protected Configurator configurator;

  /** Accepts incoming socket connections */
  protected ServerSocket serverSocket;
  
  /** Runs the socket server */
  protected Thread socketServerThread;



  /**
   * Sets the JDBC configurator used for log4j configuration.
   * @param Configurator to set.
   */
  public void setConfigurator(final Configurator conf)
  {
    this.configurator = conf;
  }
  
  /**
   * Gets the address to which the server socket is bound.
   * @return Bind address.
   */
  public String getBindAddress()
  {
    return bindAddress;
  }

  /**
   * Sets the bind address on which incoming connections will be accepted.
   * @param hostNameOrIP Host name or dotted IP address of bind address.
   */
  public void setBindAddress(final String hostNameOrIP)
  {
    bindAddress = hostNameOrIP;
  }

  /**
   * Gets the port on which to listen for client connections.
   * @return Listening port number.
   */
  public int getPort()
  {
    return port;
  }

  /**
   * Sets the port on which to listen for client connections.
   * @param n Listening port number.
   */
  public void setPort(final int n)
  {
    this.port = n;
  }

  /**
   * Set a flag indicating whether or not to start the server after
   * initialization via {@link #init()} is complete.
   * @param init True to start after initialization, false otherwise.
   * If false, the server must be started by an explicit call to
   * {@link #start()}.  The default is FALSE.
   */
  public void setStartOnInit(boolean startOnInit)
  {
    this.startOnInit = startOnInit;
  }

  /**
   * Initializes the socket server so it can begin accepting connections
   * from remote hosts.  If the {@link #startOnInit} flag is set, this 
   * method calls {@link #start()}; otherwise the server must be started
   * by calling {@link #start()} explicitly. 
   * @throws Exception On initialization errors.
   */
  public void init() throws Exception
  {
    Assert.notNull(configurator, "Configurator cannot be null.");
    try {
      inetBindAddress = InetAddress.getByName(bindAddress);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Unknow host " + bindAddress);
    }
    if (startOnInit) {
	    start();
    }
  }

  /**
   * Start the server and listen for incoming connections.
   * @throws IOException On failure to bind to desired port.
   */
  public void start() throws IOException
  {
    logger.info("Starting SocketServer...");
    logger.info(String.format("Listening on %s:%s", inetBindAddress, port));
    serverSocket = new ServerSocket(port, 0, inetBindAddress);
    socketServerThread = new Thread(this);
    socketServerThread.start();
  }
 
  /**
   * Stops the socket server from accepting incoming connections.
   */
  public void stop()
  {
    if (!socketServerThread.isAlive()) {
      logger.info("Socket server is already stopped.");
      return;
    }
    logger.info("Stopping socket server...");
    if (!serverSocket.isClosed()) {
	    try {
	      serverSocket.close();
	    } catch (IOException e) {
	      logger.error("Error closing server socket.", e);
	    }
    }
    try {
      socketServerThread.join(STOP_TIMEOUT);
    } catch (InterruptedException e) {
      logger.warn("Interrupted waiting for socker server thread to finish.");
    }
    serverSocket = null;
    logger.info("Socket server stopped.");
  }

  /** {@inheritDoc} */
  public void run()
  {
    while(true) {
      logger.info("Waiting to accept a new client.");
      Socket socket = null;
      InetAddress inetAddress = null;
      try {
        socket = serverSocket.accept();
        inetAddress =  socket.getInetAddress();
        logger.info("Accepted connection from client " + inetAddress);
        LoggerRepository repo = null;
        if (clientRepoMap.containsKey(inetAddress)) {
	        logger.info("Using existing cached logger repository for client.");
          repo = clientRepoMap.get(inetAddress);
        } else {
	        logger.info("Creating new logger repository for client.");
          repo = new Hierarchy(new RootLogger(Level.ALL));
	        clientRepoMap.put(inetAddress, repo);
        }
        configurator.configure(inetAddress, repo);
        logger.info("Logger repository configured successfully.");
        final LoggingEventHandler handler =
          new LoggingEventHandler(socket, repo);
        handler.getSocketCloseListeners().add(this);
        new LoggingEventHandlerThread(handler, inetAddress).start();
      } catch(UnknownClientException e) {
        logger.warn("Unknown client " + inetAddress +
          " connected but was rejected.");
        if (socket != null && !socket.isClosed()) {
          logger.info("Closing socket for rejected host.");
          try {
            socket.close();
          } catch (IOException ioex) {
            logger.error("Error closing client socket.", ioex);
          }
        }
      } catch(SocketException e) {
        // May have received this due to a stop() invocation.
        // Check whether socket is closed and abort.
        if (serverSocket == null || serverSocket.isClosed()) {
          logger.info("No listening socket available. Quitting.");
          return;
        } else {
          logger.error(e);
        }
      } catch(Exception e) {
        logger.error(e);
        if (serverSocket == null || serverSocket.isClosed()) {
          logger.info("No listening socket available. Quitting.");
          return;
        }
      }
    }
  }

  /** {@inheritDoc} */
  public synchronized void projectChanged(
      final Object sender, final ProjectConfig project)
  {
    logger.info(String.format("Got notice that %s has changed.", project));
    for (InetAddress addr : clientRepoMap.keySet()) {
      for (ClientConfig client : project.getClients()) {
        if (addr.getHostName().equals(client.getName()) ||
            addr.getHostAddress().equals(client.getName()))
        {
          try {
            configurator.configure(project, clientRepoMap.get(addr));
          } catch (ConfigurationException e) {
            logger.error(String.format(
                "Error updating configuration for %s.", project), e);
          }
        }
      }
    }
  }

  /** {@inheritDoc} */
  public synchronized void projectRemoved(
      final Object sender, final ProjectConfig project)
  {
    logger.info(String.format("Got notice that %s was removed.", project));
  }

  /** {@inheritDoc} */
  public synchronized void socketClosed(Object sender, Socket socket)
  {
    logger.info(String.format("Got notification of closed socket %s", socket));
    final InetAddress addr = socket.getInetAddress();
    if (clientRepoMap.containsKey(addr)) {
      logger.info(String.format(
          "Removing cached logger repository for %s", addr));
	    clientRepoMap.remove(addr);
    }
  }
}
