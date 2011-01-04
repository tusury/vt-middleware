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

import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

/**
 * Password-based encryption scheme used by OpenSSL for encrypting private keys.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class OpenSSLEncryptionScheme extends AbstractEncryptionScheme
{
  /**
   * Creates a new instance using the given parameters.
   *
   * @param  alg  Symmetric cipher algorithm used for encryption/decryption.
   * The cipher is expected to be initialized with data required for use
   * except, of course, the private key which will be generated from a password.
   * @param  salt  Salt data for key generation function.
   * @param  keyBitLength  Size of derived keys in bits.
   */
  public OpenSSLEncryptionScheme(
      final SymmetricAlgorithm alg, final byte[] salt, final int keyBitLength)
  {
    this.generator = new OpenSSLKeyGenerator(keyBitLength, salt);
    this.cipher = alg;
  }


  /**
   * Creates a new instance from an algorithm identifier string and salt data.
   *
   * @param  algId  Identifier describing the cipher used for key
   * encryption.  The form of the identifier follows the following convention:
   * <br>
   * $ALGORITHM-$MODE
   * @param  iv  Cipher initialization vector data.
   */
  public OpenSSLEncryptionScheme(final String algId, final byte[] iv)
  {
    final String alg;
    String mode = "CBC";
    String padding = "PKCS5Padding";
    final int keyBitLength;
    if (algId.endsWith("-CFB")) {
      mode = "CFB";
      padding = "NoPadding";
    } else if (algId.endsWith("-OFB")) {
      mode = "CFB";
      padding = "NoPadding";
    } else if (algId.endsWith("-ECB")) {
      mode = "ECB";
    }
    if (algId.startsWith("DES-EDE3")) {
      alg = "DESede";
      keyBitLength = 192;
    } else if (algId.startsWith("DES")) {
      alg = "DES";
      keyBitLength = 64;
    } else if (algId.startsWith("RC2")) {
      alg = "RC2";
      if (algId.startsWith("RC2-40")) {
        keyBitLength = 40;
      } else if (algId.startsWith("RC2-64")) {
        keyBitLength = 64;
      } else {
        keyBitLength = 128;
      }
    } else if (algId.startsWith("AES")) {
      alg = "AES";
      if (algId.startsWith("AES-128")) {
        keyBitLength = 128;
      } else if (algId.startsWith("AES-192")) {
        keyBitLength = 192;
      } else if (algId.startsWith("AES-256")) {
        keyBitLength = 256;
      } else {
        throw new IllegalArgumentException("Unknown AES cipher " + algId);
      }
    } else {
      throw new IllegalArgumentException("Unknown algorithm " + algId);
    }
    byte[] salt = iv;
    if (iv.length > 8) {
      salt = new byte[8];
      System.arraycopy(iv, 0, salt, 0, 8);
    }
    this.cipher = SymmetricAlgorithm.newInstance(alg, mode, padding);
    this.cipher.setIV(iv);
    this.generator = new OpenSSLKeyGenerator(keyBitLength, salt);
  }


  /** {@inheritDoc} */
  @Override
  protected void initCipher(final byte[] derivedKey)
  {
    cipher.setKey(new SecretKeySpec(derivedKey, cipher.getAlgorithm()));
  }
}
