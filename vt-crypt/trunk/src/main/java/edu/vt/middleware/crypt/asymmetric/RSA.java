/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
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
 * @version  $Revision$
 */

public class RSA extends AsymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "RSA";


  /**
   * Creates a default RSA asymmetric encryption algorithm that uses OAEP
   * padding.
   */
  public RSA()
  {
    super(ALGORITHM);
  }

}
