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
 * Provider of symmetric encryption/decryption operations using RC6 cipher.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class RC6 extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "RC6";

  /** Available key lengths in bits. */
  public static final int[] KEY_LENGTHS = new int[] {
    256,
    192,
    128,
  };


  /**
   * Creates a default RC6 symmetric encryption algorithm using CBC mode and
   * PKCS5 padding.
   */
  public RC6()
  {
    this(DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a default RC6 symmetric encryption algorithm using the given mode
   * and padding style.
   *
   * @param  mode  Cipher mode name.
   * @param  padding  Cipher padding style name.
   */
  public RC6(final String mode, final String padding)
  {
    super(ALGORITHM, mode, padding);
  }


  /** {@inheritDoc} */
  public int[] getAllowedKeyLengths()
  {
    return KEY_LENGTHS;
  }
}
