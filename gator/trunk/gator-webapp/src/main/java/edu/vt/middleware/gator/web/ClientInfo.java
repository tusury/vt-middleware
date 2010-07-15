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
package edu.vt.middleware.gator.web;

import java.util.Date;

import edu.vt.middleware.gator.ProjectConfig;

import org.apache.log4j.spi.LoggerRepository;

/**
 * Describes useful information about a connected client.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ClientInfo
{
  private String name;
  
  private ProjectConfig project;

  private Date connectedAt;
  
  private long loggingEventCount;

  private LoggerRepository repository;

  /**
   * @return the address
   */
  public String getName()
  {
    return name;
  }

  /**
   * @param name  Client name.
   */
  public void setName(final String name)
  {
    this.name = name;
  }

  /**
   * @return the project
   */
  public ProjectConfig getProject()
  {
    return project;
  }

  /**
   * @param project the project to set
   */
  public void setProject(final ProjectConfig project)
  {
    this.project = project;
  }

  /**
   * @return the connectedAt
   */
  public Date getConnectedAt()
  {
    return connectedAt;
  }

  /**
   * @param connectedAt the connectedAt to set
   */
  public void setConnectedAt(final Date connectedAt)
  {
    this.connectedAt = connectedAt;
  }

  /**
   * @return the loggingEventCount
   */
  public long getLoggingEventCount()
  {
    return loggingEventCount;
  }

  /**
   * @param loggingEventCount the loggingEventCount to set
   */
  public void setLoggingEventCount(final long loggingEventCount)
  {
    this.loggingEventCount = loggingEventCount;
  }

  /**
   * @return the repository
   */
  public LoggerRepository getRepository()
  {
    return repository;
  }

  /**
   * @param repository the repository to set
   */
  public void setRepository(final LoggerRepository repository)
  {
    this.repository = repository;
  }
}
