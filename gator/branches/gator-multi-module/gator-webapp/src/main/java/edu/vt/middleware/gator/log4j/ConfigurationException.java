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
 * Describes Log4j configuration errors.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class ConfigurationException extends Exception
{
  /** ConfigurationException.java */
  private static final long serialVersionUID = -4833363886386073180L;

  /**
   * Creates a new instance with the given error text message.
   * @param msg Error text.
   */
  public ConfigurationException(final String msg)
  {
    super(msg);
  }

  /**
   * Creates a new instance with the given causing exception.
   * @param cause Cause of this exception.
   */
  public ConfigurationException(final Throwable cause)
  {
    super(cause);
  }

  /**
   * Creates a new instance with the given error text message and cause.
   * @param msg Error text.
   * @param cause Cause of this exception.
   */
  public ConfigurationException(final String msg, final Throwable cause)
  {
    super(msg, cause);
  }
}
