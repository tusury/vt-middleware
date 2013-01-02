/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.async;

import java.util.Arrays;
import org.ldaptive.AbstractRequest;

/**
 * Contains the data required to perform an ldap abandon operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AbandonRequest extends AbstractRequest
{

  /** Message ID to abandon. */
  private int messageId;


  /** Default constructor. */
  public AbandonRequest() {}


  /**
   * Creates a new abandon request.
   *
   * @param  id  message id to abandon
   */
  public AbandonRequest(final int id)
  {
    messageId = id;
  }


  /**
   * Returns the message id to abandon.
   *
   * @return  message id
   */
  public int getMessageId()
  {
    return messageId;
  }


  /**
   * Sets the message id to abandon.
   *
   * @param  id  of the message to abandon
   */
  public void setMessageId(final int id)
  {
    messageId = id;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::messageId=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        messageId,
        Arrays.toString(getControls()));
  }
}
