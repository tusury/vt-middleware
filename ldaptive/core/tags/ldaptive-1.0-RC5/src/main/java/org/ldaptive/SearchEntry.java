/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import java.util.Arrays;
import org.ldaptive.control.ResponseControl;

/**
 * Simple bean representing a search entry.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchEntry extends LdapEntry implements ResponseMessage
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 347;

  /** response controls. */
  private final ResponseControl[] responseControls;

  /** message ID. */
  private final int messageId;


  /**
   * Creates a new search entry.
   *
   * @param  id  message id
   * @param  c  response controls
   */
  public SearchEntry(final int id, final ResponseControl[] c)
  {
    messageId = id;
    responseControls = c;
  }


  /**
   * Creates a new search entry.
   *
   * @param  id  message id
   * @param  c  response controls
   * @param  sb  sort behavior
   */
  public SearchEntry(
    final int id,
    final ResponseControl[] c,
    final SortBehavior sb)
  {
    super(sb);
    messageId = id;
    responseControls = c;
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl[] getControls()
  {
    return responseControls;
  }


  /** {@inheritDoc} */
  @Override
  public int getMessageId()
  {
    return messageId;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtils.computeHashCode(
        HASH_CODE_SEED,
        getDn() != null ? getDn().toLowerCase() : null,
        getAttributes(),
        messageId,
        responseControls);
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[dn=%s%s, responseControls=%s, messageId=%s]",
        getDn(),
        getAttributes(),
        Arrays.toString(responseControls),
        messageId);
  }
}
