/*
  $Id$

  Copyright (C) 2007-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Utility class provides static methods to perform common conversions.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public final class Convert
{

  /** System property used to specify character set. */
  public static final String CHARSET_PROPERTY =
    "edu.vt.middleware.crypt.charset";

  /** ASCII character set used for all encoding methods. */
  public static final Charset ASCII_CHARSET = Charset.forName("ASCII");

  /** Default character set. */
  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  /** For hex-byte conversions */
  private static final HexConverter HEX_CONVERTER = new HexConverter();

  /** For base64-byte conversions */
  private static final Base64Converter B64_CONVERTER = new Base64Converter();


  /** <p>Default constructor.</p> */
  private Convert() {}


  /**
   * Gets the default charset used for character/byte conversions. If the
   * edu.vt.middleware.crypt.charset system property is specified, attempts to
   * get the named character set, otherwise returns {@link #DEFAULT_CHARSET}.
   *
   * @return  Default character set.
   */
  public static Charset getDefaultCharset()
  {
    final String charSetName = System.getProperty(CHARSET_PROPERTY);
    if (charSetName != null) {
      return Charset.forName(System.getProperty(charSetName));
    } else {
      return DEFAULT_CHARSET;
    }
  }


  /**
   * Convers an array of strings into a byte array produced by concatenating the
   * byte representation of each string in the default character set.
   *
   * @param  input  String to convert
   *
   * @return  String characters as bytes.
   */
  public static byte[] toBytes(final String[] input)
  {
    final ByteArrayOutputStream data = new ByteArrayOutputStream();
    for (int i = 0; i < input.length; i++) {
      try {
        data.write(input[i].getBytes(getDefaultCharset().name()));
      } catch (Exception e) {
        throw new IllegalStateException("Error decoding " + input[i]);
      }
    }
    return data.toByteArray();
  }


  /**
   * Converts a string to bytes in the default character set.
   *
   * @param  input  String to convert.
   *
   * @return  String characters as bytes.
   */
  public static byte[] toBytes(final String input)
  {
    try {
      return input.getBytes(getDefaultCharset().name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Error decoding " + input);
    }
  }


  /**
   * Converts a character array to a byte array in the default character set.
   *
   * @param  input  Character array to convert.
   *
   * @return  Characters as bytes in the default charset.
   */
  public static byte[] toBytes(final char[] input)
  {
    try {
      return new String(input).getBytes(getDefaultCharset().name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Error decoding input characters.");
    }
  }


  /**
   * Converts a string to bytes in the ASCII character set.
   *
   * @param  input  String to convert.
   *
   * @return  Byte array of ASCII characters.
   */
  public static byte[] toAsciiBytes(final String input)
  {
    try {
      return input.getBytes(ASCII_CHARSET.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Error decoding " + input);
    }
  }


  /**
   * Converts a byte array to a string in the default encoding.
   *
   * @param  input  Byte array to convert.
   *
   * @return  String representation of bytes.
   */
  public static String toString(final byte[] input)
  {
    try {
      return new String(input, getDefaultCharset().name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Error decoding input bytes.");
    }
  }


  /**
   * Converts hexadecimal string of bytes into the original byte array.
   *
   * @param  hex  Hexadecimal string of bytes.
   *
   * @return  Original byte array.
   */
  public static byte[] fromHex(final String hex)
  {
    return HEX_CONVERTER.toBytes(hex);
  }


  /**
   * Converts a byte array into a hexadecimal string representation of the
   * bytes.
   *
   * @param  input  Byte array to convert
   *
   * @return  String of hexadecimal characters, two for each byte in input
   * array.
   */
  public static String toHex(final byte[] input)
  {
    return HEX_CONVERTER.fromBytes(input);
  }


  /**
   * Converts base64-encoded string of bytes into the original byte array.
   *
   * @param  base64  Base64-encoded string of bytes.
   *
   * @return  Original byte array.
   */
  public static byte[] fromBase64(final String base64)
  {
    return B64_CONVERTER.toBytes(base64);
  }


  /**
   * Converts a byte array into a base-64 encoded string without any line
   * breaks.
   *
   * @param  input  Byte array to convert.
   *
   * @return  Base-64 encoded string of input bytes.
   */
  public static String toBase64(final byte[] input)
  {
    return B64_CONVERTER.fromBytes(input);
  }
}
