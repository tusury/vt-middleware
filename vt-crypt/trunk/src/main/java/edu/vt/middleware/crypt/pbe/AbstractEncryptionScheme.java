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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

/**
 * Abstract base class for password-based encryption schemes based on salt data
 * and iterated hashing as the basis of the key derivation function.
 *
 * <p>NOTE: Classes derived from this class are not thread safe. In particular,
 * care should be take to prevent multiple threads from performing encryption
 * and/or decryption concurrently.</p>
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractEncryptionScheme implements EncryptionScheme
{

  /** Cipher used for encryption and decryption. */
  protected SymmetricAlgorithm cipher;


  /** {@inheritDoc} */
  public byte[] encrypt(final char[] password, final byte[] plaintext)
    throws CryptException
  {
    initCipher(password);
    cipher.initEncrypt();
    return cipher.encrypt(plaintext);
  }


  /** {@inheritDoc} */
  public void encrypt(
    final char[] password,
    final InputStream in,
    final OutputStream out)
    throws CryptException, IOException
  {
    initCipher(password);
    cipher.initEncrypt();
    cipher.encrypt(in, out);
  }


  /** {@inheritDoc} */
  public byte[] decrypt(final char[] password, final byte[] ciphertext)
    throws CryptException
  {
    initCipher(password);
    cipher.initDecrypt();
    return cipher.decrypt(ciphertext);
  }


  /** {@inheritDoc} */
  public void decrypt(
    final char[] password,
    final InputStream in,
    final OutputStream out)
    throws CryptException, IOException
  {
    initCipher(password);
    cipher.initDecrypt();
    cipher.decrypt(in, out);
  }


  /**
   * Sets the symmetric algorithm cipher.
   *
   * @param  alg  Symmetric algorithm instance.
   */
  protected void setCipher(final SymmetricAlgorithm alg)
  {
    if (alg == null) {
      throw new IllegalArgumentException("Cipher cannot be null.");
    }
    this.cipher = alg;
  }


  /**
   * Initializes the cipher with the given PBE derived key bytes.
   *
   * @param  password  PBE password.
   */
  protected abstract void initCipher(final char[] password);
}
