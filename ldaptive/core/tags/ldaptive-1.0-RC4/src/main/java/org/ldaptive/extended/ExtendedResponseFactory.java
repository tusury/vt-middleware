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
 * Utility class for creating extended responses.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class ExtendedResponseFactory
{


  /** Default constructor. */
  private ExtendedResponseFactory() {}


  /**
   * Creates an extended response from the supplied response data.
   *
   * @param  requestOID  of the extended request
   * @param  responseOID  of the extended response
   * @param  encoded  BER encoding of the extended response
   *
   * @return  extended response
   */
  public static ExtendedResponse<?> createExtendedResponse(
    final String requestOID,
    final String responseOID,
    final byte[] encoded)
  {
    ExtendedResponse<?> res;
    if (PasswordModifyRequest.OID.equals(requestOID)) {
      res = new PasswordModifyResponse();
      if (encoded != null) {
        res.decode(encoded);
      }
    } else if (WhoAmIRequest.OID.equals(requestOID)) {
      res = new WhoAmIResponse();
      if (encoded != null) {
        res.decode(encoded);
      }
    } else {
      throw new IllegalArgumentException("Unknown OID: " + responseOID);
    }
    return res;
  }
}
