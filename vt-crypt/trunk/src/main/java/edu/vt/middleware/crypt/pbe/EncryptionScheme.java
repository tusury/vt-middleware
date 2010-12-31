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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.vt.middleware.crypt.CryptException;

/**
 * Describes a password-based encryption scheme.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public interface EncryptionScheme
{
  /**
   * Encrypts the given plaintext bytes into a byte array of ciphertext using
   * an encryption key derived from the password.
   *
   * @param  password  Basis for encryption.
   * @param  plaintext  Input plaintext bytes.
   *
   * @return  Ciphertext resulting from plaintext encryption.
   *
   * @throws  CryptException  On encryption errors.
   */
  byte[] encrypt(char[] password, byte[] plaintext)
    throws CryptException;


  /**
   * Encrypts the data in the given plaintext input stream into ciphertext in
   * the output stream. Use {@link
   * edu.vt.middleware.crypt.io.Base64FilterOutputStream} or {@link
   * edu.vt.middleware.crypt.io.HexFilterOutputStream} to produce ciphertext in
   * the output stream in an encoded string repreprestation.
   *
   * @param  password  Basis for encryption.
   * @param  in  Input stream of plaintext.
   * @param  out  Output stream of ciphertext.
   *
   * @throws  CryptException  On encryption errors.
   * @throws  IOException  On stream read/write errors.
   */
  void encrypt(char[] password, InputStream in, OutputStream out)
    throws CryptException, IOException;


  /**
   * Decrypts the given ciphertext bytes into a byte array of plaintext using
   * a decryption key based on the given password.
   *
   * @param  password  Basis for encryption.
   * @param  ciphertext  Input ciphertext bytes.
   *
   * @return  Plaintext resulting from ciphertext decryption.
   *
   * @throws  CryptException  On decryption errors.
   */
  byte[] decrypt(char[] password, byte[] ciphertext)
    throws CryptException;


  /**
   * Decrypts the data in the given ciphertext input stream into plaintext in
   * the output stream. Use {@link
   * edu.vt.middleware.crypt.io.Base64FilterInputStream} or {@link
   * edu.vt.middleware.crypt.io.HexFilterInputStream} to consume ciphertext in
   * an encoded string representation.
   *
   * @param  password  Basis for encryption.
   * @param  in  Input stream of ciphertext.
   * @param  out  Output stream of plaintext.
   *
   * @throws  CryptException  On decryption errors.
   * @throws  IOException  On stream read/write errors.
   */
  void decrypt(char[] password, InputStream in, OutputStream out)
    throws CryptException, IOException;
}
