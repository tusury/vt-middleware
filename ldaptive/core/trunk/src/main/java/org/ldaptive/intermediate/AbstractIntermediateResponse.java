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
package org.ldaptive.intermediate;

import org.ldaptive.LdapUtils;
import org.ldaptive.control.ResponseControl;

/**
 * Base class for ldap intermediate response messages.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractIntermediateResponse
  implements IntermediateResponse
{

  /** control oid. */
  private final String oid;

  /** response controls. */
  private final ResponseControl[] responseControls;

  /** message ID. */
  private final int messageId;


  /**
   * Creates a new abstract intermediate response.
   *
   * @param  id  OID of this message
   * @param  c  response controls
   * @param  i  message id
   */
  public AbstractIntermediateResponse(
    final String id,
    final ResponseControl[] c,
    final int i)
  {
    oid = id;
    responseControls = c;
    messageId = i;
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return oid;
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
  public boolean equals(final Object o)
  {
    return LdapUtils.areEqual(this, o);
  }


  /**
   * Returns the hash code for this object.
   *
   * @return  hash code
   */
  @Override
  public abstract int hashCode();
}
