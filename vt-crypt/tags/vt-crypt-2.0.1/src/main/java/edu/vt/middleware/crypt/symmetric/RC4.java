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
 * Provider of symmetric encryption/decryption operations using RC4 cipher.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class RC4 extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "RC4";

  /** Default key size for this algorithm in bits. */
  public static final int DEFAULT_KEY_LENGTH = 128;

  /** Available key lengths in bits. */
  public static final int[] KEY_LENGTHS = new int[] {
    256,
    128,
    64,
    56,
    48,
    40,
  };


  /** Creates a RC4 symmetric encryption algorithm. */
  public RC4()
  {
    // RC4 is a stream cipher and does not support a mode or padding
    super(ALGORITHM, null, null);
  }


  /**
   * Creates a RC4 symmetric encryption algorithm. The mode and padding
   * arguments are ignored since this is a stream cipher and does not support a
   * block mode or padding; it is provided for consistency with other ciphers
   * only.
   *
   * @param  mode  Cipher mode name -- ignored.
   * @param  padding  Cipher padding style name -- ignored.
   */
  public RC4(final String mode, final String padding)
  {
    super(ALGORITHM, null, null);
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
  public boolean isValidKeyLength(final int bitLength)
  {
    return bitLength >= getMinKeyLength() && bitLength <= getMaxKeyLength();
  }
}
