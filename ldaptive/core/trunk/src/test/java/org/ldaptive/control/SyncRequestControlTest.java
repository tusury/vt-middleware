/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.control;

import org.ldaptive.LdapUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SyncRequestControl}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SyncRequestControlTest
{


  /**
   * Sync request control test data.
   *
   * @return  response test data
   */
  @DataProvider(name = "request")
  public Object[][] createData()
  {
    return new Object[][] {
      // refresh only, reloadHint false
      // BER:30:06:02:01:01:01:01:00
      new Object[] {
        LdapUtils.base64Decode("MAYCAQEBAQA="),
        new SyncRequestControl(SyncRequestControl.Mode.REFRESH_ONLY, true),
      },
    };
  }


  /**
   * @param  berValue  to encode.
   * @param  expected  sync request control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"control"}, dataProvider = "request")
  public void decode(final byte[] berValue, final SyncRequestControl expected)
    throws Exception
  {
    Assert.assertEquals(expected.encode(), berValue);
  }
}
