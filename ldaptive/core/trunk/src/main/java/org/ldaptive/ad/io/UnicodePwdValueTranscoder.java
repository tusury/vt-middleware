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

import java.nio.charset.Charset;
import org.ldaptive.LdapUtils;
import org.ldaptive.io.ValueTranscoder;

/**
 * Decodes and encodes an active directory unicodePwd value for use in an ldap
 * attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date\$
 */
public class UnicodePwdValueTranscoder implements ValueTranscoder<String>
{

  /** UTF-16LE character set. */
  private static final Charset UTF_16LE = Charset.forName("UTF-16LE");


  /** {@inheritDoc} */
  @Override
  public String decodeStringValue(final String value)
  {
    return decodeBinaryValue(LdapUtils.utf8Encode(value));
  }


  /** {@inheritDoc} */
  @Override
  public String decodeBinaryValue(final byte[] value)
  {
    final String pwd = new String(value, UTF_16LE);
    if (pwd.length() < 2) {
      throw new IllegalArgumentException(
        "unicodePwd must be at least 2 characters long");
    }
    return pwd.substring(1, pwd.length() - 1);
  }


  /** {@inheritDoc} */
  @Override
  public String encodeStringValue(final String value)
  {
    return LdapUtils.utf8Encode(encodeBinaryValue(value));
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encodeBinaryValue(final String value)
  {
    if (value == null) {
      throw new IllegalArgumentException("Cannot encode null value");
    }
    final String pwd = String.format("\"%s\"", value);
    return pwd.getBytes(UTF_16LE);
  }


  /** {@inheritDoc} */
  @Override
  public Class<String> getType()
  {
    return String.class;
  }
}
