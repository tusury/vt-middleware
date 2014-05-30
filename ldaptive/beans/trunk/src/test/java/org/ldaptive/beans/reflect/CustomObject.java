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
package org.ldaptive.beans.reflect;

import org.ldaptive.io.ValueTranscoder;

/**
 * Interface for testing bean annotations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface CustomObject
{


  /**
   * Prepare this object for use;
   */
  void initialize();



  /** Transcoder that adds 'prefix-' to string values. */
  static class PrefixStringValueTranscoder implements ValueTranscoder<String>
  {

    @Override
    public String decodeStringValue(final String value)
    {
      return value.replaceFirst("prefix-", "");
    }

    @Override
    public String decodeBinaryValue(final byte[] value)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String encodeStringValue(final String value)
    {
      return String.format("prefix-%s", value);
    }

    @Override
    public byte[] encodeBinaryValue(final String value)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<String> getType()
    {
      return String.class;
    }
  }
}
