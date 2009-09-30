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
import java.io.FileReader;

import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

/**
 * Test class for <code>DecryptMojo</code>.
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class DecryptMojoTest extends AbstractMojoTestCase
{
  /** Test plaintext */
  private static final String TEST_PLAINTEXT = "sekrit";


  /**
   * {@inheritDoc}
   */
  protected void setUp() throws Exception
  {
    // required for mojo lookups to work
    super.setUp();
  }

  /**
   * Tests the decryption mojo.
   *
   * @throws Exception On any error.
   */
  public void testDecrypt() throws Exception
  {
    final File testPom = getTestFile("src/test/resources/decrypt-test.xml");
    final DecryptMojo mojo = (DecryptMojo) lookupMojo("decrypt", testPom);
    final MavenXpp3Reader mr = new MavenXpp3Reader();
    mojo.project = new MavenProject(mr.read(new FileReader(testPom)));
    assertNotNull(mojo);
    mojo.execute();
    assertEquals(TEST_PLAINTEXT, mojo.getPlainText());
  }
}
