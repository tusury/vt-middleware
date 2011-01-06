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
  /**
   * Creates a new instance that uses SHA1 hash for pseudorandom function
   * to generate derived keys.
   *
   * @param  saltBytes  Key derivation function salt bytes.
   */
  public PBKDF2KeyGenerator(final byte[] saltBytes)
  {
    this(saltBytes, DEFAULT_ITERATION_COUNT);
  }


  /**
   * Creates a new instance that uses SHA1 hash for pseudorandom function
   * to generate derived keys.
   *
   * @param  saltBytes  Key derivation function salt bytes.
   * @param  iterations  Key derivation function iteration count.
   */
  public PBKDF2KeyGenerator(final byte[] saltBytes, final int iterations)
  {
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
}
