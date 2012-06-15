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
import org.ldaptive.Response;
import org.ldaptive.TestUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link WhoAmIOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class WhoAmIOperationTest extends AbstractTest
{


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"extended"})
  public void whoami()
    throws Exception
  {
    final Connection conn = TestUtils.createConnection();
    try {
      conn.open();
      final WhoAmIOperation whoami = new WhoAmIOperation(conn);
      final Response<WhoAmIResponse> res = whoami.execute(new WhoAmIRequest());
      AssertJUnit.assertEquals(
        "dn:" + conn.getConnectionConfig().getBindDn(),
        res.getResult().getAuthzId());
    } finally {
      conn.close();
    }
  }
}
