/*
  $Id: RelativeDistinguishedNameTest.java 578 2009-09-08 19:10:23Z marvin.addison $

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
 * Unit test for {@link RelativeDistinguishedName} class.
 *
 * @author Middleware
 * @version $Revision: 578 $
 *
 */
public class RelativeDistinguishedNameTest
{
  /**
   * @return  RDN test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "rdndata")
  public Object[][] createRDNTestData()
    throws Exception
  {
    return new Object[][] {
      {
        new RelativeDistinguishedName(
            new AttributeTypeAndValue(AttributeType.DomainComponent, "vt")),
        "DC=vt",
      },
      {
        new RelativeDistinguishedName(
          new AttributeTypeAndValue[] {
            new AttributeTypeAndValue(
                AttributeType.OrganizationalUnitName, "Sales"),
            new AttributeTypeAndValue(
                AttributeType.CommonName, "J. Smith"),
          }),
        "OU=Sales+CN=J. Smith",
      },
    };
  }


  /**
   * @param  rdn  Test value to perform toString() on.
   * @param  expected  Expected string value.
   */
  @Test(groups = {"functest", "x509"}, dataProvider = "rdndata")
  public void testToString(
    final RelativeDistinguishedName rdn,
    final String expected)
  {
    AssertJUnit.assertEquals(rdn.toString(), expected);
  }

}
