/*
  $Id: BitString.java 426 2009-08-11 20:40:13Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 426 $
  Updated: $Date: 2009-08-11 16:40:13 -0400 (Tue, 11 Aug 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

import org.bouncycastle.util.Arrays;

/**
 * Represents a string of bits encoded into octets in network byte order.
 *
 * @author Middleware
 * @version $Revision: 426 $
 *
 */
public class BitString
{
  /** Number of bytes in a Java int */
  protected static final int INT_BYTE_SIZE = 4;

  /** Number of bits in a byte */
  protected static final int BITS_IN_BYTE = 8;

  /** Unsigned byte bitmask */
  protected static final int UNSIGNED_BYTE_MASK = 0xFF;

  /** Hash code scale factor */
  private static final int HASH_FACTOR = 31;

  /** Octets used to encode bit string */
  protected byte[] octets;


  /**
   * Gets a byte representation of a bit string encoded as a Java integer.
   *
   * @param  bitString  Bit string stored in Java int.
   *
   * @return  Bit string encoded as octets.
   */
  public static byte[] getBytes(final int bitString)
  {
    int bytes = INT_BYTE_SIZE;
    for (int i = INT_BYTE_SIZE - 1; i >= 1; i--)
    {
      if ((bitString & (UNSIGNED_BYTE_MASK << (i * BITS_IN_BYTE))) != 0)
      {
        break;
      }
      bytes--;
    }
    final byte[] data = new byte[bytes];
    for (int i = 0; i < bytes; i++)
    {
      data[i] = (byte) ((bitString >> (i * BITS_IN_BYTE)) & UNSIGNED_BYTE_MASK);
    }
    return data;
  }


  /**
   * @return  Bit string encoded as octet bytes.
   */
  public byte[] getOctets()
  {
    return octets;
  }


  /**
   * @return The value of the bit string as a Java int.
   */
  public int intValue()
  {
    int value = 0;

    for (int i = 0; i != octets.length && i != INT_BYTE_SIZE; i++) {
      value |= (octets[i] & UNSIGNED_BYTE_MASK) << (BITS_IN_BYTE * i);
    }

    return value;
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      result = Arrays.areEqual(octets, ((BitString) obj).getOctets());
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return HASH_FACTOR * getClass().hashCode() + Arrays.hashCode(octets);
  }
}
