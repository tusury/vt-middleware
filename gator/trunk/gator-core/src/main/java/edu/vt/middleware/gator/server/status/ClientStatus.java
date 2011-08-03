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
package edu.vt.middleware.gator.server.status;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.server.LoggingEventHandler;

/**
 * Describes useful information about a connected client.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ClientStatus
{
  private final InetAddress client;

  private final Date connectedAt;
  
  private final long loggingEventCount;

  /** Name:value map of arbitrary properties. */
  private final Map<String, Object> propertyMap = new HashMap<String, Object>();
  
  private final List<LoggerStatus> loggers = new ArrayList<LoggerStatus>();
  
  private ProjectConfig project;


  /**
   * Creates a new instance from data in the given handler.
   *
   * @param  handler  Logging event handler associated with client whose status
   * is desired.
   */
  public ClientStatus(final LoggingEventHandler handler)
  {
    client = handler.getRemoteAddress();
    connectedAt = handler.getStartTime();
    loggingEventCount = handler.getLoggingEventCount();
  }


  /**
   * @return  Internet address of client.
   */
  public InetAddress getClient()
  {
    return client;
  }


  /**
   * @return  Host name or IP address of client.
   */
  public String getClientName()
  {
    return client.getHostName() == null ?
      client.getHostAddress() :
      client.getHostName();
  }


  /**
   * @return  Project with which client is associated.
   */
  public ProjectConfig getProject()
  {
    return project;
  }


  /**
   * Sets the project with which client is associated.
   *
   * @param  project  Project with which client is associated.
   */
  public void setProject(final ProjectConfig project)
  {
    this.project = project;
  }


  /**
   * @return  Date at which client originally connected.
   */
  public Date getConnectedAt()
  {
    return connectedAt;
  }


  /**
   * @return  Number of logging events received by server from this client.
   */
  public long getLoggingEventCount()
  {
    return loggingEventCount;
  }
  

  /**
   * Gets a map of engine-specific property name:value pairs.
   *
   * @return  Map of property name:value pairs.
   */
  public Map<String, Object> getPropertyMap()
  {
    return propertyMap;
  }


  /**
   * @return  List of status information about engine-specific handlers for
   * this client.
   */
  public List<LoggerStatus> getLoggers()
  {
    return loggers;
  }
}
