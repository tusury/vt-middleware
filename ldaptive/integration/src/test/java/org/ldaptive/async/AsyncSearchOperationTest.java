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
package org.ldaptive.async;

import java.util.concurrent.BlockingQueue;
import org.ldaptive.AbstractTest;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.Connection;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.TestUtils;
import org.ldaptive.control.SyncStateControl;
import org.ldaptive.intermediate.SyncInfoMessage;
import org.ldaptive.provider.Provider;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link AsyncSearchOperation} and {@link SyncReplClient}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AsyncSearchOperationTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry17")
  @BeforeClass(groups = {"async"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtils.readFileIntoString(ldifFile);
    testLdapEntry = TestUtils.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"async"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
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
      "searchAsyncDn",
      "searchAsyncFilter",
      "searchAsyncReturnAttrs",
      "searchAsyncResults"
    }
  )
  @Test(groups = {"async"})
  public void search(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    Connection conn = TestUtils.createConnection();

    final String expected = TestUtils.readFileIntoString(ldifFile);

    try {
      conn.open();
      final AsyncSearchOperation search = new AsyncSearchOperation(conn);
      final SearchRequest request = new SearchRequest(
        dn, new SearchFilter(filter), returnAttrs.split("\\|"));
      FutureResponse<SearchResult> response = search.execute(request);
      AssertJUnit.assertTrue(response.getResult().size() > 0);
      AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
      TestUtils.assertEquals(
        TestUtils.convertLdifToResult(expected), response.getResult());
    } catch (IllegalStateException e) {
      throw (Exception) e.getCause();
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
    "syncReplSearchDn",
    "syncReplSearchReturnAttrs",
    "syncReplSearchResults"
  })
  @Test(groups = {"async"})
  public void syncReplRefreshOnly(
    final String dn,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Provider<?> p = DefaultConnectionFactory.getDefaultProvider();
    if (p.getClass().getName().equals(
      "org.ldaptive.provider.jndi.JndiProvider")) {
      throw new UnsupportedOperationException(
        "Search entry controls not supported");
    }
    final String expected = TestUtils.readFileIntoString(ldifFile);

    Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final SyncReplClient client = new SyncReplClient(conn, false, null);
      final SearchRequest request = SearchRequest.newObjectScopeSearchRequest(
        dn, returnAttrs.split("\\|"));
      final BlockingQueue<SyncReplItem> results = client.execute(request);

      SyncReplItem item = results.take();
      AssertJUnit.assertTrue(item.isEntry());
      AssertJUnit.assertEquals(
        SyncStateControl.State.ADD,
        item.getEntry().getSyncStateControl().getSyncState());
      AssertJUnit.assertNotNull(
        item.getEntry().getSyncStateControl().getEntryUuid());
      AssertJUnit.assertNull(item.getEntry().getSyncStateControl().getCookie());

      item = results.take();
      AssertJUnit.assertTrue(item.isResponse());
      AssertJUnit.assertEquals(
        true, item.getResponse().getSyncDoneControl().getRefreshDeletes());
      AssertJUnit.assertNotNull(
        item.getResponse().getSyncDoneControl().getCookie());

    } finally {
      conn.close();
    }

  }


  /**
   * @param  dn  to search on.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
    "syncReplSearchDn",
    "syncReplSearchReturnAttrs",
    "syncReplSearchResults"
  })
  @Test(groups = {"async"})
  public void syncReplRefreshAndPersist(
    final String dn,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Provider<?> p = DefaultConnectionFactory.getDefaultProvider();
    if (p.getClass().getName().equals(
      "org.ldaptive.provider.jndi.JndiProvider")) {
      throw new UnsupportedOperationException(
        "Intermediate responses not supported");
    } else if (p.getClass().getName().equals(
      "org.ldaptive.provider.netscape.NetscapeProvider")) {
      throw new UnsupportedOperationException(
        "Intermediate responses not supported");
    } else if (p.getClass().getName().equals(
      "org.ldaptive.provider.opends.OpenDSProvider")) {
      throw new UnsupportedOperationException(
        "Intermediate responses not supported");
    } else if (p.getClass().getName().equals(
      "org.ldaptive.provider.opendj.OpenDJProvider")) {
      throw new UnsupportedOperationException("Message IDs not supported");
    }
    final String expected = TestUtils.readFileIntoString(ldifFile);

    Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final SyncReplClient client = new SyncReplClient(conn, true, null);
      final SearchRequest request = SearchRequest.newObjectScopeSearchRequest(
        dn, returnAttrs.split("\\|"));
      final BlockingQueue<SyncReplItem> results = client.execute(request);

      // test the first entry
      SyncReplItem item = results.take();
      AssertJUnit.assertTrue(item.isEntry());
      AssertJUnit.assertEquals(
        SyncStateControl.State.ADD,
        item.getEntry().getSyncStateControl().getSyncState());
      AssertJUnit.assertNotNull(
        item.getEntry().getSyncStateControl().getEntryUuid());
      AssertJUnit.assertNull(item.getEntry().getSyncStateControl().getCookie());
      TestUtils.assertEquals(
        TestUtils.convertLdifToResult(expected),
        new SearchResult(item.getEntry().getSearchEntry()));

      // test the info message
      item = results.take();
      AssertJUnit.assertTrue(item.isMessage());
      AssertJUnit.assertEquals(
        SyncInfoMessage.Type.REFRESH_DELETE, item.getMessage().getMessageType());
      AssertJUnit.assertNotNull(item.getMessage().getCookie());
      AssertJUnit.assertFalse(item.getMessage().getRefreshDeletes());
      AssertJUnit.assertTrue(item.getMessage().getRefreshDone());

      // make a change
      final ModifyOperation modify = new ModifyOperation(conn);
      modify.execute(new ModifyRequest(
        dn,
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute("employeeType", "Employee"))));
      item = results.take();
      AssertJUnit.assertTrue(item.isEntry());
      AssertJUnit.assertEquals(
        SyncStateControl.State.MODIFY,
        item.getEntry().getSyncStateControl().getSyncState());
      AssertJUnit.assertNotNull(
        item.getEntry().getSyncStateControl().getEntryUuid());
      AssertJUnit.assertNotNull(
        item.getEntry().getSyncStateControl().getCookie());

      // change it back
      modify.execute(new ModifyRequest(
        dn,
        new AttributeModification(
          AttributeModificationType.REMOVE,
          new LdapAttribute("employeeType"))));
      item = results.take();
      AssertJUnit.assertTrue(item.isEntry());
      AssertJUnit.assertEquals(
        SyncStateControl.State.MODIFY,
        item.getEntry().getSyncStateControl().getSyncState());
      AssertJUnit.assertNotNull(
        item.getEntry().getSyncStateControl().getEntryUuid());
      AssertJUnit.assertNotNull(
        item.getEntry().getSyncStateControl().getCookie());
      TestUtils.assertEquals(
        TestUtils.convertLdifToResult(expected),
        new SearchResult(item.getEntry().getSearchEntry()));

      client.cancel(item.getEntry().getSearchEntry().getMessageId());

      item = results.take();
      AssertJUnit.assertTrue(item.isResponse());
      AssertJUnit.assertTrue(results.isEmpty());
      AssertJUnit.assertEquals(
        ResultCode.CANCELED, item.getResponse().getResponse().getResultCode());
    } finally {
      conn.close();
    }
  }
}
