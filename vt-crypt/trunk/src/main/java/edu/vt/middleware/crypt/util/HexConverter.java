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
import java.util.regex.Pattern;
import org.bouncycastle.util.encoders.Encoder;
import org.bouncycastle.util.encoders.HexEncoder;

/**
 * Converts bytes to HEX and vice versa.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class HexConverter extends AbstractEncodingConverter
{

  /** Default byte delimiter. */
  public static final String DEFAULT_BYTE_DELIMITER = ":";

  /** Does encoding work. */
  private HexEncoder encoder = new HexEncoder();

  /** Flag that determines whether bytes are delimited in string output. */
  private boolean delimitBytesFlag;

  /** Byte delimiter. */
  private String byteDelimiter;

  /** Pattern used to split delimited hex string. */
  private Pattern splitPattern;


  /** Creates a new instance. */
  public HexConverter()
  {
    this(false);
  }


  /**
   * Creates a new instance that optionally handled delimited bytes in the
   * string input/output.
   *
   * @param  delimitBytes  True to handle delimited input and produce delimited
   * output strings with delimiter character, false otherwise. If enabled, the
   * input/output hexadecimal strings would resemble <code>1A:2B:3C:4D</code>.
   * Call {@link #setByteDelimiter(String)} to use a delimiter other than {@link
   * #DEFAULT_BYTE_DELIMITER}.
   *
   * <p>Note: Setting delimited output has the side effect of
   * producing uppercase hex characters. This is because several cryptographic
   * utilities produce delimited fingerprints with uppercase hex characters, so
   * delimited output keeps with that convention since it is anticipated to be
   * the common use case for this feature.</p>
   */
  public HexConverter(final boolean delimitBytes)
  {
    this.delimitBytesFlag = delimitBytes;
    setByteDelimiter(DEFAULT_BYTE_DELIMITER);
  }


  /**
   * Gets the byte delmiter string.
   *
   * @return  Byte delimiter string.
   */
  public String getByteDelimiter()
  {
    return byteDelimiter;
  }


  /**
   * Sets the byte delimiter string. For example, if the delimiter is ":", then
   * output would resemble <code>1A:2B:3C:4D</code>.
   *
   * @param  delim  Byte delimiter string.
   */
  public void setByteDelimiter(final String delim)
  {
    this.byteDelimiter = delim;
    if (this.delimitBytesFlag) {
      splitPattern = Pattern.compile(byteDelimiter);
    }
  }


  /** {@inheritDoc} */
  public String fromBytes(
    final byte[] input,
    final int offset,
    final int length)
  {
    if (delimitBytesFlag) {
      final byte[] delimBytes = byteDelimiter.getBytes();
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      for (int i = offset; i < length; i++) {
        try {
          if (i > offset) {
            out.write(delimBytes);
          }
          encoder.encode(input, offset + i, 1, out);
        } catch (IOException e) {
          throw new IllegalArgumentException(e.getMessage());
        }
      }
      // Use the default character set since the delimiter likely comes
      // from platform character set, which could possibly be outside ASCII
      return out.toString().toUpperCase();
    } else {
      return super.fromBytes(input, offset, length);
    }
  }


  /** {@inheritDoc} */
  public byte[] toBytes(final String input)
  {
    if (delimitBytesFlag) {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final String[] hexBytes = splitPattern.split(input);
      for (int i = 0; i < hexBytes.length; i++) {
        try {
          encoder.decode(hexBytes[i], out);
        } catch (IOException e) {
          throw new IllegalArgumentException(e.getMessage());
        }
      }
      return out.toByteArray();
    } else {
      return super.toBytes(input);
    }
  }


  /** {@inheritDoc} */
  protected Encoder getEncoder()
  {
    return encoder;
  }
}
