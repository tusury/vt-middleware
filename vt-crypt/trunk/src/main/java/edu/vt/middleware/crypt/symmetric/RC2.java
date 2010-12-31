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

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.RC2ParameterSpec;

/**
 * Provider of symmetric encryption/decryption operations using RC2 cipher.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class RC2 extends SymmetricAlgorithm
{
  /** Algorithm name. */
  public static final String ALGORITHM = "RC2";

  /** Default effective key bits. */
  public static final int DEFAULT_EFFECTIVE_BITS = 1024;

  /** Sets the effective key size in bits. */
  private int effectiveKeyBits = DEFAULT_EFFECTIVE_BITS;


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
   * Gets the effective key size in bits.  This is a parameter specific to the
   * RC2 cipher algorithm.
   *
   * @return  Effective key size in bits.
   */
  public int getEffectiveKeyBits()
  {
    return effectiveKeyBits;
  }


  /**
   * Sets the effective key size in bits.  This is a parameter specific to the
   * RC2 cipher algorithm.
   *
   * @param  numBits  Effective key size in bits; MUST be positive integer.
   */
  public void setEffectiveKeyBits(final int numBits)
  {
    if (numBits < 1) {
      throw new IllegalArgumentException(
          "EffectiveKeyBits must be positive integer.");
    }
    this.effectiveKeyBits = numBits;
  }


  /** {@inheritDoc} */
  @Override
  protected AlgorithmParameterSpec getAlgorithmParameterSpec()
  {
    final AlgorithmParameterSpec spec;
    if (paramSpec != null) {
      spec = paramSpec;
    } else {
      if (iv != null) {
        spec = new RC2ParameterSpec(effectiveKeyBits, iv);
      } else {
        spec = new RC2ParameterSpec(effectiveKeyBits);
      }
    }
    return spec;
  }
}
