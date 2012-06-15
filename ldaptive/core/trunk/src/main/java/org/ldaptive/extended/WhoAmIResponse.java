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

import java.nio.ByteBuffer;
import org.ldaptive.asn1.OctetStringType;

/**
 * Contains the response from an ldap who am i operation. See RFC 4532.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class WhoAmIResponse implements ExtendedResponse
{

  /** Authorization identity. */
  private String authzId;


  /** Default constructor. */
  public WhoAmIResponse() {}


  /**
   * Creates a new who am i response.
   *
   * @param  id  authorization id
   */
  public WhoAmIResponse(final String id)
  {
    authzId = id;
  }


  /**
   * Returns the authorization identity or null if no authorization identity was
   * returned by this operation.
   *
   * @return  authorization identity
   */
  public String getAuthzId()
  {
    return authzId;
  }


  /**
   * Sets the authorization identity.
   *
   * @param  id  returned from a who am i request
   */
  public void setAuthzId(final String id)
  {
    authzId = id;
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    // RFC defines the response name as absent
    return null;
  }



  /** {@inheritDoc} */
  @Override
  public void decode(final byte[] encoded)
  {
    setAuthzId(OctetStringType.decode(ByteBuffer.wrap(encoded)));
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d]",
        getClass().getName(),
        hashCode());
  }
}
