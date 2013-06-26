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

import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

/**
 * Password-based encryption scheme used by OpenSSL for encrypting private keys.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class OpenSSLEncryptionScheme
  extends AbstractVariableKeySizeEncryptionScheme
{

  /**
   * Creates a new instance using the given parameters.
   *
   * @param  alg  Symmetric cipher algorithm used for encryption/decryption. The
   * cipher is expected to be initialized with data required for use except, of
   * course, the private key which will be generated from a password.
   * @param  salt  Salt data for key generation function.
   * @param  keyBitLength  Size of derived keys in bits.
   */
  public OpenSSLEncryptionScheme(
    final SymmetricAlgorithm alg,
    final byte[] salt,
    final int keyBitLength)
  {
    setCipher(alg);
    setGenerator(new OpenSSLKeyGenerator(salt));
    setKeyLength(keyBitLength);
  }


  /**
   * Creates a new instance from an algorithm identifier string and salt data.
   *
   * @param  algId  Identifier describing the cipher used for key encryption.
   * The form of the identifier follows the following convention:<br>
   * $ALGORITHM-$MODE
   * @param  iv  Cipher initialization vector data.
   */
  public OpenSSLEncryptionScheme(final String algId, final byte[] iv)
  {
    final String alg;
    String mode = "CBC";
    String padding = "PKCS5Padding";
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
      setKeyLength(192);
    } else if (algId.startsWith("DES")) {
      alg = "DES";
      setKeyLength(64);
    } else if (algId.startsWith("RC2")) {
      alg = "RC2";
      if (algId.startsWith("RC2-40")) {
        setKeyLength(40);
      } else if (algId.startsWith("RC2-64")) {
        setKeyLength(64);
      } else {
        setKeyLength(128);
      }
    } else if (algId.startsWith("AES")) {
      alg = "AES";
      if (algId.startsWith("AES-128")) {
        setKeyLength(128);
      } else if (algId.startsWith("AES-192")) {
        setKeyLength(192);
      } else if (algId.startsWith("AES-256")) {
        setKeyLength(256);
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
    setCipher(SymmetricAlgorithm.newInstance(alg, mode, padding));
    setGenerator(new OpenSSLKeyGenerator(salt));
    this.cipher.setIV(iv);
  }
}
