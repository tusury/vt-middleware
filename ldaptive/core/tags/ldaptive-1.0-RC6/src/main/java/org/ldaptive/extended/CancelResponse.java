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
package org.ldaptive.extended;

/**
 * Contains the response from an ldap cancel operation. See RFC 3909.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CancelResponse extends AbstractExtendedResponse<Void>
{


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return null;
  }


  /** {@inheritDoc} */
  @Override
  public void decode(final byte[] encoded) {}


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return String.format("[%s@%d]", getClass().getName(), hashCode());
  }
}
