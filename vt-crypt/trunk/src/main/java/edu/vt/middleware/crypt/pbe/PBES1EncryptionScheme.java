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

import edu.vt.middleware.crypt.pkcs.PBEParameter;
import edu.vt.middleware.crypt.pkcs.PBES1Algorithm;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

/**
 * Implements the PBES1 encryption scheme defined in PKCS#5v2.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PBES1EncryptionScheme extends AbstractEncryptionScheme
{
  /** Number of bytes (octets) in derived key. */
  public static final int KEY_BYTE_LENGTH = 8;

  /** Number of bytes (octets) in IV. */
  public static final int IV_BYTE_LENGTH = 8;


  /**
   * Creates a new instance with the given parameters.
   *
   * @param  alg  Describes hash/algorithm pair suitable for PBES1 scheme.
   * @param  params  Key generation function salt and iteration count.
   */
  public PBES1EncryptionScheme(
      final PBES1Algorithm alg,
      final PBEParameter params)
  {
    generator = new PBKDF1KeyGenerator(
        alg.getDigest(), params.getSalt(), params.getIterationCount());
    cipher = SymmetricAlgorithm.newInstance(alg.getSpec());
  }


  /**
   * Initializes the cipher with the given PBE derived key bytes.
   *
   * @param  derivedKey  Derived key bytes.
   */
  protected void initCipher(final byte[] derivedKey)
  {
    final byte[] keyBytes = new byte[KEY_BYTE_LENGTH];
    final byte[] ivBytes = new byte[IV_BYTE_LENGTH];
    System.arraycopy(derivedKey, 0, keyBytes, 0, KEY_BYTE_LENGTH);
    System.arraycopy(derivedKey, KEY_BYTE_LENGTH, ivBytes, 0, IV_BYTE_LENGTH);
    cipher.setKey(new SecretKeySpec(keyBytes, cipher.getAlgorithm()));
    cipher.setIV(ivBytes);
  }
}
