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
package edu.vt.middleware.crypt.pbe;

import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;

/**
 * Implements the PBKDF1 key generation function defined in PKCS#5v2.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PBKDF1KeyGenerator extends AbstractPKCSKeyGenerator
{

  /** Digest algorithm. */
  private DigestAlgorithm digest;


  /**
   * Creates a new instance that uses the given digest for the pseudorandom
   * function.
   *
   * @param  prf  Pseudorandom function digest.
   * @param  saltBytes  Key derivation function salt bytes.
   */
  public PBKDF1KeyGenerator(final DigestAlgorithm prf, final byte[] saltBytes)
  {
    this(prf, saltBytes, DEFAULT_ITERATION_COUNT);
  }


  /**
   * Creates a new instance that uses the given digest for the pseudorandom
   * function.
   *
   * @param  prf  Pseudorandom function digest.
   * @param  saltBytes  Key derivation function salt bytes.
   * @param  iterations  Key derivation function iteration count.
   */
  public PBKDF1KeyGenerator(
    final DigestAlgorithm prf,
    final byte[] saltBytes,
    final int iterations)
  {
    if (prf == null) {
      throw new IllegalArgumentException("Digest cannot be null.");
    }
    this.digest = prf;
    this.salt = saltBytes;
    setIterationCount(iterations);
  }


  /** {@inheritDoc} */
  protected PBEParametersGenerator newParamGenerator()
  {
    return new PKCS5S1ParametersGenerator(digest.getDigest());
  }


  /** {@inheritDoc} */
  protected byte[] toBytes(final char[] password)
  {
    return PBEParametersGenerator.PKCS5PasswordToBytes(password);
  }
}
