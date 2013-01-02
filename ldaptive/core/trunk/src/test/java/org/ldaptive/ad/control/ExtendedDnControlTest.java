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
package org.ldaptive.ad.control;

import org.ldaptive.LdapUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ExtendedDnControl}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ExtendedDnControlTest
{


  /**
   * Extended DN control test data.
   *
   * @return  response test data
   */
  @DataProvider(name = "request")
  public Object[][] createData()
  {
    return new Object[][] {
      // standard format
      // BER: 30:03:02:01:01:
      new Object[] {
        LdapUtils.base64Decode("MAMCAQE="),
        new ExtendedDnControl(),
      },
      // hexadecimal format
      // BER: 30:03:02:01:00
      new Object[] {
        LdapUtils.base64Decode("MAMCAQA="),
        new ExtendedDnControl(ExtendedDnControl.Flag.HEXADECIMAL),
      },
    };
  }


  /**
   * @param  berValue  to encode.
   * @param  expected  extended dn control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"control"}, dataProvider = "request")
  public void encode(final byte[] berValue, final ExtendedDnControl expected)
    throws Exception
  {
    Assert.assertEquals(expected.encode(), berValue);
  }
}
