/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.intermediate;

import org.ldaptive.ResponseMessage;

/**
 * Interface for ldap intermediate responses.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface IntermediateResponse extends ResponseMessage
{


  /**
   * Returns the OID for this response.
   *
   * @return  oid
   */
  String getOID();


  /**
   * Initializes this response with the supplied BER encoded data.
   *
   * @param  encoded  BER encoded response value
   */
  void decode(byte[] encoded);
}
