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

import javax.crypto.spec.SecretKeySpec;

import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.pkcs.PBEParameter;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

/**
 * Implements the password-based encryption scheme in section B of PKCS#12.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PKCS12EncryptionScheme extends AbstractPKCSEncryptionScheme
{
  /**
   * Creates a new instance with the given parameters.
   *
   * @param  digest  Digest algorithm used for PBE pseudorandom function.
   * @param  params  Key generation function salt and iteration count.
   * @param  keyBitLength  Derived key length in bits.
   * @param  alg  Symmetric cipher algorithm used for encryption/decryption.
   */
  public PKCS12EncryptionScheme(
      final DigestAlgorithm digest,
      final PBEParameter params,
      final int keyBitLength,
      final SymmetricAlgorithm alg)
  {
    if (alg == null) {
      throw new IllegalArgumentException("Symmetric cipher cannot be null.");
    }
    generator = new PKCS12KeyGenerator(
        digest, params.getSalt(), params.getIterationCount(), keyBitLength);
    cipher = alg;
  }


  /** {@inheritDoc} */
  protected void initCipher(final byte[] derivedKey)
  {
    cipher.setKey(new SecretKeySpec(derivedKey, cipher.getAlgorithm()));
  }
}
