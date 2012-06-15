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

import java.util.Arrays;
import org.ldaptive.AbstractRequest;

/**
 * Contains the data required to perform an ldap who am i operation. See
 * RFC 4532.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class WhoAmIRequest extends AbstractRequest implements ExtendedRequest
{

  /** OID of this extended request. */
  public static final String OID = "1.3.6.1.4.1.4203.1.11.3";


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    return null;
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return OID;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::controls=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getControls()));
  }
}
