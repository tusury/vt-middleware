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

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * Test class for <code>GenkeyMojo</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class GenkeyMojoTest extends AbstractMojoTestCase
{

  /** Test path to key file. */
  private static final String TEST_KEYFILE_PATH = "target/test.key";

  /** {@inheritDoc} */
  protected void setUp()
    throws Exception
  {
    // required for mojo lookups to work
    super.setUp();
  }

  /**
   * Tests the key generation mojo.
   *
   * @throws  Exception  On any error.
   */
  public void testGenkey()
    throws Exception
  {
    final File testPom = getTestFile("src/test/resources/genkey-test.xml");
    final GenkeyMojo mojo = (GenkeyMojo) lookupMojo("genkey", testPom);
    assertNotNull(mojo);
    mojo.execute();

    final File keyFile = getTestFile(TEST_KEYFILE_PATH);
    assertTrue(keyFile.exists());
  }
}
