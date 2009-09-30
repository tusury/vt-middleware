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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * Test class for <code>EncryptMojo</code>.
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class EncryptMojoTest extends AbstractMojoTestCase
{
  /** Test ciphertext corresponding to plaintext (using test.key) */
  private static final String TEST_CIPHERTEXT = "Hpvc/gZ/1DGqG0dzIeNlvw==";

  /**
   * {@inheritDoc}
   */
  protected void setUp() throws Exception
  {
    // required for mojo lookups to work
    super.setUp();
  }

  /**
   * Tests the encryption mojo.
   *
   * @throws Exception On any error.
   */
  public void testEncrypt() throws Exception
  {
    final File testPom = getTestFile("src/test/resources/encrypt-test.xml");
    final EncryptMojo mojo = (EncryptMojo) lookupMojo("encrypt", testPom);
    assertNotNull(mojo);
    mojo.execute();
    assertEquals(TEST_CIPHERTEXT, mojo.getCipherText());
  }
}
