/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.pbe;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;

/**
 * Implements the PBKDF2 key generation function defined in PKCS#5v2.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PBKDF2KeyGenerator extends AbstractPKCSKeyGenerator
{
  /** Size of derived key in bits. */
  private int derivedKeyLength;


  /**
   * Creates a new instance that uses SHA1 hash for pseudorandom function
   * to generate derived keys of the given length in bytes (octets).
   *
   * @param  keyBitLength  Size of derived key in bits.
   * @param  saltBytes  Key derivation function salt bytes.
   */
  public PBKDF2KeyGenerator(final int keyBitLength, final byte[] saltBytes)
  {
    this(keyBitLength, saltBytes, DEFAULT_ITERATION_COUNT);
  }


  /**
   * Creates a new instance that uses SHA1 hash for pseudorandom function
   * to generate derived keys of the given length in bytes (octets).
   *
   * @param  keyBitLength  Size of derived keys in bits.
   * @param  saltBytes  Key derivation function salt bytes.
   * @param  iterations  Key derivation function iteration count.
   */
  public PBKDF2KeyGenerator(
      final int keyBitLength, final byte[] saltBytes, final int iterations)
  {
    this.derivedKeyLength = keyBitLength;
    this.salt = saltBytes;
    setIterationCount(iterations);
  }


  /** {@inheritDoc} */
  protected PBEParametersGenerator newParamGenerator()
  {
    return new PKCS5S2ParametersGenerator();
  }


  /** {@inheritDoc} */
  protected byte[] toBytes(final char[] password)
  {
    return PBEParametersGenerator.PKCS5PasswordToBytes(password);
  }


  /** {@inheritDoc} */
  protected int getKeyBitLength()
  {
    return derivedKeyLength;
  }

}
