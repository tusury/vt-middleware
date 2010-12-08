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
package edu.vt.middleware.crypt.asymmetric;

/**
 * <p><code>RSA</code> contains functions for encrypting and decrypting using
 * the RSA algorithm. The encryption mode is set to 'NONE'. The padding is set
 * to 'OAEP'. This classes defaults to a key creation length of 2048 bits.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class RSA extends AsymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "RSA";

  /** Default key size for this algorithm in bits. */
  public static final int DEFAULT_KEY_LENGTH = 2048;


  /**
   * Creates a default RSA asymmetric encryption algorithm that uses OAEP
   * padding.
   */
  public RSA()
  {
    super(ALGORITHM);
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
}
