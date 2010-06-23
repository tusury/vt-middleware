/*
  $Id$

  Copyright (C) 2007-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
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
 * Maven goal which decrypts a base-64-encoded property value into its plaintext
 * representation in another property.
 *
 * @author  Middleware Services
 * @version  $Revison: $
 * @goal  decrypt
 * @phase  process-resources
 */
public class DecryptMojo extends AbstractCryptMojo
{

  /**
   * Maven project context in which this plugin is executing.
   *
   * @parameter  default-value="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project;

  /**
   * The names of properties whose values are base-64-encoded ciphertext to be
   * decrypted. The property value will be set to the resulting plain text after
   * decryption.
   *
   * @parameter  expression="${property}"
   * @required
   */
  protected String[] properties;

  /** Holds plaintext after decryption. */
  private String plainText;


  /**
   * Performs the decryption process.
   *
   * @throws  MojoExecutionException  on any error.
   */
  public void execute()
    throws MojoExecutionException
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
    logSettings();
    try {
      for (String property : properties) {
        decrypt(cipher, property);
      }
    } catch (CryptException crex) {
      throw new MojoExecutionException("Decryption error.", crex);
    }
  }


  /**
   * Decrypts the base-64-encoded ciphertext in the given property and sets the
   * property to resulting plaintext.
   *
   * @param  cipher  Decryption cipher.
   * @param  property  Name of property whose value will be decrypted.
   *
   * @throws  MojoExecutionException  On configuration errors.
   * @throws  CryptException  On decryption errors.
   */
  private void decrypt(final SymmetricAlgorithm cipher, final String property)
    throws CryptException, MojoExecutionException
  {
    final Properties props = getProps(property);
    if (props == null) {
      throw new MojoExecutionException(
        String.format("Property \"%s\" not found in project.", property));
    }
    plainText = new String(
      cipher.decrypt(props.getProperty(property), b64Converter));
    props.setProperty(property, plainText);
  }


  /**
   * Looks for a set of properties in the project containing the given key.
   *
   * @param  key  Search key.
   *
   * @return  Set of properties containing given key or null if no properties
   * can be found.
   */
  private Properties getProps(final String key)
  {
    Properties props = null;
    if (project.getProperties().containsKey(key)) {
      props = project.getProperties();
    } else {
      for (Object o : project.getModel().getProfiles()) {
        final Profile p = (Profile) o;
        if (p.getProperties().containsKey(key)) {
          props = p.getProperties();
          break;
        }
      }
    }
    return props;
  }
}
