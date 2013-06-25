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
package edu.vt.middleware.crypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import edu.vt.middleware.crypt.util.Converter;

/**
 * Describes operations common to both symmetric and asymmetric encryption
 * algorithms.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface EncryptionAlgorithm extends Algorithm
{

  /**
   * Gets the encryption mode.
   *
   * @return  Name of an encryption mode, e.g. CBC.
   */
  String getMode();


  /**
   * Gets the encryption padding method.
   *
   * @return  Name of a padding method, e.g. PKCS5Padding.
   */
  String getPadding();


  /**
   * Sets the key used for encryption/decryption.
   *
   * @param  k  Public, private, or secret key used for encryption or
   * decryption.
   */
  void setKey(final Key k);


  /**
   * Gets the cipher mode indicating whether this instance is currently
   * initialized for encryption or decryption.
   *
   * @return  <a href="http://java.sun.com/j2se/1.5.0/docs/api/
   * javax/crypto/Cipher.html#ENCRYPT_MODE">Cipher.ENCRYPT_MODE</a>, <a
   * href="http://java.sun.com/j2se/1.5.0/docs/api/
   * javax/crypto/Cipher.html#DECRYPT_MODE">Cipher.DECRYPT_MODE</a>, or 0 if the
   * cipher mode has not been initialized by calling either {@link
   * #initEncrypt()} or {@link #initDecrypt()}.
   */
  int getCipherMode();


  /**
   * Gets the block size of the encryption algorithm cipher in bytes.
   *
   * @return  Block size of cipher in bytes, or 0 if the cipher is not a block
   * cipher.
   */
  int getBlockSize();


  /**
   * Initializes this instance for encryption operations.
   *
   * @throws  CryptException  On cryptographic configuration errors.
   */
  void initEncrypt()
    throws CryptException;


  /**
   * Initializes this instance for decryption operations.
   *
   * @throws  CryptException  On cryptographic configuration errors.
   */
  void initDecrypt()
    throws CryptException;


  /**
   * Encrypts the given plaintext bytes into a byte array of ciphertext using
   * the encryption key.
   *
   * @param  plaintext  Input plaintext bytes.
   *
   * @return  Ciphertext resulting from plaintext encryption.
   *
   * @throws  CryptException  On encryption errors.
   */
  byte[] encrypt(final byte[] plaintext)
    throws CryptException;


  /**
   * Encrypts the given plaintext bytes into a ciphertext string using the
   * conversion strategy provided by the given converter.
   *
   * @param  plaintext  Input plaintext bytes.
   * @param  converter  Converter that converts ciphertext output bytes to a
   * string representation.
   *
   * @return  Ciphertext string resulting from plaintext encryption.
   *
   * @throws  CryptException  On encryption errors.
   */
  String encrypt(final byte[] plaintext, final Converter converter)
    throws CryptException;


  /**
   * Encrypts the data in the given plaintext input stream into ciphertext in
   * the output stream. Use {@link
   * edu.vt.middleware.crypt.io.Base64FilterOutputStream} or {@link
   * edu.vt.middleware.crypt.io.HexFilterOutputStream} to produce ciphertext in
   * the output stream in an encoded string repreprestation.
   *
   * @param  in  Input stream of plaintext.
   * @param  out  Output stream of ciphertext.
   *
   * @throws  CryptException  On encryption errors.
   * @throws  IOException  On stream read/write errors.
   */
  void encrypt(final InputStream in, final OutputStream out)
    throws CryptException, IOException;


  /**
   * Decrypts the given ciphertext bytes into a byte array of plaintext using
   * the decryption key.
   *
   * @param  ciphertext  Input ciphertext bytes.
   *
   * @return  Plaintext resulting from ciphertext decryption.
   *
   * @throws  CryptException  On decryption errors.
   */
  byte[] decrypt(final byte[] ciphertext)
    throws CryptException;


  /**
   * Decrypts a string representation of ciphertext bytes into a byte array of
   * plaintext using the decryption key.
   *
   * @param  ciphertext  Input ciphertext bytes.
   * @param  converter  Converter that converts the ciphertext input string into
   * raw bytes required by the cipher algorithm.
   *
   * @return  Plaintext resulting from ciphertext decryption.
   *
   * @throws  CryptException  On decryption errors.
   */
  byte[] decrypt(final String ciphertext, final Converter converter)
    throws CryptException;


  /**
   * Decrypts the data in the given ciphertext input stream into plaintext in
   * the output stream. Use {@link
   * edu.vt.middleware.crypt.io.Base64FilterInputStream} or {@link
   * edu.vt.middleware.crypt.io.HexFilterInputStream} to consume ciphertext in
   * an encoded string representation.
   *
   * @param  in  Input stream of ciphertext.
   * @param  out  Output stream of plaintext.
   *
   * @throws  CryptException  On decryption errors.
   * @throws  IOException  On stream read/write errors.
   */
  void decrypt(final InputStream in, final OutputStream out)
    throws CryptException, IOException;

}
