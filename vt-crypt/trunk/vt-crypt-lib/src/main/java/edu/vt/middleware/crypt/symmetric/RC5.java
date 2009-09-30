/*
  $Id: RC5.java 3 2008-11-11 20:58:48Z dfisher $

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
 * Provider of symmetric encryption/decryption operations using RC5 cipher.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class RC5 extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "RC5";

  /** Default key size for this algorithm in bits. */
  public static final int DEFAULT_KEY_LENGTH = 128;

  /** Available key lengths in bits. */
  private static final int[] KEY_LENGTHS = new int[] {
    2040,
    1792,
    1536,
    1280,
    1024,
    768,
    512,
    256,
    128,
    64,
    32,
  };


  /**
   * Creates a default RC5 symmetric encryption algorithm using CBC mode and
   * PKCS5 padding.
   */
  public RC5()
  {
    this(DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a default RC5 symmetric encryption algorithm using the given mode
   * and padding style.
   *
   * @param  mode  Cipher mode name.
   * @param  padding  Cipher padding style name.
   */
  public RC5(final String mode, final String padding)
  {
    super(ALGORITHM, mode, padding);
  }


  /** {@inheritDoc} */
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
  public boolean isValidKeyLength(final int bitLength)
  {
    return bitLength >= getMinKeyLength() && bitLength <= getMaxKeyLength();
  }
}
