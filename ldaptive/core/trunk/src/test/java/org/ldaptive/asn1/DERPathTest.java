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
package org.ldaptive.asn1;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link DERPath} class.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DERPathTest
{


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"asn1"})
  public void testPushChild()
    throws Exception
  {
    final DERPath path = new DERPath("/SEQ/INT");
    path.pushChild("CHOICE", 1);
    Assert.assertEquals(path.toString(), "/SEQ/INT/CHOICE[1]");
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"asn1"})
  public void testPopChild()
    throws Exception
  {
    final DERPath path = new DERPath("/SEQ/SET[1]/CHOICE");
    path.popChild();
    Assert.assertEquals(path.toString(), "/SEQ/SET[1]");

  }


  /**
   * DER path test data.
   *
   * @return  der paths
   */
  @DataProvider(name = "paths")
  public Object[][] createTestParams()
  {
    return new Object[][] {
      new Object[] {
        "/SET[0]/SEQ[0]/INT[0]",
        "/SET/SEQ/INT",
      },
      new Object[] {
        "/SET[1]/CTX(0)[1]/INT",
        "/SET[1]/CTX(0)[1]/INT",
      },
      new Object[] {
        "/SET[0]/APP(0)[0]/APP(1)",
        "/SET/APP(0)/APP(1)",
      },
    };
  }


  /**
   * @param  testPath  to test
   * @param  expected  to test
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"asn1"}, dataProvider = "paths")
  public void testToString(
    final String testPath, final String expected)
    throws Exception
  {
    Assert.assertEquals(new DERPath(testPath).toString(), expected);
  }


  /**
   * @param  testPath  to test
   * @param  expected  to test
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"asn1"}, dataProvider = "paths")
  public void testEquals(
    final String testPath, final String expected)
    throws Exception
  {
    Assert.assertTrue(new DERPath(testPath).equals(new DERPath(expected)));
  }
}
