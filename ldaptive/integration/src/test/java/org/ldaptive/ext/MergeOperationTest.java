/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.ext;

import org.ldaptive.AbstractTest;
import org.ldaptive.Connection;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.TestUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link MergeOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class MergeOperationTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry30")
  @BeforeClass(groups = {"merge"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtils.readFileIntoString(ldifFile);
    testLdapEntry = TestUtils.convertLdifToResult(ldif).getEntry();
    final Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      AssertJUnit.assertFalse(super.entryExists(conn, testLdapEntry));
      final MergeOperation merge = new MergeOperation(conn);
      merge.execute(new MergeRequest(testLdapEntry));
      AssertJUnit.assertTrue(super.entryExists(conn, testLdapEntry));
    } finally {
      conn.close();
    }
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"merge"})
  public void deleteLdapEntry()
    throws Exception
  {
    final Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      AssertJUnit.assertTrue(super.entryExists(conn, testLdapEntry));
      final MergeOperation merge = new MergeOperation(conn);
      merge.execute(new MergeRequest(testLdapEntry, true));
      AssertJUnit.assertFalse(super.entryExists(conn, testLdapEntry));
      merge.execute(new MergeRequest(testLdapEntry, true));
    } finally {
      conn.close();
    }
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"merge"})
  public void merge()
    throws Exception
  {
    final LdapEntry source = new LdapEntry(
      testLdapEntry.getDn(), testLdapEntry.getAttributes());

    final LdapAttribute gn = new LdapAttribute("givenName");
    gn.addStringValues(source.getAttribute("givenName").getStringValues());
    gn.addStringValue("John");
    gn.addStringValue("John");
    source.addAttribute(gn);
    final LdapAttribute cn = new LdapAttribute("cn");
    cn.addStringValues(source.getAttribute("cn").getStringValues());
    cn.addStringValue("John Calvin Coolidge, Jr.");
    source.addAttribute(cn);

    final Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final MergeOperation merge = new MergeOperation(conn);
      final MergeRequest request = new MergeRequest(source);
      request.setIgnoreAttributes(new String[] {"givenName", "cn"});
      merge.execute(request);

      final SearchOperation search = new SearchOperation(conn);
      SearchResult result = search.execute(
        SearchRequest.newObjectScopeSearchRequest(source.getDn())).getResult();
      TestUtils.assertEquals(testLdapEntry, result.getEntry());

      request.setIgnoreAttributes(null);
      merge.execute(request);

      result = search.execute(
        SearchRequest.newObjectScopeSearchRequest(source.getDn())).getResult();
      TestUtils.assertEquals(source, result.getEntry());
    } finally {
      conn.close();
    }
  }
}
