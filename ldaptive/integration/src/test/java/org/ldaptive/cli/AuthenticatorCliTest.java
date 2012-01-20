/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.ldaptive.AbstractTest;
import org.ldaptive.LdapEntry;
import org.ldaptive.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link AuthenticatorCli} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
@Test(dependsOnGroups = { "ssl", "ssl-hostname" })
public class AuthenticatorCliTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry9")
  @BeforeClass(groups = {"authcli"}, dependsOnGroups = { "ssl", "ssl-hostname" })
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);

    System.setProperty(
      "javax.net.ssl.trustStore",
      "target/test-classes/ldaptive.truststore");
    System.setProperty("javax.net.ssl.trustStoreType", "BKS");
    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"authcli"}, dependsOnGroups = { "ssl", "ssl-hostname" })
  public void deleteLdapEntry()
    throws Exception
  {
    System.clearProperty("javax.net.ssl.trustStore");
    System.clearProperty("javax.net.ssl.trustStoreType");
    System.clearProperty("javax.net.ssl.trustStorePassword");

    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  args  List of delimited arguments to pass to the CLI.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "cliAuthTLSArgs", "cliAuthResults" })
  @Test(groups = {"authcli"})
  public void authenticateTLS(final String args, final String ldifFile)
    throws Exception
  {
    authenticate(args, ldifFile);
  }


  /**
   * @param  args  List of delimited arguments to pass to the CLI.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "cliAuthSSLArgs", "cliAuthResults" })
  @Test(groups = {"authcli"})
  public void authenticateSSL(final String args, final String ldifFile)
    throws Exception
  {
    authenticate(args, ldifFile);
  }


  /**
   * @param  args  List of delimited arguments to pass to the CLI.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  private void authenticate(final String args, final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      AuthenticatorCli.main(args.split("\\|"));
      AssertJUnit.assertEquals(
        TestUtil.convertLdifToResult(ldif),
        TestUtil.convertLdifToResult(outStream.toString()));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
