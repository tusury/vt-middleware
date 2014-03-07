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
package org.ldaptive.io;

import java.util.UUID;
import org.ldaptive.LdapUtils;

/**
 * Decodes and encodes a UUID for use in an ldap attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UUIDValueTranscoder implements ValueTranscoder<UUID>
{


  /** {@inheritDoc} */
  @Override
  public UUID decodeStringValue(final String value)
  {
    return UUID.fromString(value);
  }


  /** {@inheritDoc} */
  @Override
  public UUID decodeBinaryValue(final byte[] value)
  {
    return decodeStringValue(LdapUtils.utf8Encode(value));
  }


  /** {@inheritDoc} */
  @Override
  public String encodeStringValue(final UUID value)
  {
    return value.toString();
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encodeBinaryValue(final UUID value)
  {
    return LdapUtils.utf8Encode(encodeStringValue(value));
  }


  /** {@inheritDoc} */
  @Override
  public Class<UUID> getType()
  {
    return UUID.class;
  }
}
