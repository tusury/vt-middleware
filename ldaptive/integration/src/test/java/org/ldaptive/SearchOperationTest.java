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
package org.ldaptive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ldaptive.control.PagedResultsControl;
import org.ldaptive.control.SortKey;
import org.ldaptive.control.SortRequestControl;
import org.ldaptive.handler.CaseChangeEntryHandler;
import org.ldaptive.handler.CaseChangeEntryHandler.CaseChange;
import org.ldaptive.handler.DnAttributeEntryHandler;
import org.ldaptive.handler.LdapEntryHandler;
import org.ldaptive.handler.MergeAttributeEntryHandler;
import org.ldaptive.handler.NoOpEntryHandler;
import org.ldaptive.handler.RecursiveEntryHandler;
import org.ldaptive.handler.ext.RangeEntryHandler;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.PooledConnectionFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SearchOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SearchOperationTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;

  /** Entry created for ldap tests. */
  private static LdapEntry specialCharsLdapEntry;

  /** Entries for group tests. */
  private static Map<String, LdapEntry[]> groupEntries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of group entries.
   */
  static {
    for (int i = 2; i <= 5; i++) {
      groupEntries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }

  /** Connection instance for concurrency testing. */
  protected Connection singleConn;


  /**
   * Default constructor.
   *
   * @throws  Exception  On test failure.
   */
  public SearchOperationTest()
    throws Exception
  {
    singleConn = TestUtils.createConnection();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @BeforeClass(groups = {"search"})
  public void openConnection()
    throws Exception
  {
    singleConn.open();
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry2")
  @BeforeClass(groups = {"search", "searchInit"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtils.readFileIntoString(ldifFile);
    testLdapEntry = TestUtils.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /**
   * @param  ldifFile2  to create.
   * @param  ldifFile3  to create.
   * @param  ldifFile4  to create.
   * @param  ldifFile5  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "createGroup2",
      "createGroup3",
      "createGroup4",
      "createGroup5"
    }
  )
  @BeforeClass(groups = {"search"}, dependsOnGroups = {"searchInit"})
  public void createGroupEntry(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4,
    final String ldifFile5)
    throws Exception
  {
    // CheckStyle:Indentation OFF
    groupEntries.get("2")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile2)).getEntry();
    groupEntries.get("3")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile3)).getEntry();
    groupEntries.get("4")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile4)).getEntry();
    groupEntries.get("5")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile5)).getEntry();
    // CheckStyle:Indentation ON

    for (Map.Entry<String, LdapEntry[]> e : groupEntries.entrySet()) {
      super.createLdapEntry(e.getValue()[0]);
    }

    final String baseDn = DnParser.substring(
      groupEntries.get("2")[0].getDn(), 1);
    // setup group relationships
    final Connection conn = TestUtils.createSetupConnection();
    try {
      conn.open();
      final ModifyOperation modify = new ModifyOperation(conn);
      try {
        modify.execute(new ModifyRequest(
          groupEntries.get("2")[0].getDn(),
            new AttributeModification(
              AttributeModificationType.ADD,
              new LdapAttribute(
                "member", new String[]{ "cn=Group 3," + baseDn}))));
      } catch (LdapException e) {
        // ignore attribute already exists
        if (ResultCode.ATTRIBUTE_OR_VALUE_EXISTS != e.getResultCode()) {
          throw e;
        }
      }
      try {
        modify.execute(new ModifyRequest(
          groupEntries.get("3")[0].getDn(),
            new AttributeModification(
              AttributeModificationType.ADD,
              new LdapAttribute(
                "member",
                new String[]{
                  "cn=Group 4," + baseDn, "cn=Group 5," + baseDn, }))));
      } catch (LdapException e) {
        // ignore attribute already exists
        if (ResultCode.ATTRIBUTE_OR_VALUE_EXISTS != e.getResultCode()) {
          throw e;
        }
      }
      try {
        modify.execute(new ModifyRequest(
          groupEntries.get("4")[0].getDn(),
            new AttributeModification(
              AttributeModificationType.ADD,
              new LdapAttribute(
                "member", new String[]{ "cn=Group 3," + baseDn}))));
      } catch (LdapException e) {
        // ignore attribute already exists
        if (ResultCode.ATTRIBUTE_OR_VALUE_EXISTS != e.getResultCode()) {
          throw e;
        }
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createSpecialCharsEntry")
  @BeforeClass(groups = {"search"})
  public void createSpecialCharsEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtils.readFileIntoString(ldifFile);
    specialCharsLdapEntry = TestUtils.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(specialCharsLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"search"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
    super.deleteLdapEntry(specialCharsLdapEntry.getDn());
    super.deleteLdapEntry(groupEntries.get("2")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("3")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("4")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("5")[0].getDn());
  }


  /**
   * @param  createNew  whether to construct a new connection.
   *
   * @return  connection
   *
   * @throws  Exception  On connection failure.
   */
  public Connection createLdapConnection(final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtils.createConnection();
    }
    return singleConn;
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchDn",
      "searchFilter",
      "searchFilterParameters",
      "searchReturnAttrs",
      "searchResults"
    }
  )
  @Test(
    groups = {"search"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void search(
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));

    final String expected = TestUtils.readFileIntoString(ldifFile);

    final LdapResult entryDnResult = TestUtils.convertLdifToResult(expected);
    entryDnResult.getEntry().addAttribute(
      new LdapAttribute("entryDN", entryDnResult.getEntry().getDn()));

    // test searching
    LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterParameters.split("\\|")),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);

    // test searching no attributes
    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterParameters.split("\\|")),
        new String[]{})).getResult();
    AssertJUnit.assertTrue(result.getEntry().getAttributes().isEmpty());

    // test searching without handler
    final SearchRequest sr = new SearchRequest(
      dn,
      new SearchFilter(filter, filterParameters.split("\\|")),
      returnAttrs.split("\\|"));
    sr.setLdapEntryHandlers(new LdapEntryHandler[0]);
    result = search.execute(sr).getResult();
    AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);

    // test searching with multiple handlers
    final DnAttributeEntryHandler srh = new DnAttributeEntryHandler();
    sr.setLdapEntryHandlers(new NoOpEntryHandler(), srh);
    result = search.execute(sr).getResult();
    // ignore the case of entryDN; some directories return those in mixed case
    AssertJUnit.assertEquals(
      0,
      (new LdapEntryIgnoreCaseComparator("entryDN")).compare(
        entryDnResult.getEntry(), result.getEntry()));

    // test that entry dn handler is no-op if attribute name conflicts
    srh.setDnAttributeName("givenName");
    sr.setLdapEntryHandlers(new NoOpEntryHandler(), srh);
    result = search.execute(sr).getResult();
    // ignore the case of entryDN; some directories return those in mixed case
    AssertJUnit.assertEquals(
      0,
      (new LdapEntryIgnoreCaseComparator("entryDN")).compare(
        TestUtils.convertLdifToResult(expected).getEntry(),
        result.getEntry()));
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "pagedSearchDn",
      "pagedSearchFilter",
      "pagedSearchResults"
    })
  @Test(groups = {"search"})
  public void pagedSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final PagedResultsControl prc = new PagedResultsControl(1, true);
    final Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final String expected = TestUtils.readFileIntoString(ldifFile);

      // test searching
      final SearchRequest request = new SearchRequest(
        dn, new SearchFilter(filter));
      request.setControls(prc);
      final LdapResult result = search.execute(request).getResult();
      AssertJUnit.assertEquals(
        TestUtils.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "sortSearchDn",
      "sortSearchFilter"
    })
  @Test(groups = {"search"})
  public void sortedSearch(
    final String dn,
    final String filter)
    throws Exception
  {
    final Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final SearchRequest request = new SearchRequest(
        dn, new SearchFilter(filter));
      request.setSortBehavior(SortBehavior.ORDERED);

      // test sort by uugid
      SortRequestControl src = new SortRequestControl(
        new SortKey[] {new SortKey("uugid", "caseExactMatch")}, true);
      request.setControls(src);
      LdapResult result = search.execute(request).getResult();

      // confirm sorted
      int i = 2;
      for (LdapEntry e : result.getEntries()) {
        AssertJUnit.assertEquals(
          String.valueOf(2000 + i), e.getAttribute("uid").getStringValue());
        i++;
      }

      // test sort by uid
      src = new SortRequestControl(
        new SortKey[] {new SortKey("uid", "integerMatch", true)}, true);
      request.setControls(src);
      result = search.execute(request).getResult();

      // confirm sorted
      i = 5;
      for (LdapEntry e : result.getEntries()) {
        AssertJUnit.assertEquals(
          String.valueOf(2000 + i), e.getAttribute("uid").getStringValue());
        i--;
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "recursiveSearchDn",
      "recursiveSearchFilter",
      "recursiveSearchFilterParameters",
      "recursiveSearchReturnAttrs",
      "recursiveHandlerResults"
    }
  )
  @Test(groups = {"search"})
  public void recursiveHandlerSearch(
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));

    final String expected = TestUtils.readFileIntoString(ldifFile);

    // test recursive searching
    final RecursiveEntryHandler rsrh = new RecursiveEntryHandler(
      "member",
      new String[] {"uugid", "uid"});

    final SearchRequest sr = new SearchRequest(
      dn,
      new SearchFilter(filter, filterParameters.split("\\|")),
      returnAttrs.split("\\|"));
    sr.setLdapEntryHandlers(rsrh);
    final LdapResult result = search.execute(sr).getResult();
    // ignore the case of member and contactPerson; some directories return
    // those in mixed case
    AssertJUnit.assertEquals(
      0,
      (new LdapEntryIgnoreCaseComparator("member", "contactPerson")).compare(
        TestUtils.convertLdifToResult(expected).getEntry(),
        result.getEntry()));
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "mergeSearchDn",
      "mergeSearchFilter",
      "mergeSearchReturnAttrs",
      "mergeSearchResults"
    })
  @Test(groups = {"search"})
  public void mergeSearch(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final String expected = TestUtils.readFileIntoString(ldifFile);

      // test result merge
      final SearchRequest sr = new SearchRequest(
        dn, new SearchFilter(filter), returnAttrs.split("\\|"));
      sr.setSortBehavior(SortBehavior.SORTED);
      final LdapResult result = search.execute(sr).getResult();
      // ignore the case of member and contactPerson; some directories return
      // those in mixed case
      AssertJUnit.assertEquals(
        0,
        (new LdapEntryIgnoreCaseComparator("member", "contactPerson")).compare(
          TestUtils.convertLdifToResult(expected).getEntry(),
          LdapResult.mergeEntries(result).getEntry()));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "mergeDuplicateSearchDn",
      "mergeDuplicateSearchFilter",
      "mergeDuplicateReturnAttrs",
      "mergeDuplicateSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void mergeDuplicateSearch(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final String expected = TestUtils.readFileIntoString(ldifFile);

      // test result merge
      final SearchRequest sr = new SearchRequest(
        dn, new SearchFilter(filter), returnAttrs.split("\\|"));
      sr.setSortBehavior(SortBehavior.SORTED);
      final LdapResult result = search.execute(sr).getResult();
      // ignore the case of member and contactPerson; some directories return
      // those in mixed case
      AssertJUnit.assertEquals(
        0,
        (new LdapEntryIgnoreCaseComparator("member", "contactPerson")).compare(
          TestUtils.convertLdifToResult(expected).getEntry(),
          LdapResult.mergeEntries(result).getEntry()));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "mergeAttributeSearchDn",
      "mergeAttributeSearchFilter",
      "mergeAttributeReturnAttrs",
      "mergeAttributeSearchResults"
    })
  @Test(groups = {"search"})
  public void mergeAttributeSearch(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final String expected = TestUtils.readFileIntoString(ldifFile);

      // test merge searching
      final MergeAttributeEntryHandler handler =
        new MergeAttributeEntryHandler();
      handler.setMergeAttributeName("cn");
      handler.setAttributeNames(
        new String[] {"displayName", "givenName", "sn", });

      final SearchRequest sr = new SearchRequest(
        dn,
        new SearchFilter(filter),
        returnAttrs.split("\\|"));
      sr.setLdapEntryHandlers(handler);
      sr.setSortBehavior(SortBehavior.SORTED);
      final LdapResult result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  returnAttr  to return from search.
   * @param  base64Value  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "binarySearchDn",
      "binarySearchFilter",
      "binarySearchReturnAttr",
      "binarySearchResult"
    }
  )
  @Test(groups = {"search"})
  public void binarySearch(
    final String dn,
    final String filter,
    final String returnAttr,
    final String base64Value)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));

    // test binary searching
    SearchRequest request = new SearchRequest(
      dn,
      new SearchFilter(filter),
      new String[] {returnAttr});
    request.setBinaryAttributes(new String[]{returnAttr});
    LdapResult result = search.execute(request).getResult();
    AssertJUnit.assertNotSame(
      base64Value,
      result.getEntry().getAttribute().getStringValue());

    request = new SearchRequest(
      dn, new SearchFilter(filter), new String[] {returnAttr});
    request.setBinaryAttributes(new String[]{returnAttr});
    result = search.execute(request).getResult();
    AssertJUnit.assertEquals(
      base64Value,
      result.getEntry().getAttribute().getStringValue());
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
      {
        "searchDn",
        "searchFilter",
        "searchFilterParameters",
        "searchReturnAttrs",
        "searchResults"
      }
    )
  @Test(groups = {"search"})
  public void caseChangeSearch(
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final CaseChangeEntryHandler srh =
        new CaseChangeEntryHandler();
      final String expected = TestUtils.readFileIntoString(ldifFile);

      // test no case change
      final LdapResult noChangeResult = TestUtils.convertLdifToResult(expected);
      SearchRequest sr = new SearchRequest(
        dn,
        new SearchFilter(filter, filterParameters.split("\\|")),
        returnAttrs.split("\\|"));
      sr.setLdapEntryHandlers(srh);
      LdapResult result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(noChangeResult, result);

      // test lower case attribute values
      srh.setAttributeValueCaseChange(CaseChange.LOWER);
      final LdapResult lcValuesChangeResult = TestUtils.convertLdifToResult(
        expected);
      for (LdapAttribute la : lcValuesChangeResult.getEntry().getAttributes()) {
        final Set<String> s = new HashSet<String>();
        for (String value : la.getStringValues()) {
          s.add(value.toLowerCase());
        }
        la.clear();
        la.addStringValues(s);
      }
      sr = new SearchRequest(
        dn,
        new SearchFilter(filter, filterParameters.split("\\|")),
        returnAttrs.split("\\|"));
      sr.setLdapEntryHandlers(srh);
      result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(lcValuesChangeResult, result);

      // test upper case attribute names
      srh.setAttributeValueCaseChange(CaseChange.NONE);
      srh.setAttributeNameCaseChange(CaseChange.UPPER);
      final LdapResult ucNamesChangeResult = TestUtils.convertLdifToResult(
        expected);
      for (LdapAttribute la : ucNamesChangeResult.getEntry().getAttributes()) {
        la.setName(la.getName().toUpperCase());
      }
      sr = new SearchRequest(
        dn,
        new SearchFilter(filter, filterParameters.split("\\|")),
        returnAttrs.split("\\|"));
      sr.setLdapEntryHandlers(srh);
      result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(ucNamesChangeResult, result);

      // test lower case everything
      srh.setAttributeValueCaseChange(CaseChange.LOWER);
      srh.setAttributeNameCaseChange(CaseChange.LOWER);
      srh.setDnCaseChange(CaseChange.LOWER);
      final LdapResult lcAllChangeResult = TestUtils.convertLdifToResult(
        expected);
      for (LdapAttribute la : ucNamesChangeResult.getEntry().getAttributes()) {
        lcAllChangeResult.getEntry().setDn(
          lcAllChangeResult.getEntry().getDn().toLowerCase());
        la.setName(la.getName().toLowerCase());
        final Set<String> s = new HashSet<String>();
        for (String value : la.getStringValues()) {
          s.add(value.toLowerCase());
        }
        la.clear();
        la.addStringValues(s);
      }
      sr = new SearchRequest(
        dn,
        new SearchFilter(filter, filterParameters.split("\\|")),
        returnAttrs.split("\\|"));
      sr.setLdapEntryHandlers(srh);
      result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(ucNamesChangeResult, result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "rangeSearchDn",
      "rangeSearchFilter",
      "rangeSearchReturnAttrs",
      "rangeHandlerResults"
    }
  )
  @Test(groups = {"search"})
  public void rangeHandlerSearch(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    if (!TestControl.isActiveDirectory()) {
      return;
    }

    final String expected = TestUtils.readFileIntoString(ldifFile);
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final SearchRequest sr = new SearchRequest(
        dn, new SearchFilter(filter), returnAttrs.split("\\|"));
      sr.setLdapEntryHandlers(new RangeEntryHandler());
      final LdapResult result = search.execute(sr).getResult();
      // ignore the case of member; some directories return it in mixed case
      AssertJUnit.assertEquals(
        0,
        (new LdapEntryIgnoreCaseComparator("member")).compare(
          TestUtils.convertLdifToResult(expected).getEntry(),
          result.getEntry()));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "specialCharSearchDn",
      "specialCharSearchFilter",
      "specialCharSearchFilterParameters",
      "specialCharBinarySearchFilter",
      "specialCharBinarySearchFilterParameters",
      "specialCharReturnAttrs",
      "specialCharSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void specialCharsSearch(
    final String dn,
    final String filter,
    final String filterParameters,
    final String binaryFilter,
    final String binaryFilterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));
    final String expected = TestUtils.readFileIntoString(ldifFile);
    final LdapResult specialCharsResult = TestUtils.convertLdifToResult(
      expected);

    LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterParameters.split("\\|")),
        returnAttrs.split("\\|"))).getResult();
    // DNs returned from JNDI may have escaped characters
    result.getEntry().setDn(result.getEntry().getDn().replaceAll("\\\\", ""));
    AssertJUnit.assertEquals(specialCharsResult, result);

    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(
          binaryFilter,
          new Object[] {
            LdapUtils.base64Decode(binaryFilterParameters)}),
        returnAttrs.split("\\|"))).getResult();
    // DNs returned from JNDI may have escaped characters
    result.getEntry().setDn(result.getEntry().getDn().replaceAll("\\\\", ""));
    AssertJUnit.assertEquals(specialCharsResult, result);
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "rewriteSearchDn",
      "rewriteSearchFilter",
      "rewriteSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void rewriteSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    final String expected = TestUtils.readFileIntoString(ldifFile);
    final LdapResult specialCharsResult = TestUtils.convertLdifToResult(
      expected);
    specialCharsResult.getEntry().setDn(
      specialCharsResult.getEntry().getDn().replaceAll("\\\\", ""));

    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      // test special character searching
      final SearchRequest request = new SearchRequest(
        dn, new SearchFilter(filter));
      final LdapResult result = search.execute(request).getResult();
      AssertJUnit.assertEquals(specialCharsResult, result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  resultsSize  of search results.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchExceededDn",
      "searchExceededFilter",
      "searchExceededResultsSize"
    }
  )
  @Test(groups = {"search"})
  public void searchExceeded(
    final String dn,
    final String filter,
    final int resultsSize)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();

      final SearchOperation search = new SearchOperation(conn);
      final SearchRequest request = new SearchRequest();
      request.setBaseDn(dn);
      request.setSizeLimit(resultsSize);

      request.setSearchFilter(new SearchFilter("(uugid=*)"));
      Response<LdapResult> response = search.execute(request);
      AssertJUnit.assertEquals(resultsSize, response.getResult().size());
      AssertJUnit.assertEquals(
        ResultCode.SIZE_LIMIT_EXCEEDED, response.getResultCode());

      request.setSearchFilter(new SearchFilter(filter));
      response = search.execute(request);
      AssertJUnit.assertEquals(resultsSize, response.getResult().size());
      AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchReferralDn",
      "searchReferralFilter"
    }
  )
  @Test(groups = {"search"})
  public void searchReferral(final String dn, final String filter)
    throws Exception
  {
    if (TestControl.isActiveDirectory()) {
      return;
    }

    Connection conn = createLdapConnection(true);

    // expects a referral on the dn ou=referrals
    final String referralDn = "ou=referrals," + DnParser.substring(dn, 1);
    final SearchRequest request = new SearchRequest();
    request.setBaseDn(referralDn);
    request.setSearchScope(SearchScope.ONELEVEL);
    request.setReturnAttributes(new String[0]);
    request.setSearchFilter(new SearchFilter(filter));

    request.setFollowReferrals(false);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      try {
        Response<LdapResult> response = search.execute(request);
        AssertJUnit.assertEquals(ResultCode.REFERRAL, response.getResultCode());
        AssertJUnit.assertTrue(response.getReferralURLs().length > 0);
        for (String s : response.getReferralURLs()) {
          AssertJUnit.assertTrue(
            response.getReferralURLs()[0].startsWith(
              conn.getConnectionConfig().getLdapUrl()));
        }
      } catch (LdapException e) {
        AssertJUnit.assertEquals(ResultCode.REFERRAL, e.getResultCode());
        AssertJUnit.assertTrue(e.getReferralURLs().length > 0);
        for (String s : e.getReferralURLs()) {
          AssertJUnit.assertTrue(
            e.getReferralURLs()[0].startsWith(
              conn.getConnectionConfig().getLdapUrl()));
        }
      }
    } finally {
      conn.close();
    }

    request.setFollowReferrals(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      try {
        Response<LdapResult> response = search.execute(request);
        AssertJUnit.assertTrue(response.getResult().size() > 0);
        AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
        AssertJUnit.assertNull(response.getReferralURLs());
      } catch (UnsupportedOperationException e) {
        // ignore this test if not supported
        AssertJUnit.assertNotNull(e);
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchReferenceDn",
      "searchReferenceFilter"
    }
  )
  @Test(groups = {"search"})
  public void searchReference(final String dn, final String filter)
    throws Exception
  {
    if (TestControl.isActiveDirectory()) {
      return;
    }

    Connection conn = createLdapConnection(true);

    // expects a referral on the root dn
    final String referralDn = DnParser.substring(dn, 1);
    final SearchRequest request = new SearchRequest();
    request.setBaseDn(referralDn);
    request.setSearchScope(SearchScope.ONELEVEL);
    request.setReturnAttributes(new String[0]);
    request.setSearchFilter(new SearchFilter(filter));

    request.setFollowReferrals(false);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      Response<LdapResult> response = search.execute(request);
      AssertJUnit.assertTrue(response.getResult().size() > 0);
      AssertJUnit.assertTrue(response.getReferralURLs().length > 0);
      // providers may return either result code
      if (response.getResultCode() != ResultCode.SUCCESS &&
          response.getResultCode() != ResultCode.REFERRAL) {
        AssertJUnit.fail("Invalid result code: " + response);
      }
    } finally {
      conn.close();
    }

    request.setFollowReferrals(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      try {
        Response<LdapResult> response = search.execute(request);
        AssertJUnit.assertTrue(response.getResult().size() > 0);
        AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
        AssertJUnit.assertNull(response.getReferralURLs());
      } catch (UnsupportedOperationException e) {
        // ignore this test if not supported
        AssertJUnit.assertNotNull(e);
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchActiveDirectoryDn",
      "searchActiveDirectoryFilter"
    }
  )
  @Test(groups = {"search"})
  public void searchActiveDirectory(
    final String dn, final String filter)
    throws Exception
  {
    if (!TestControl.isActiveDirectory()) {
      return;
    }

    Connection conn = createLdapConnection(true);

    // expects a referral on the root dn
    final String referralDn = DnParser.substring(dn, 1);
    final SearchRequest request = new SearchRequest();
    request.setBaseDn(referralDn);
    request.setSearchScope(SearchScope.ONELEVEL);
    request.setReturnAttributes(new String[0]);
    request.setSearchFilter(new SearchFilter(filter));

    request.setFollowReferrals(false);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      Response<LdapResult> response = search.execute(request);
      AssertJUnit.assertTrue(response.getResult().size() > 0);
      AssertJUnit.assertTrue(response.getReferralURLs().length > 0);
      // providers may return either result code
      if (response.getResultCode() != ResultCode.SUCCESS &&
          response.getResultCode() != ResultCode.REFERRAL) {
        AssertJUnit.fail("Invalid result code: " + response);
      }
    } finally {
      conn.close();
    }

    request.setFollowReferrals(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      try {
        Response<LdapResult> response = search.execute(request);
        AssertJUnit.assertTrue(response.getResult().size() > 0);
        AssertJUnit.assertNull(response.getReferralURLs());
        // AD referrals cannot be followed
        // providers may return either result code
        if (response.getResultCode() != ResultCode.SUCCESS &&
            response.getResultCode() != ResultCode.PARTIAL_RESULTS) {
          AssertJUnit.fail("Invalid result code: " + response);
        }
      } catch (LdapException e) {
        // some providers throw referral exception here
        AssertJUnit.assertEquals(
          ResultCode.REFERRAL, e.getResultCode());
      } catch (UnsupportedOperationException e) {
        // ignore this test if referrals not supported
        AssertJUnit.assertNotNull(e);
      }
    } finally {
      conn.close();
    }
  }


   /**
    * @param  dn  to search on.
    * @param  resultCode  to retry operations on.
    *
    * @throws  Exception  On test failure.
    */
  @Parameters(
    {
      "searchRetryDn",
      "searchRetryResultCode"
    }
  )
  @Test(groups = {"search-with-retry"}, dependsOnGroups = {"search"})
  public void searchWithRetry(final String dn, final String resultCode)
    throws Exception
  {
    final ResultCode retryResultCode = ResultCode.valueOf(resultCode);
    final ConnectionConfig cc = TestUtils.readConnectionConfig(null);
    DefaultConnectionFactory cf = new DefaultConnectionFactory(cc);
    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(
      new ResultCode[] {retryResultCode, });

    Connection conn = cf.getConnection();
    RetrySearchOperation search = new RetrySearchOperation(conn);

    try {
      conn.open();

      // test defaults
      try {
        final Response<LdapResult> response = search.execute(
          new SearchRequest(dn, new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail(
          "Should have thrown LdapException, returned: " + response);
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(1, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);

      // test no retry
      search.reset();
      search.setOperationRetry(0);

      try {
        final Response<LdapResult> response = search.execute(
          new SearchRequest(dn, new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail(
          "Should have thrown LdapException, returned: " + response);
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(0, search.getRetryCount());
      AssertJUnit.assertEquals(0, search.getRunTime());
    } finally {
      conn.close();
    }

    // test no exception
    cf = new DefaultConnectionFactory(cc);
    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(null);
    conn = cf.getConnection();
    search = new RetrySearchOperation(conn);
    search.setOperationRetry(1);

    try {
      conn.open();
      try {
        final Response<LdapResult> response = search.execute(
          new SearchRequest(dn, new SearchFilter("(objectclass=*)")));
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, response.getResultCode());
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(0, search.getRetryCount());
      AssertJUnit.assertEquals(0, search.getRunTime());
    } finally {
      conn.close();
    }

    // test retry count and wait time
    cf = new DefaultConnectionFactory(cc);
    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(
      new ResultCode[] {retryResultCode, });
    conn = cf.getConnection();
    search = new RetrySearchOperation(conn);
    search.setOperationRetry(3);
    search.setOperationRetryWait(1000);

    try {
      conn.open();
      try {
        final Response<LdapResult> response = search.execute(
          new SearchRequest(dn, new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail(
          "Should have thrown LdapException, returned: " + response);
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(3, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);

      // test backoff interval
      search.reset();
      search.setOperationRetryBackoff(2);
      try {
        final Response<LdapResult> response = search.execute(
          new SearchRequest(dn, new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail(
          "Should have thrown LdapException, returned: " + response);
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(3, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);

      // test infinite retries
      search.reset();
      search.setStopCount(10);
      search.setOperationRetry(-1);
      try {
        final Response<LdapResult> response = search.execute(
          new SearchRequest(dn, new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail(
          "Should have thrown LdapException, returned: " + response);
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(10, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  returnAttrs  to return from search.
   * @param  results  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "getAttributesDn",
      "getAttributesReturnAttrs",
      "getAttributesResults"
    }
  )
  @Test(
    groups = {"search"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void getAttributes(
    final String dn,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Connection conn = createLdapConnection(false);
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      SearchRequest.newObjectScopeSearchRequest(
        dn, returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(
      TestUtils.convertStringToEntry(dn, results), result.getEntry());
  }


  /**
   * @param  dn  to search on.
   * @param  returnAttrs  to return from search.
   * @param  results  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "getAttributesBase64Dn",
      "getAttributesBase64ReturnAttrs",
      "getAttributesBase64Results"
    }
  )
  @Test(groups = {"search"})
  public void getAttributesBase64(
    final String dn,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final SearchRequest request = SearchRequest.newObjectScopeSearchRequest(
        dn, returnAttrs.split("\\|"));
      request.setBinaryAttributes(new String[]{"jpegPhoto"});
      final LdapResult result = search.execute(request).getResult();
      AssertJUnit.assertEquals(
        TestUtils.convertStringToEntry(
          dn, results).getAttribute("jpegPhoto").getStringValue(),
        result.getEntry().getAttribute("jpegPhoto").getStringValue());
    } finally {
      conn.close();
    }
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"search"})
  public void getSaslMechanisms()
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          "", new String[] {"supportedSASLMechanisms"})).getResult();
      AssertJUnit.assertTrue(result.getEntry().getAttributes().size() > 0);
    } finally {
      conn.close();
    }
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"search"})
  public void getSupportedControls()
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          "", new String[] {"supportedcontrol"})).getResult();
      AssertJUnit.assertTrue(result.getEntry().getAttributes().size() > 0);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "digestMd5SearchDn",
      "digestMd5SearchFilter",
      "digestMd5SearchFilterParameters",
      "digestMd5SearchReturnAttrs",
      "digestMd5SearchResults"
    }
  )
  @Test(groups = {"search"})
  public void digestMd5Search(
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtils.readFileIntoString(ldifFile);
    final Connection conn = TestUtils.createDigestMd5Connection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterParameters.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "cramMd5SearchDn",
      "cramMd5SearchFilter",
      "cramMd5SearchFilterParameters",
      "cramMd5SearchReturnAttrs",
      "cramMd5SearchResults"
    }
  )
  @Test(groups = {"search"})
  public void cramMd5Search(
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtils.readFileIntoString(ldifFile);
    final Connection conn = TestUtils.createCramMd5Connection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterParameters.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "saslExternalSearchDn",
      "saslExternalSearchFilter",
      "saslExternalSearchFilterParameters",
      "saslExternalSearchReturnAttrs",
      "saslExternalSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void saslExternalSearch(
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtils.readFileIntoString(ldifFile);
    final Connection conn = TestUtils.createSaslExternalConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterParameters.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  krb5Realm  kerberos realm
   * @param  krb5Kdc  kerberos kdc
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "krb5Realm",
      "ldapTestHost",
      "gssApiSearchDn",
      "gssApiSearchFilter",
      "gssApiSearchFilterParameters",
      "gssApiSearchReturnAttrs",
      "gssApiSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void gssApiSearch(
    final String krb5Realm,
    final String krb5Kdc,
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final LdapURL ldapUrl = new LdapURL(krb5Kdc);
    System.setProperty(
      "java.security.auth.login.config",
      "target/test-classes/ldap_jaas.config");
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
    System.setProperty("java.security.krb5.realm", krb5Realm);
    System.setProperty(
      "java.security.krb5.kdc", ldapUrl.getEntry().getHostname());

    final String expected = TestUtils.readFileIntoString(ldifFile);

    final Connection conn = TestUtils.createGssApiConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterParameters.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);
    } finally {
      System.clearProperty("java.security.auth.login.config");
      System.clearProperty("javax.security.auth.useSubjectCredsOnly");
      System.clearProperty("java.security.krb5.realm");
      System.clearProperty("java.security.krb5.kdc");
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterParameters  to replace parameters in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchDn",
      "searchFilter",
      "searchFilterParameters",
      "searchReturnAttrs",
      "searchResults"
    }
  )
  @Test(groups = {"search"})
  public void executorSearch(
    final String dn,
    final String filter,
    final String filterParameters,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final SearchExecutor executor = new SearchExecutor();
    executor.setBaseDn(dn);
    executor.setSearchFilter(
      new SearchFilter(filter, filterParameters.split("\\|")));
    executor.setReturnAttributes(returnAttrs.split("\\|"));

    final String expected = TestUtils.readFileIntoString(ldifFile);

    final ConnectionFactory cf = new DefaultConnectionFactory(
      TestUtils.readConnectionConfig(null));
    LdapResult result = executor.search(cf).getResult();
    AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);

    BlockingConnectionPool pool = new BlockingConnectionPool(
      new DefaultConnectionFactory(TestUtils.readConnectionConfig(null)));
    pool.setConnectOnCreate(false);
    pool.initialize();
    PooledConnectionFactory pcf = new PooledConnectionFactory(pool);
    result = executor.search(pcf).getResult();
    pool.close();
    AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);

    pool = new BlockingConnectionPool(
      new DefaultConnectionFactory(TestUtils.readConnectionConfig(null)));
    pool.setConnectOnCreate(true);
    pool.initialize();
    pcf = new PooledConnectionFactory(pool);
    result = executor.search(pcf).getResult();
    pool.close();
    AssertJUnit.assertEquals(TestUtils.convertLdifToResult(expected), result);
  }
}
