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
package org.ldaptive.io;

/**
 * Interface for encoding custom types into ldap attribute values.
 *
 * @param  <T>  type of value
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface LdapAttributeValueEncoder<T>
{


  /**
   * Encodes the supplied value into an ldap attribute value.
   *
   * @param  value  to encode
   *
   * @return  encoded value
   */
  String encodeStringValue(T value);


  /**
   * Encodes the supplied value into an ldap attribute value.
   *
   * @param  value  to encode
   *
   * @return  encoded value
   */
  byte[] encodeBinaryValue(T value);


  /**
   * Returns the type produced by this value encoder.
   *
   * @return  type produced by this value encoder
   */
  Class<T> getType();
}
