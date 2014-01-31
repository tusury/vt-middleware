/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.ad.io;

import org.ldaptive.LdapUtils;
import org.ldaptive.io.ValueTranscoder;

/**
 * Decodes and encodes an active directory delta time value for use in an ldap
 * attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DeltaTimeValueTranscoder implements ValueTranscoder<Long>
{

  /**
   * Delta time uses 100-nanosecond intervals. For conversion purposes this is
   * 1x10^6 / 100.
   */
  private static final long ONE_HUNDRED_NANOSECOND_INTERVAL = 10000L;


  /** {@inheritDoc} */
  @Override
  public Long decodeStringValue(final String value)
  {
    return -Long.parseLong(value) / ONE_HUNDRED_NANOSECOND_INTERVAL;
  }


  /** {@inheritDoc} */
  @Override
  public Long decodeBinaryValue(final byte[] value)
  {
    return decodeStringValue(LdapUtils.utf8Encode(value));
  }


  /** {@inheritDoc} */
  @Override
  public String encodeStringValue(final Long value)
  {
    return String.valueOf(-value * ONE_HUNDRED_NANOSECOND_INTERVAL);
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encodeBinaryValue(final Long value)
  {
    return LdapUtils.utf8Encode(encodeStringValue(value));
  }


  /** {@inheritDoc} */
  @Override
  public Class<Long> getType()
  {
    return Long.class;
  }
}
