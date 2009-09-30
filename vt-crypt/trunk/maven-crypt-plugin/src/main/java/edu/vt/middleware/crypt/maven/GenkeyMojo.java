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
import edu.vt.middleware.crypt.util.CryptWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Maven goal which creates a symmetric encryption/decription key file
 * using a configurable symmetric encryption algorithm.
 * @author Marvin S. Addison
 * @version $Revison: $
 *
 * @goal genkey
 */
public class GenkeyMojo extends AbstractMojo
{
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
   * Size of key to be generated in bits.
   * @parameter expression="${keySize}"
   * @required
   */
  protected int keySize;


  /**
   * Performs the key generation process.
   * @throws MojoExecutionException on any error.
   */
  public void execute() throws MojoExecutionException
  {
    if (algorithm == null) {
      throw new IllegalStateException("Must specify cipher algorithm.");
    }
    final SymmetricAlgorithm cipher = SymmetricAlgorithm.newInstance(algorithm);
    getLog().info("Generating key with bit length " + keySize);
    try {
      getLog().info("Writing key to file " + keyFile);
      CryptWriter.writeEncodedKey(cipher.generateKey(keySize), keyFile);
    } catch (Exception ex) {
      throw new MojoExecutionException("Key file generation error.", ex);
    }
  }
}
