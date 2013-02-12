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
package org.ldaptive.extended;

import org.ldaptive.AbstractTest;
import org.ldaptive.Connection;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchEntry;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.TestControl;
import org.ldaptive.TestUtils;
import org.ldaptive.async.AsyncSearchOperation;
import org.ldaptive.control.SyncRequestControl;
import org.ldaptive.handler.HandlerResult;
import org.ldaptive.handler.SearchEntryHandler;
import org.ldaptive.provider.Provider;
import org.testng.AssertJUnit;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link CancelOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CancelOperationTest extends AbstractTest
{


  /**
   * @param  dn  to search on.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "cancelDn" })
  @Test(groups = {"extended"})
  public void cancel(final String dn)
    throws Exception
  {
    if (TestControl.isActiveDirectory()) {
      return;
    }
    final Provider<?> p = DefaultConnectionFactory.getDefaultProvider();
    if (p.getClass().getName().equals(
      "org.ldaptive.provider.jndi.JndiProvider")) {
      throw new UnsupportedOperationException("Message IDs not supported");
    } else if (p.getClass().getName().equals(
      "org.ldaptive.provider.netscape.NetscapeProvider")) {
      throw new UnsupportedOperationException(
        "Intermediate responses not supported");
    } else if (p.getClass().getName().equals(
      "org.ldaptive.provider.opends.OpenDSProvider")) {
      throw new UnsupportedOperationException("Message IDs not supported");
    } else if (p.getClass().getName().equals(
      "org.ldaptive.provider.opendj.OpenDJProvider")) {
      throw new UnsupportedOperationException("Message IDs not supported");
    }
    final Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final AsyncSearchOperation search = new AsyncSearchOperation(conn);
      final SearchRequest request = SearchRequest.newObjectScopeSearchRequest(
        dn);
      request.setSearchEntryHandlers(
        new SearchEntryHandler() {
          @Override
          public HandlerResult<SearchEntry> handle(
            final Connection conn,
            final SearchRequest request,
            final SearchEntry entry) throws LdapException
          {
            final CancelOperation cancel = new CancelOperation(conn);
            final Response<Void> response = cancel.execute(
              new CancelRequest(entry.getMessageId()));
            AssertJUnit.assertEquals(
              ResultCode.SUCCESS, response.getResultCode());
            return new HandlerResult<SearchEntry>(null);
          }

          @Override
          public void initializeRequest(final SearchRequest request) {}
        }
      );
      request.setControls(
        new SyncRequestControl(
          SyncRequestControl.Mode.REFRESH_AND_PERSIST, true));
      final Response<SearchResult> response = search.execute(request);
      AssertJUnit.assertEquals(ResultCode.CANCELED, response.getResultCode());
    } finally {
      conn.close();
    }
  }
}
