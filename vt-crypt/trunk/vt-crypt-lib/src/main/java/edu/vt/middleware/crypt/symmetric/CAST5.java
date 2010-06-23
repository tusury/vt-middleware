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
package edu.vt.middleware.crypt.symmetric;

/**
 * Provider of symmetric encryption/decryption operations using CAST5 cipher.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class CAST5 extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "CAST5";

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
  };


  /**
   * Creates a default CAST5 symmetric encryption algorithm using CBC mode and
   * PKCS5 padding.
   */
  public CAST5()
  {
    this(DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a default CAST5 symmetric encryption algorithm using the given mode
   * and padding style.
   *
   * @param  mode  Cipher mode name.
   * @param  padding  Cipher padding style name.
   */
  public CAST5(final String mode, final String padding)
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
