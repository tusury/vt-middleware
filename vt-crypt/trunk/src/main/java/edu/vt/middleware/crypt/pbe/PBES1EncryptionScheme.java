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
package edu.vt.middleware.crypt.pbe;

import javax.crypto.spec.SecretKeySpec;
import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.pkcs.PBEParameter;
import edu.vt.middleware.crypt.pkcs.PBES1Algorithm;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

/**
 * Implements the PBES1 encryption scheme defined in PKCS#5v2.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PBES1EncryptionScheme extends AbstractEncryptionScheme
{

  /** Number of bytes (octets) in derived key. */
  public static final int KEY_LENGTH = 8;

  /** Number of bytes (octets) in IV. */
  public static final int IV_LENGTH = 8;

  /** Derived key bit length. */
  private static final int DKEY_BIT_LENGTH = (KEY_LENGTH + IV_LENGTH) * 8;

  /** Key generator. */
  private KeyGenerator generator;


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
    setCipher(SymmetricAlgorithm.newInstance(alg.getSpec()));
    generator = new PBKDF1KeyGenerator(
      alg.getDigest(),
      params.getSalt(),
      params.getIterationCount());
  }


  /**
   * Creates a new instance with the given parameters.
   *
   * @param  alg  Symmetric algorithm used for encryption/decryption.
   * @param  digest  Key generation function digest.
   * @param  params  Key generation function salt and iteration count.
   */
  public PBES1EncryptionScheme(
    final SymmetricAlgorithm alg,
    final DigestAlgorithm digest,
    final PBEParameter params)
  {
    boolean valid = false;
    for (PBES1Algorithm a : PBES1Algorithm.values()) {
      if (
        a.getDigest().getAlgorithm().equals(digest.getAlgorithm()) &&
          a.getSpec().getName().equals(alg.getAlgorithm()) &&
          a.getSpec().getMode().equals(alg.getMode()) &&
          a.getSpec().getPadding().equals(alg.getPadding())) {
        valid = true;
        break;
      }
    }
    if (!valid) {
      throw new IllegalArgumentException("Invalid digest/cipher combination.");
    }
    setCipher(alg);
    generator = new PBKDF1KeyGenerator(
      digest,
      params.getSalt(),
      params.getIterationCount());
  }


  /** {@inheritDoc} */
  protected void initCipher(final char[] password)
  {
    final byte[] derivedKey = generator.generate(password, DKEY_BIT_LENGTH);
    final byte[] keyBytes = new byte[KEY_LENGTH];
    System.arraycopy(derivedKey, 0, keyBytes, 0, KEY_LENGTH);
    cipher.setKey(new SecretKeySpec(keyBytes, cipher.getAlgorithm()));
    if (!cipher.hasIV()) {
      // Use the generated IV value
      final byte[] ivBytes = new byte[IV_LENGTH];
      System.arraycopy(derivedKey, KEY_LENGTH, ivBytes, 0, IV_LENGTH);
      cipher.setIV(ivBytes);
    }
  }
}
