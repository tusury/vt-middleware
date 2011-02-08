/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.util;

/**
 * Interface describing conversion of bytes to string and vice versa.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public interface Converter
{

  /**
   * Converts a byte array to a formatted/encoded string.
   *
   * @param  input  Input bytes.
   *
   * @return  Formatted/encoded string derived from input bytes.
   */
  String fromBytes(byte[] input);


  /**
   * Converts a byte array to a formatted/encoded string.
   *
   * @param  input  Input bytes.
   * @param  offset  Offset into input bytes at which to begin processing.
   * @param  length  Number of bytes of input data to process.
   *
   * @return  Formatted/encoded string derived from input bytes.
   */
  String fromBytes(byte[] input, int offset, int length);


  /**
   * Converts a formatted/encoded string to raw bytes.
   *
   * @param  input  Formatted/encoded input string.
   *
   * @return  Byte array corresponding to input string.
   */
  byte[] toBytes(String input);
}
