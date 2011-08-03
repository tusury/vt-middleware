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

/**
 * General logging engine exception.
 *
 * @author  Marvin S. Addison
 * @version  $Revision: $
 *
 */
public class EngineException extends Exception
{
  /** EngineException.java */
  private static final long serialVersionUID = 4063082188465188814L;

  /**
   * Creates a new instance with the given error text message.
   *
   * @param  msg  Error text.
   */
  public EngineException(final String msg)
  {
    super(msg);
  }

  /**
   * Creates a new instance with the given causing exception.
   *
   * @param  cause  Cause of this exception.
   */
  public EngineException(final Throwable cause)
  {
    super(cause);
  }

  /**
   * Creates a new instance with the given error text message and cause.
   *
   * @param  msg  Error text.
   * @param  cause  Cause of this exception.
   */
  public EngineException(final String msg, final Throwable cause)
  {
    super(msg, cause);
  }

}