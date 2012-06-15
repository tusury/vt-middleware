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
 * Marker interface for ldap extended responses.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ExtendedResponse
{


  /**
   * Returns the OID for this extended response. Response OIDs are optional and
   * this value may be null.
   *
   * @return  oid
   */
  String getOID();


  /**
   * Initializes this response with the supplied BER encoded data.
   *
   * @param  encoded  BER encoded response
   */
  void decode(byte[] encoded);
}
