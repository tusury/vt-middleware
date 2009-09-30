/*
  $Id$

  Copyright (C) 2007-2009 Virginia Tech
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.maven;

import java.io.IOException;

import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;
import edu.vt.middleware.crypt.util.CryptReader;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Maven goal which encrypts a plaintext string into a a base-64-encoded string
 * of cipher test via a configurable symmetric encryption algorithm.
 * @author Marvin S. Addison
 * @version $Revison: $
 *
 * @goal encrypt
 */
public class EncryptMojo extends AbstractCryptMojo
{
  /**
   * The plain text string to encrypt.
   * @parameter expression="${plainText}"
   * @required
   */
  protected String plainText;

  /**
   * Stores the results of the most recent encryption;
   */
  private String cipherText;

  /**
   * Performs the encryption process.
   * @throws MojoExecutionException on any error.
   */
  public void execute() throws MojoExecutionException
  {
    final SymmetricAlgorithm cipher = createCipher();
    try {
      cipher.setKey(CryptReader.readSecretKey(keyFile, algorithm));
      cipher.initEncrypt();
    } catch (IOException ioex) {
      throw new MojoExecutionException("Encryption key file IO error.", ioex);
    } catch (CryptException crex) {
      throw new MojoExecutionException("Invalid encryption key file.", crex);
    }
    logSettings();
    try {
      cipherText = cipher.encrypt(plainText.getBytes(), b64Converter);
      getLog().info(String.format("Plaintext=%s", plainText));
      getLog().info(String.format("Ciphertext=%s", cipherText));
    } catch (CryptException crex) {
      throw new MojoExecutionException("Decryption error.", crex);
    }
  }


  /**
   * Gets the ciphertext that resulted from the most recent encryption.
   * @return Ciphertext result of last encryption.
   *
   */
  public String getCipherText()
  {
    return cipherText;
  }
}
