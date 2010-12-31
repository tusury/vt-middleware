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
 * Implements the PBES1 encryption scheme defined in PKCS#5v2.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PBES1EncryptionScheme extends AbstractPKCSEncryptionScheme
{
  /** Number of bytes (octets) in derived key. */
  public static final int KEY_BYTE_LENGTH = 8;

  /** Number of bytes (octets) in IV. */
  public static final int IV_BYTE_LENGTH = 8;

  /** Supported cipher/digest pairs. */
  private static final String[][] SUPPORTED_ALGORITHMS = new String[][] {
    new String[] {"MD2", "DES"},
    new String[] {"MD2", "RC2"},
    new String[] {"MD5", "DES"},
    new String[] {"MD5", "RC2"},
    new String[] {"SHA1", "DES"},
    new String[] {"SHA1", "RC2"},
  };


  /**
   * Creates a new instance with the given parameters.
   *
   * @param  digest  Digest algorithm used for PBE pseudorandom function.
   * @param  params  Key generation function salt and iteration count.
   * @param  alg  Symmetric cipher algorithm used for encryption/decryption.
   */
  public PBES1EncryptionScheme(
      final DigestAlgorithm digest,
      final PBEParameter params,
      final SymmetricAlgorithm alg)
  {
    // Validate supported digest/cipher combinations as defined in PKCS#5v2
    // section A.3 for PBES1 scheme
    boolean valid = false;
    for (String[] pair : SUPPORTED_ALGORITHMS) {
      if (pair[0].equals(digest.getAlgorithm()) &&
          pair[1].equals(alg.getAlgorithm())) {
        valid = true;
        break;
      }
    }
    if (valid) {
      generator = new PBKDF1KeyGenerator(
          digest, params.getSalt(), params.getIterationCount());
      setCipher(alg);
    } else {
      throw new IllegalArgumentException(String.format(
          "Unsupported digest/cipher combination: %s/%s", digest, alg));
    }
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


  /**
   * Sets the symmetric cipher algorithm used to encrypt/decrypt data.
   *
   * @param  alg  Symmetric cipher algorithm configured for CBC mode and PKCS#5
   * (aka RFC 1423) padding.
   */
  private void setCipher(final SymmetricAlgorithm alg)
  {
    if ("CBC".equals(alg.getMode()) &&
        "PKCS5Padding".equals(alg.getPadding())) {
      cipher = alg;
    } else {
      throw new IllegalArgumentException(
          "SymmetricAlgorith must be in CBC mode with PKCS5Padding.");
    }
  }
}
