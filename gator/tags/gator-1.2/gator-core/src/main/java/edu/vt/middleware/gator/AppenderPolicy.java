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
package edu.vt.middleware.gator;

/**
 * Strategy pattern for pluggable policy for including/excluding appenders
 * on categories.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public interface AppenderPolicy
{
  /**
   * Determines whether the given category allows the appender to be part of
   * logger configuration.
   *
   * @param category Logger category configuration.
   * @param appender Logger appender configuration.
   * @return True if appender is allowed in category, false otherwise.
   */
  boolean allow(CategoryConfig category, AppenderConfig appender);
  
  
  /**
   * Determines whether the given category is allowed to have a socket appender
   * reference to send logging events to the server.
   *
   * @param category Logger category configuration.
   * @return True if socket appender is allowed for given category,
   * false otherwise.
   */
  boolean allowSocketAppender(CategoryConfig category);
}
