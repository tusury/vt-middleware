/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.symmetric;

/**
 * Provider of symmetric encryption/decryption operations using RC2 cipher.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class RC2 extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "RC2";

  /** Default key size for this algorithm in bits. */
  public static final int DEFAULT_KEY_LENGTH = 128;

  /** Available key lengths in bits. */
  public static final int[] KEY_LENGTHS = new int[] {
    128,
    120,
    112,
    104,
    96,
    88,
    80,
    72,
    64,
    56,
    48,
    40,
    32,
    24,
    16,
    8,
  };


  /**
   * Creates a default RC2 symmetric encryption algorithm using CBC mode and
   * PKCS5 padding.
   */
  public RC2()
  {
    this(DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a default RC2 symmetric encryption algorithm using the given mode
   * and padding style.
   *
   * @param  mode  Cipher mode name.
   * @param  padding  Cipher padding style name.
   */
  public RC2(final String mode, final String padding)
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
}
