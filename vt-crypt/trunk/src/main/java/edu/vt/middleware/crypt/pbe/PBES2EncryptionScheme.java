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

import edu.vt.middleware.crypt.pkcs.PBKDF2Parameters;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

/**
 * Implements the PBES2 encryption scheme defined in PKCS#5v2.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PBES2EncryptionScheme extends AbstractPKCSEncryptionScheme
{
  /**
   * Creates a new instance with the given parameters.
   *
   * @param  params  Container for required salt, iterations, and key length.
   * @param  alg  Symmetric cipher algorithm used for encryption/decryption.
   * The cipher is expected to be initialized with whatever initialization data
   * is required for encryption/decryption, e.g. initialization vector.
   */
  public PBES2EncryptionScheme(
      final PBKDF2Parameters params, final SymmetricAlgorithm alg)
  {
    if (alg == null) {
      throw new IllegalArgumentException("Symmetric cipher cannot be null.");
    }
    generator = new PBKDF2KeyGenerator(
        params.getLength() * 8, params.getSalt(), params.getIterationCount());
    cipher = alg;
  }


  /** {@inheritDoc} */
  protected void initCipher(final byte[] derivedKey)
  {
    cipher.setKey(new SecretKeySpec(derivedKey, cipher.getAlgorithm()));
  }
}
