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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Provides a generic representation of the status of the engine-specific
 * details of category/logger.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class LoggerStatus
{
  /** Name:value map of arbitrary properties. */
  private final Map<String, Object> propertyMap = new HashMap<String, Object>();

  private final SortedSet<String> appenders = new TreeSet<String>();
  

  /**
   * Gets an map of engine-specific property name:value pairs.
   *
   * @return  Map of property name:value pairs.
   */
  public Map<String, Object> getPropertyMap()
  {
    return propertyMap;
  }


  /**
   * Adds an appender name to the set of appenders.
   *
   * @param  name  Appender name.
   */
  public void addAppender(final String name)
  {
    appenders.add(name);
  }


  /**
   * Gets an set of appender names.
   *
   * @return Sorted set of appender names.
   */
  public Set<String> getAppenders()
  {
    return appenders;
  }

}
