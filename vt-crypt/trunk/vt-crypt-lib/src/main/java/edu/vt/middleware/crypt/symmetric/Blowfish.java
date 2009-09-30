/*
  $Id: Blowfish.java 3 2008-11-11 20:58:48Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3 $
  Updated: $Date: 2008-11-11 15:58:48 -0500 (Tue, 11 Nov 2008) $
*/
package edu.vt.middleware.crypt.symmetric;

/**
 * Provider of symmetric encryption/decryption operations using Blowfish cipher.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class Blowfish extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "Blowfish";

  /** Minimum key length in bits. */
  public static final int MIN_KEY_LENGTH = 32;

  /** Maximum key length in bits. */
  public static final int MAX_KEY_LENGTH = 448;

  /** Default key size for this algorithm in bits. */
  public static final int DEFAULT_KEY_LENGTH = 128;

  /** Available key lengths in bits. */
  public static final int[] KEY_LENGTHS = new int[] {
    448,
    384,
    320,
    256,
    192,
    128,
    64,
  };

  /** Number of bits in byte. */
  private static final int BITS_IN_BYTE = 8;


  /**
   * Creates a default CAST5 symmetric encryption algorithm using CBC mode and
   * PKCS5 padding.
   */
  public Blowfish()
  {
    this(DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a default Blowfish symmetric encryption algorithm using the given
   * mode and padding style.
   *
   * @param  mode  Cipher mode name.
   * @param  padding  Cipher padding style name.
   */
  public Blowfish(final String mode, final String padding)
  {
    super(ALGORITHM, mode, padding);
  }


  /**
   * Gets the default key length for this algorithm.
   *
   * @return  Default key length in bits.
   */
  public int getDefaultKeyLength()
  {
    return DEFAULT_KEY_LENGTH;
  }


  /** {@inheritDoc} */
  public int[] getAllowedKeyLengths()
  {
    return KEY_LENGTHS;
  }


  /** {@inheritDoc} */
  public int getMinKeyLength()
  {
    return MIN_KEY_LENGTH;
  }


  /** {@inheritDoc} */
  public int getMaxKeyLength()
  {
    return MAX_KEY_LENGTH;
  }


  /** {@inheritDoc} */
  public boolean isValidKeyLength(final int bitLength)
  {
    if (bitLength < MIN_KEY_LENGTH || bitLength > MAX_KEY_LENGTH) {
      return false;
    } else {
      return bitLength % BITS_IN_BYTE == 0;
    }
  }
}
