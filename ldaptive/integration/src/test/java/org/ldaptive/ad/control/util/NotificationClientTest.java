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
package org.ldaptive.ad.control.util;

import java.util.concurrent.BlockingQueue;
import org.ldaptive.AbstractTest;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.Connection;
import org.ldaptive.LdapAttribute;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.TestControl;
import org.ldaptive.TestUtils;
import org.ldaptive.async.AsyncRequest;
import org.testng.AssertJUnit;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link NotificationClient}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NotificationClientTest extends AbstractTest
{


  /**
   * @param  dn  to search on.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("ncSearchDn")
  @Test(groups = {"control-util"})
  public void execute(final String dn)
    throws Exception
  {
    if (!TestControl.isActiveDirectory()) {
      return;
    }

    Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final NotificationClient client = new NotificationClient(conn);

      final SearchRequest request = new SearchRequest(
        "ou=test,dc=middleware,dc=vt,dc=edu",
        new SearchFilter("(objectClass=*)"));
      request.setSearchScope(SearchScope.ONELEVEL);
      final BlockingQueue<NotificationClient.NotificationItem> results =
        client.execute(request);

      final ModifyOperation modify = new ModifyOperation(conn);
      modify.execute(
        new ModifyRequest(
          dn,
          new AttributeModification(
            AttributeModificationType.REPLACE,
            new LdapAttribute("sn", "Jones"))));

      NotificationClient.NotificationItem item = results.take();
      if (item.isException()) {
        throw item.getException();
      }
      AssertJUnit.assertTrue(item.isAsyncRequest());
      AssertJUnit.assertTrue(item.getAsyncRequest().getMessageId() > 0);
      final AsyncRequest asyncRequest = item.getAsyncRequest();

      item = results.take();
      AssertJUnit.assertTrue(item.isSearchEntry());
      AssertJUnit.assertNotNull(item.getEntry());

      asyncRequest.abandon();

    } finally {
      final ModifyOperation modify = new ModifyOperation(conn);
      modify.execute(
        new ModifyRequest(
          dn,
          new AttributeModification(
            AttributeModificationType.REPLACE,
            new LdapAttribute("sn", "Admin"))));

      conn.close();
    }
  }
}
