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
package edu.vt.middleware.crypt.symmetric;

/**
 * Provider of symmetric encryption/decryption operations using Triple-DES
 * cipher.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class DESede extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "DESede";

  /** Default key size for this algorithm in bits. */
  public static final int DEFAULT_KEY_LENGTH = 168;

  /** Available key lengths in bits. */
  public static final int[] KEY_LENGTHS = new int[] {168};


  /**
   * Creates a default Triple-DES symmetric encryption algorithm using CBC mode
   * and PKCS5 padding.
   */
  public DESede()
  {
    this(DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a default Triple-DES symmetric encryption algorithm using the given
   * mode and padding style.
   *
   * @param  mode  Cipher mode name.
   * @param  padding  Cipher padding style name.
   */
  public DESede(final String mode, final String padding)
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
