/*
  $Id$

  Copyright (C) 2007-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.x509.types.AttributeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link DNUtils} class.
 *
 * @author  Middleware Services
 * @version  $Revision: 587 $
 */
public class DNUtilsTest
{

  /** Path to directory containing test resources. */
  private static final String RESOURCE_DIR =
    "src/test/resources/edu/vt/middleware/crypt/x509";

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(this.getClass());


  /**
   * @return  Test data requiring multiple attribute values per certificate.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "multivaluesdata")
  public Object[][] createMultipleValuesTestData()
    throws Exception
  {
    return
      new Object[][] {
        {
          new File(RESOURCE_DIR, "serac-dev-test-cert.pem"),
          AttributeType.DomainComponent.getOid(),
          new String[] {"vt", "edu"},
        },
        {
          new File(RESOURCE_DIR, "glider.cc.vt.edu.crt"),
          AttributeType.SerialNumber.getOid(),
          new String[] {"1248110657961"},
        },
        {
          new File(RESOURCE_DIR, "glider.cc.vt.edu.crt"),
          AttributeType.OrganizationalUnitName.getOid(),
          new String[] {"Middleware-Client", "SETI"},
        },
      };
  }


  /**
   * @return  Test data requiring single attribute value per certificate.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "singlevaluedata")
  public Object[][] createSingleValuesTestData()
    throws Exception
  {
    return
      new Object[][] {
        {
          new File(RESOURCE_DIR, "serac-dev-test-cert.pem"),
          AttributeType.DomainComponent.getOid(),
          "vt",
        },
        {
          new File(RESOURCE_DIR, "glider.cc.vt.edu.crt"),
          AttributeType.CommonName.getOid(),
          "glider.cc.vt.edu",
        },
      };
  }


  /**
   * @param  certFile  File containing X.509 certificate data.
   * @param  attributeOid  OID of attribute values to fetch.
   * @param  expectedValues  Expected attribute values.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "x509"},
    dataProvider = "multivaluesdata"
  )
  public void testGetAttributeValues(
    final File certFile,
    final String attributeOid,
    final String[] expectedValues)
    throws Exception
  {
    logger.info(
      "Testing getting attribute values of subject DN of " + certFile);

    final X509Certificate cert = (X509Certificate) CryptReader.readCertificate(
      certFile);
    final String[] actualValues = DNUtils.getAttributeValues(
      cert.getSubjectX500Principal(),
      attributeOid);
    AssertJUnit.assertEquals(
      Arrays.asList(expectedValues),
      Arrays.asList(actualValues));
  }


  /**
   * @param  certFile  File containing X.509 certificate data.
   * @param  attributeOid  OID of attribute values to fetch.
   * @param  expectedValue  Expected attribute value.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "x509"},
    dataProvider = "singlevaluedata"
  )
  public void testGetAttributeValue(
    final File certFile,
    final String attributeOid,
    final String expectedValue)
    throws Exception
  {
    logger.info("Testing getting attribute value of subject DN of " + certFile);

    final X509Certificate cert = (X509Certificate) CryptReader.readCertificate(
      certFile);
    final String actualValue = DNUtils.getAttributeValue(
      cert.getSubjectX500Principal(),
      attributeOid);
    AssertJUnit.assertEquals(expectedValue, actualValue);
  }
}
