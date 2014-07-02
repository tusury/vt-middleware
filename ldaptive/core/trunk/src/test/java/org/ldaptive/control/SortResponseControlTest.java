/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.control;

import org.ldaptive.LdapUtils;
import org.ldaptive.ResultCode;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SortResponseControl}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortResponseControlTest
{


  /**
   * Sort response control test data.
   *
   * @return  response test data
   */
  @DataProvider(name = "response")
  public Object[][] createData()
  {
    return
      new Object[][] {
        // result code success
        // BER: 30:03:0A:01:00
        new Object[] {
          LdapUtils.base64Decode("MAMKAQA="),
          new SortResponseControl(ResultCode.SUCCESS, true),
        },
      };
  }


  /**
   * @param  berValue  to encode.
   * @param  expected  sort response control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"control"},
    dataProvider = "response"
  )
  public void decode(final byte[] berValue, final SortResponseControl expected)
    throws Exception
  {
    final SortResponseControl actual = new SortResponseControl(
      expected.getCriticality());
    actual.decode(berValue);
    Assert.assertEquals(actual, expected);
  }
}
