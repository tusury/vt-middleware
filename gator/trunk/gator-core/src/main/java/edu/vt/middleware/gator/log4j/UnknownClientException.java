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

/**
 * Exception describing a condition where a client connects to the
 * Log4j socket server but is not registered with any projects.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class UnknownClientException extends Exception
{
  /** UnknownClientException.java */
  private static final long serialVersionUID = -1811261346121315976L;

  /**
   * Creates a new instance with the given error text message.
   * @param msg Error text.
   */
  public UnknownClientException(final String msg)
  {
    super(msg);
  }

  /**
   * Creates a new instance with the given causing exception.
   * @param cause Cause of this exception.
   */
  public UnknownClientException(final Throwable cause)
  {
    super(cause);
  }

  /**
   * Creates a new instance with the given error text message and cause.
   * @param msg Error text.
   * @param cause Cause of this exception.
   */
  public UnknownClientException(final String msg, final Throwable cause)
  {
    super(msg, cause);
  }
}
