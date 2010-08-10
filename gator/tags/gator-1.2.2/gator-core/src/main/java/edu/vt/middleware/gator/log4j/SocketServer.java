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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ConfigChangeListener;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.springframework.util.Assert;

/**
 * Log4j server that accepts connections from clients publishing log events via
 * the log4j SocketAppender. Based on the SocketServer class provided in the
 * log4j distribution, but does configuration via database instead of
 * filesystem. Additionally this class was designed with Spring configuration in
 * mind.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SocketServer
  implements ConfigChangeListener, SocketCloseListener, Runnable
{

  /** Default port number to listen for connections on. */
  public static final int DEFAULT_PORT = 8000;

  /** Default bind address is loopback address. */
  public static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";

  /** Number of ms to wait for async thread operations to stop. */
  protected static final int STOP_TIMEOUT = 10000;

  /** Logger instance. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** IP address/host name server will bind to. */
  protected String bindAddress = DEFAULT_BIND_ADDRESS;

  /** Server bind/listing address. */
  protected InetAddress inetBindAddress;

  /** Port server will listen on. */
  protected int port = DEFAULT_PORT;

  /** Maximum number of clients that can connect to server. */
  protected int maxClients;

  /** Flag indicating whether to start on intialization. */
  protected boolean startOnInit;

  /** Log4j configuration handler. */
  protected Configurator configurator;

  /** Accepts incoming socket connections. */
  protected ServerSocket serverSocket;

  /** Runs the socket server. */
  protected Thread socketServerThread;

  /** Defines behavior when clients are removed from projects. */
  protected ClientRemovalPolicy clientRemovalPolicy;

  /** Executes logging event handlers for each connected client. */
  protected LoggingEventHandlerExecutor handlerExecutor;

  /** Executor used to publish log events to registered listeners. */
  protected Executor eventExecutor;

  /** Project configuration manager. */
  protected ConfigManager configManager;

  /** Maps clients to the logging event handler that services its log events. */
  private final Map<InetAddress, LoggingEventHandler> eventHandlerMap =
    new HashMap<InetAddress, LoggingEventHandler>();

  /** Maps project to logger repository to allow 1:1 relationship */
  private final Map<ProjectConfig, LoggerRepository> repositoryMap =
    new HashMap<ProjectConfig, LoggerRepository>();
  
  /** This is initialized on application startup */
  private final Date startTime = new Date();


  /** {@inheritDoc}. */
  public void setConfigManager(final ConfigManager manager)
  {
    this.configManager = manager;
  }

  /**
   * Sets the JDBC configurator used for log4j configuration.
   *
   * @param  Configurator  to set.
   */
  public void setConfigurator(final Configurator conf)
  {
    this.configurator = conf;
  }

  /**
   * Gets the address to which the server socket is bound.
   *
   * @return  Bind address.
   */
  public String getBindAddress()
  {
    return bindAddress;
  }

  /**
   * Sets the bind address on which incoming connections will be accepted.
   *
   * @param  hostNameOrIP  Host name or dotted IP address of bind address.
   */
  public void setBindAddress(final String hostNameOrIP)
  {
    bindAddress = hostNameOrIP;
  }

  /**
   * Gets the port on which to listen for client connections.
   *
   * @return  Listening port number.
   */
  public int getPort()
  {
    return port;
  }

  /**
   * Sets the port on which to listen for client connections.
   *
   * @param  n  Listening port number.
   */
  public void setPort(final int n)
  {
    port = n;
  }

  /**
   * Gets the maximum number of logging clients allowed by this server.
   *
   * @return Maximum number of allowed clients.
   */
  public int getMaxClients()
  {
    return this.maxClients;
  }

  /**
   * Sets the maximum number of logging clients allowed by this server.
   *
   * @param  max  Maximum number of clients.
   */
  public void setMaxClients(final int max)
  {
    this.maxClients = max;
  }

  /**
   * Gets the policy applied when clients are removed from a project.
   *
   * @return  Client removal policy.
   */
  public ClientRemovalPolicy getClientRemovalPolicy()
  {
    return clientRemovalPolicy;
  }

  /**
   * Sets the policy applied when clients are removed from a project.
   *
   * @param  policy  Client removal policy.
   */
  public void setClientRemovalPolicy(final ClientRemovalPolicy policy)
  {
    clientRemovalPolicy = policy;
  }

  /**
   * Gets a collection of all registered logging event handlers.
   *
   * @return  Immutable collection of logging event handlers.
   */
  public Collection<LoggingEventHandler> getLoggingEventHandlers()
  {
    return Collections.unmodifiableCollection(eventHandlerMap.values());
  }

  /**
   * Gets the logging event handler for the given client.
   * 
   * @param  hostNameOrIp  Host name or IP address of client.
   *
   * @return  Logging event handler for given client or null if no handler is
   * found for given client.
   */
  public LoggingEventHandler getLoggingEventHandler(final String hostNameOrIp)
  {
    for (InetAddress address : eventHandlerMap.keySet()) {
      if (address.getHostName().equals(hostNameOrIp) ||
	      address.getHostAddress().equals(hostNameOrIp)) {
        return eventHandlerMap.get(address);
      }
    }
    return null;
  }

  /**
   * Gets first project to which the host possessing the given IP address is a
   * member.
   *
   * @param  addr  IP address.
   *
   * @return  First project to which the client at the given IP address is a
   * member or null if client does not belong to project.
   */
  public ProjectConfig getProject(final InetAddress addr)
  {
    ProjectConfig project = null;
    List<ProjectConfig> projects =
      configManager.findProjectsByClientName(addr.getHostName());
    if (projects.size() > 0) {
      project = projects.get(0);
    } else {
	    projects = configManager.findProjectsByClientName(addr.getHostAddress());
	    if (projects.size() > 0) {
	      project = projects.get(0);
	    }
    }
    return project;
  }

  /**
   * Set a flag indicating whether or not to start the server after
   * initialization via {@link #init()} is complete.
   *
   * @param  init  True to start after initialization, false otherwise. If
   * false, the server must be started by an explicit call to {@link #start()}.
   * The default is FALSE.
   */
  public void setStartOnInit(boolean startOnInit)
  {
    this.startOnInit = startOnInit;
  }

  /** @param  executor  Executor service used to publish events. */
  public void setEventExecutor(final Executor executor)
  {
    this.eventExecutor = executor;
  }

  /**
   * @return  Date/time of server startup.
   */
  public Date getStartTime()
  {
    return this.startTime;
  }

  /**
   * Initializes the socket server so it can begin accepting connections from
   * remote hosts. If the {@link #startOnInit} flag is set, this method calls
   * {@link #start()}; otherwise the server must be started by calling {@link
   * #start()} explicitly.
   *
   * @throws  Exception  On initialization errors.
   */
  public void init()
    throws Exception
  {
    Assert.notNull(configManager, "ConfigManager cannot be null.");
    Assert.notNull(configurator, "Configurator cannot be null.");
    Assert.notNull(clientRemovalPolicy, "ClientRemovalPolicy cannot be null.");
    Assert.notNull(eventExecutor, "EventExecutor cannot be null.");
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
   *
   * @throws  IOException  On failure to bind to desired port.
   */
  public void start()
    throws IOException
  {
    final String listenIP = inetBindAddress.getHostAddress();
    logger.info("Starting SocketServer...");
    logger.info(String.format("Listening on %s:%s", listenIP, port));
    serverSocket = new ServerSocket(port, 0, inetBindAddress);
    socketServerThread = new Thread(
      this,
      String.format("gator-server-%s-%s", listenIP, port));
    handlerExecutor = new LoggingEventHandlerExecutor();
    socketServerThread.start();
    logger.info("Socket server started successfully.");
  }

  /**
   * Stops the socket server from accepting incoming connections and cleans up
   * resources for handling logging events.
   */
  public void stop()
  {
    // Multiple invocations of stop() are safe
    if (socketServerThread == null) {
      logger.info("Socket server is already stopped.");
      return;
    }
    logger.info("Stopping socket server...");

    try {
      socketServerThread.join(STOP_TIMEOUT);
    } catch (InterruptedException e) {
      logger.warn("Times out waiting for socker server thread to finish.");
    }

    if (!serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        logger.error("Error closing server socket.", e);
      }
    }

    handlerExecutor.shutdown();

    eventHandlerMap.clear();
    serverSocket = null;
    socketServerThread = null;
    logger.info("Socket server stopped.");
  }

  /** {@inheritDoc}. */
  public void run()
  {
    while (serverSocket != null && serverSocket.isBound()) {
      logger.info("Waiting to accept a new client.");

      Socket socket = null;
      InetAddress inetAddress = null;
      try {
        socket = serverSocket.accept();
        inetAddress = socket.getInetAddress();
        
        // Validate newly-connected client
        if (eventHandlerMap.keySet().size() >= maxClients) {
          throw new UnauthorizedClientException(
            inetAddress, "Maximum number of clients exceeded.");
        }
        final ProjectConfig project = getProject(inetAddress);
        if (project == null) {
	        throw new UnauthorizedClientException(
            inetAddress, "Client not registered with any projects.");
        }

        // Explicitly enable TCP keep alives to try to help reclaim resources
        // from dead clients
        socket.setKeepAlive(true);

        logger.info("Accepted connection from client " + inetAddress);
        logger.info("Configuring logger repository for " + inetAddress);
        final LoggerRepository repo = getLoggerRepository(project);
        configurator.configure(project, repo);
        logger.info("Logger repository configured successfully.");
        final LoggingEventHandler handler = new LoggingEventHandler(
          socket,
          repo,
          eventExecutor);
        handler.getSocketCloseListeners().add(this);
        eventHandlerMap.put(inetAddress, handler);
        handlerExecutor.execute(handler);
      } catch (UnauthorizedClientException e) {
        logger.warn(
          String.format(
            "Unauthorized client %s rejected for reason: " + e.getMessage(),
            e.getClient()));
        if (socket != null && !socket.isClosed()) {
          logger.info("Closing socket for rejected host.");
          try {
            socket.close();
          } catch (IOException ioex) {
            logger.error("Error closing client socket.", ioex);
          }
        }
      } catch (SocketException e) {
        // Check whether this is caused by a stop() invocation:
        // calling stop() closes server socket, which throws SocketException
        // from blocking accept() call
        if (serverSocket == null) {
          logger.info("Ignoring SocketException caused by stop() invocation.");
        } else {
          logger.error(e);
        }
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }

  /** {@inheritDoc}. */
  public synchronized void projectChanged(
    final Object sender,
    final ProjectConfig project)
  {
    logger.info(String.format("Got notice that %s has changed.", project));
    try {
      logger.info("Reconfiguring logger repository for " + project);
      configurator.configure(project, getLoggerRepository(project));
    } catch (ConfigurationException e) {
      logger.error("Error updating configuration for " + project, e);
    }
  }

  /** {@inheritDoc}. */
  public synchronized void projectRemoved(
    final Object sender,
    final ProjectConfig project)
  {
    logger.info(String.format("Got notice that %s was removed.", project));
    for (ClientConfig client : project.getClients()) {
	    final LoggingEventHandler handler =
	      getLoggingEventHandler(client.getName());
      if (handler != null) {
        clientRemovalPolicy.clientRemoved(client.getName(), handler);
      }
    }
  }

  /** {@inheritDoc}. */
  public synchronized void clientRemoved(
    final Object sender,
    final ProjectConfig project,
    final String clientName)
  {
    logger.info(
      String.format(
        "Got notice that client %s was removed from %s.",
        clientName,
        project));
    final LoggingEventHandler handler = getLoggingEventHandler(clientName);
    if (handler != null) {
      clientRemovalPolicy.clientRemoved(clientName, handler);
    }
  }

  /** {@inheritDoc}. */
  public synchronized void socketClosed(
    final Object sender,
    final Socket socket)
  {
    logger.info("Got notification of closed socket " + socket);

    final InetAddress addr = socket.getInetAddress();
    if (eventHandlerMap.containsKey(addr)) {
      logger.info(
        String.format(
          "Cleaning up resources held by %s due to socket close.",
          addr));
      eventHandlerMap.get(addr).shutdown();
      eventHandlerMap.remove(addr);
    }
  }

  /**
   * Gets the logger repository for the given project.
   * 
   * @param  project  Project configuration.
   * 
   * @return  The logger repository associated with the given project or a new
   * logger repository if none exists.
   */
  protected LoggerRepository getLoggerRepository(final ProjectConfig project)
  {
    LoggerRepository repo = repositoryMap.get(project);
    if (repo == null) {
      repo = new Hierarchy(new RootLogger(Level.ALL));
      repositoryMap.put(project, repo);
    }
    return repo;
  }
}
