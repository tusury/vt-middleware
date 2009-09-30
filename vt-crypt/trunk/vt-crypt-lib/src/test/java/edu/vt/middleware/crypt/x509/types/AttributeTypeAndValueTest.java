/*
  $Id: AttributeTypeAndValueTest.java 578 2009-09-08 19:10:23Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 578 $
  Updated: $Date: 2009-09-08 15:10:23 -0400 (Tue, 08 Sep 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link AttributeTypeAndValue} class.
 *
 * @author Middleware
 * @version $Revision: 578 $
 *
 */
public class AttributeTypeAndValueTest
{
  /**
   * @return  Certificate test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "atvdata")
  public Object[][] createAttributeTypeAndValueTestData()
    throws Exception
  {
    return new Object[][] {
      {
        new AttributeTypeAndValue(
            AttributeType.CommonName,
            "Marvin S. Addison"),
        "CN=Marvin S. Addison",
      },
      {
        new AttributeTypeAndValue(
            AttributeType.OrganizationName,
            "Sue, Grabbit and Runn"),
        "O=Sue\\, Grabbit and Runn",
      },
      {
        new AttributeTypeAndValue(
            "3.1.4.1.5",
            " Apple, Cherry, and Mixed Berry! "),
        "3.1.4.1.5=\\ Apple\\, Cherry\\, and Mixed Berry!\\ ",
      },
    };
  }


  /**
   * @param  atv  Test value to perform toString() on.
   * @param  expected  Expected string value.
   */
  @Test(groups = {"functest", "x509"}, dataProvider = "atvdata")
  public void testToString(
    final AttributeTypeAndValue atv,
    final String expected)
  {
    AssertJUnit.assertEquals(atv.toString(), expected);
  }

}
