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
package org.ldaptive.io;

import java.io.StringReader;
import java.io.StringWriter;
import org.ldaptive.AbstractTest;
import org.ldaptive.Connection;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapResult;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SortBehavior;
import org.ldaptive.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link LdifReader} and {@link LdifWriter}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdifTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry14")
  @BeforeClass(groups = {"ldif"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"ldif"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "ldifSearchDn",
      "ldifSearchFilter"
    })
  @Test(groups = {"ldif"})
  public void searchAndCompareLdif(final String dn, final String filter)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final LdapResult result1 = search.execute(
        new SearchRequest(dn, new SearchFilter(filter))).getResult();

      final StringWriter writer = new StringWriter();
      final LdifWriter ldifWriter = new LdifWriter(writer);
      ldifWriter.write(result1);

      final StringReader reader = new StringReader(writer.toString());
      final LdifReader ldifReader = new LdifReader(reader);
      final LdapResult result2 = ldifReader.read();

      AssertJUnit.assertEquals(result1, result2);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  ldifFile  to create with
   * @param  ldifSortedFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "ldifEntry",
      "ldifSortedEntry"
    })
  @Test(groups = {"ldif"})
  public void readAndCompareSortedLdif(
    final String ldifFile,
    final String ldifSortedFile)
    throws Exception
  {
    final String ldifStringSorted = TestUtil.readFileIntoString(ldifSortedFile);
    final LdifReader ldifReader = new LdifReader(
      new StringReader(TestUtil.readFileIntoString(ldifFile)),
      SortBehavior.SORTED);
    final LdapResult result = ldifReader.read();

    final StringWriter writer = new StringWriter();
    final LdifWriter ldifWriter = new LdifWriter(writer);
    ldifWriter.write(result);

    AssertJUnit.assertEquals(ldifStringSorted, writer.toString());
  }


  /**
   * @param  ldifFileIn  to create with
   * @param  ldifFileOut  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "multipleLdifResultsIn",
      "multipleLdifResultsOut"
    })
  @Test(groups = {"ldif"})
  public void readAndCompareMultipleLdif(
    final String ldifFileIn,
    final String ldifFileOut)
    throws Exception
  {
    final String ldifStringIn = TestUtil.readFileIntoString(ldifFileIn);
    LdifReader ldifReader = new LdifReader(new StringReader(ldifStringIn));
    final LdapResult result1 = ldifReader.read();

    final String ldifStringOut = TestUtil.readFileIntoString(ldifFileOut);
    ldifReader = new LdifReader(new StringReader(ldifStringOut));
    final LdapResult result2 = ldifReader.read();

    AssertJUnit.assertEquals(result1, result2);
  }
}
