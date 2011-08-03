/*
  $Id: $

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.server;

import java.net.InetAddress;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.server.status.ClientStatus;

/**
 * Service provider interface for logging engines.
 * <p>
 * IMPORTANT: Implementers must ensure all methods of this class are thread
 * safe.
 *
 * @author  Marvin S. Addison
 * @version  $Revision: $
 *
 */
public interface LoggingEngine
{
  /**
   * Performs initialization and startup functions for underlying logging
   * engine, if any.
   * 
   * @throws  EngineException  On fatal startup errors.
   */
  void start() throws EngineException;


  /**
   * Determines whether the logging engine supports the given event.
   *
   * @param  event  Logging event.
   *
   * @return  True if engine can handle event, false otherwise.
   */
  boolean supports(Object event);
  

  /**
   * Registers the given client with the engine and associates the given
   * project configuration with the client.  Implementers MUST perform any
   * requisite initialization and configuration tasks associated with client
   * such that the client may send logging events after this method is called.
   *
   * @param  client  Client to register.
   * @param  project  Project to associate with client.
   *
   * @throws  EngineException  On errors.
   */
  void register(InetAddress client, ProjectConfig project)
      throws EngineException;
  

  /**
   * Configures the engine to log events according to the given configuration.
   *
   * @param  project  Project configuration to apply to engine.
   *
   * @throws  ConfigurationException  On configuration errors.
   */
  void configure(ProjectConfig project) throws ConfigurationException;


  /**
   * Configures engine-specific details about the state of the given client.
   *
   * @param  clientStatus  Container for client status information.
   */
  void configure(ClientStatus clientStatus);


  /**
   * Handles the given logging event from a remote host, presumably by sending
   * it to configured handlers (appenders in log4j and logback parlance).
   *
   * @param  sender  Address of sender.
   * @param  event  Logging event to handle.
   *
   * @throws  EngineException  On errors.
   */
  void handleEvent(InetAddress sender, Object event) throws EngineException;


  /**
   * Handles any cleanup tasks required by the underlying logging engine.
   */
  void shutdown();


  /**
   * Provides a descriptive string for the log event.
   *
   * @param  event  Logging event.
   *
   * @return  String representation of event. The string representation
   * should be similar to that which is logged.
   */
  String toString(Object event);
}
