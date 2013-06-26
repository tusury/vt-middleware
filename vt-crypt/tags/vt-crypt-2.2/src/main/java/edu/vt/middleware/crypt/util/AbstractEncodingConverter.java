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
package edu.vt.middleware.crypt.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.bouncycastle.util.encoders.Encoder;

/**
 * Abstract base class for all converters that perform encoding with a BC {@link
 * org.bouncycastle.util.encoders.Encoder} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractEncodingConverter implements Converter
{

  /** {@inheritDoc} */
  public String fromBytes(final byte[] input)
  {
    return fromBytes(input, 0, input.length);
  }


  /** {@inheritDoc} */
  public String fromBytes(
    final byte[] input,
    final int offset,
    final int length)
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      getEncoder().encode(input, offset, length, out);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    try {
      return out.toString(Convert.ASCII_CHARSET.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("ASCII character set not available.");
    }
  }


  /** {@inheritDoc} */
  public byte[] toBytes(final String input)
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      getEncoder().decode(input, out);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return out.toByteArray();
  }


  /**
   * Gets the encoder instance that does the work of byte-char/char-byte
   * encoding.
   *
   * @return  Encoder instance.
   */
  protected abstract Encoder getEncoder();
}
