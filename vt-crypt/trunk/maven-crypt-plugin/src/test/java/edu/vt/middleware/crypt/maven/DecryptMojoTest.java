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
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Profile;
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
   * @throws  Exception  On any error.
   */
  public void testDecrypt() throws Exception
  {
    final MavenXpp3Reader mr = new MavenXpp3Reader();
    final Map<String, String> expectedValueMap = new HashMap<String, String>();
    expectedValueMap.put("test.password.1", "sekrit");
    expectedValueMap.put("test.password.2", "snagglefish");

    // Test case 1: decrypt project properties
    final File pomFile1 =
      getTestFile("src/test/resources/decrypt-project-property-test.xml");
    final DecryptMojo mojo1 = (DecryptMojo) lookupMojo("decrypt", pomFile1);
    mojo1.project = new MavenProject(mr.read(new FileReader(pomFile1)));
    assertNotNull(mojo1);
    mojo1.execute();
    for (String property : expectedValueMap.keySet()) {
      assertEquals(
          expectedValueMap.get(property),
          mojo1.project.getProperties().get(property));
    }

    // Test case 2: decrypt profile properties
    final File pomFile2 =
      getTestFile("src/test/resources/decrypt-profile-property-test.xml");
    final DecryptMojo mojo2 = (DecryptMojo) lookupMojo("decrypt", pomFile2);
    mojo2.project = new MavenProject(mr.read(new FileReader(pomFile2)));
    assertNotNull(mojo2);
    mojo2.execute();
    for (String property : expectedValueMap.keySet()) {
      // Test file has only one profile that is active by default
      final Profile profile =
        (Profile) mojo2.project.getModel().getProfiles().get(0);
      assertEquals(
          expectedValueMap.get(property),
          profile.getProperties().get(property));
    }
  }
}
