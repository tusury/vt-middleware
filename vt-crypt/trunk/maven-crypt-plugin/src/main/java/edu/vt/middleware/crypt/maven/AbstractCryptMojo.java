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

import java.io.File;

import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;
import edu.vt.middleware.crypt.util.Base64Converter;
import edu.vt.middleware.crypt.util.HexConverter;

import org.apache.maven.plugin.AbstractMojo;

/**
 * Abstract base class for crypt mojos.
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public abstract class AbstractCryptMojo extends AbstractMojo
{
  /** Converts bytes to base-64 text */
  protected final Base64Converter b64Converter = new Base64Converter();

  /** Converts hex to bytes */
  protected final HexConverter hexConverter = new HexConverter();


  /**
   * Project-relative location of encryption/decryption key file.
   * @parameter expression="${keyFile}"
   * @required
   */
  protected File keyFile;

  /**
   * Symmetric encryption algorithm, e.g. DES, AES, Blowfish.
   * @parameter expression="${algorithm}"
   * default-value="AES"
   */
  protected String algorithm;

  /**
   * Encryption mode, e.g. CBC.
   * @parameter expression="${mode}"
   * default-value="CBC"
   */
  protected String mode;

  /**
   * Padding, e.g. PKCS5Padding.
   * @parameter expression="${padding}"
   * default-value="PKCS5Padding"
   */
  protected String padding;

  /**
   * Hexadecimal initialization vector string.
   * @parameter expression="${iv}"
   * @required
   */
  protected String iv;


  /**
   * Creates a <code>SymmetricAlgorithm</code> initialized with properties
   * set on this mojo.
   * @return <code>SymmetricAlgorithm</code> object.
   */
  protected SymmetricAlgorithm createCipher()
  {
    if (algorithm == null) {
      throw new IllegalStateException("Must specify cipher algorithm.");
    }
    final SymmetricAlgorithm cipher =
      SymmetricAlgorithm.newInstance(algorithm, mode, padding);
    cipher.setIV(hexConverter.toBytes(iv));
    return cipher;
  }


  /**
   * Logs the encryption settings for the current operation at INFO level.
   *
   */
  protected void logSettings()
  {
    getLog().info("Encryption Settings:");
    getLog().info("keyFile=" + keyFile);
    getLog().info("algorithm=" + algorithm);
    getLog().info("mode=" + mode);
    getLog().info("iv=" + iv);
    getLog().info("padding=" + padding);
  }
}
