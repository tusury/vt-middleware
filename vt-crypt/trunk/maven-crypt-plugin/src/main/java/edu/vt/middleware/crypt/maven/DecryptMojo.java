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
import java.util.Properties;

import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;
import edu.vt.middleware.crypt.util.CryptReader;

import org.apache.maven.model.Profile;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Maven goal which decrypts a base-64-encoded property value into its
 * plaintext representation in another property.
 *
 * @author Marvin S. Addison
 * @version $Revison: $
 *
 * @goal decrypt
 * @phase process-resources
 */
public class DecryptMojo extends AbstractCryptMojo
{
  /**
   * Maven project context in which this plugin is executing.
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project;

  /**
   * The name of the property containing the base-64-encoded cipher
   * text to decrypt.  The property value will be set to the resulting
   * plain text after decryption.
   * @parameter expression="${cipherTextProperty}"
   * @required
   */
  protected String cipherTextProperty;

  /**
   * The name of the property that will receive the resulting decrypted
   * plaintext after decryption.
   * @parameter expression="${plainTextProperty}"
   * @required
   */
  protected String plainTextProperty;

  /** Holds plaintext after decryption */
  private String plainText;


  /**
   * Performs the decryption process.
   * @throws MojoExecutionException on any error.
   */
  public void execute() throws MojoExecutionException
  {
    final SymmetricAlgorithm cipher = createCipher();
    try {
      cipher.setKey(CryptReader.readSecretKey(keyFile, algorithm));
      cipher.initDecrypt();
    } catch (IOException ioex) {
      throw new MojoExecutionException("Decryption key file IO error.", ioex);
    } catch (CryptException crex) {
      throw new MojoExecutionException("Invalid decryption key file.", crex);
    }
    final Properties srcProps = getProps(cipherTextProperty);
    if (srcProps == null) {
      throw new MojoExecutionException(
        String.format("Property \"%s\" not found in project.",
          cipherTextProperty));
    }
    final Properties dstProps = getProps(plainTextProperty);
    if (dstProps == null) {
      throw new MojoExecutionException(
        String.format("Property \"%s\" not found in project.",
            plainTextProperty));
    }
    logSettings();
    final String cipherText = srcProps.getProperty(cipherTextProperty);
    try {
      plainText = new String(cipher.decrypt(cipherText, b64Converter));
      dstProps.setProperty(plainTextProperty, plainText);
    } catch (CryptException crex) {
      throw new MojoExecutionException("Decryption error.", crex);
    }
  }

  /**
   * Gets the plaintext that resulted from the most recent decryption.
   * @return Plaintext result of last decryption.
   *
   */
  public String getPlainText()
  {
    return plainText;
  }

  /**
   * Looks for a set of properties in the project containing the given key.
   * @param key Search key.
   * @return Set of properties containing given key or null if no properties
   * can be found.
   */
  private Properties getProps(final String key)
  {
    Properties properties = null;
    if (project.getProperties().containsKey(key)) {
      properties = project.getProperties();
    } else {
      for (Object o : project.getModel().getProfiles()) {
        final Profile p = (Profile) o;
        if (p.getProperties().containsKey(key)) {
          properties = p.getProperties();
          break;
        }
      }
    }
    return properties;
  }
}
