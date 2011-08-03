/*
  $Id: $

  Copyright (C) 2009-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.mock;

import java.io.Serializable;
import java.util.Date;

/**
 * Mock logging event.
 *
 * @author  Marvin S. Addison
 * @version  $Revision: $
 *
 */
public class MockEvent implements Serializable
{
  /** MockEvent.java */
  private static final long serialVersionUID = 6907184835501706030L;

  /** Log event timestamp. */
  private final Date timestamp;

  /** Log event message. */
  private final String message;

  /**
   * Creates a new log event with the given message.
   *
   * @param  message  Log message.
   */
  public MockEvent(final String message)
  {
    this.message = message;
    this.timestamp = new Date();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String.format("%s INFO %s", timestamp, message);
  }
}
