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
 * Interface for decoding ldap attribute values into custom types.
 *
 * @param  <T>  type of value
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface LdapAttributeValueDecoder<T>
{


  /**
   * Decodes the supplied ldap attribute value into a custom type.
   *
   * @param  value  to decode
   *
   * @return  decoded value
   */
  T decodeStringValue(String value);


  /**
   * Decodes the supplied ldap attribute value into a custom type.
   *
   * @param  value  to decode
   *
   * @return  decoded value
   */
  T decodeBinaryValue(byte[] value);


  /**
   * Returns the type produced by this value decoder.
   *
   * @return  type produced by this value decoder
   */
  Class<T> getType();
}
