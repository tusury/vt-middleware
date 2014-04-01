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
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link PasswordExpiredControl}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PasswordExpiredControlTest
{


  /**
   * Password expired control test data.
   *
   * @return  response test data
   */
  @DataProvider(name = "response")
  public Object[][] createData()
  {
    return new Object[][] {
      // BER: 30
      new Object[] {
        LdapUtils.base64Decode("MA=="),
        new PasswordExpiredControl(),
      },
    };
  }


  /**
   * @param  berValue  to decode.
   * @param  expected  password expired control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"control"}, dataProvider = "response")
  public void decode(
    final byte[] berValue,
    final PasswordExpiredControl expected)
    throws Exception
  {
    final PasswordExpiredControl actual = new PasswordExpiredControl(
      expected.getCriticality());
    actual.decode(berValue);
    Assert.assertEquals(actual, expected);
  }
}
