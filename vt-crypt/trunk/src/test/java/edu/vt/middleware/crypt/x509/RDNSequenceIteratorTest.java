/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.x509.types.AttributeType;
import edu.vt.middleware.crypt.x509.types.AttributeTypeAndValue;
import edu.vt.middleware.crypt.x509.types.RelativeDistinguishedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link RDNSequenceIterator} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class RDNSequenceIteratorTest
{

  /** Path to directory containing test resources. */
  private static final String RESOURCE_DIR =
    "src/test/resources/edu/vt/middleware/crypt/x509";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  /**
   * @return  RDN test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "rdndata")
  public Object[][] createRDNTestData()
    throws Exception
  {
    // VT User CA client certificate
    final File testCert1 = new File(RESOURCE_DIR, "serac-dev-test-cert.pem");
    final RelativeDistinguishedName[] expected1 =
      new RelativeDistinguishedName[] {
        new RelativeDistinguishedName(
          new AttributeTypeAndValue(AttributeType.CountryName, "US")),
        new RelativeDistinguishedName(
          new AttributeTypeAndValue(AttributeType.DomainComponent, "vt")),
        new RelativeDistinguishedName(
          new AttributeTypeAndValue(AttributeType.DomainComponent, "edu")),
        new RelativeDistinguishedName(
          new AttributeTypeAndValue(
            AttributeType.OrganizationName,
            "Virginia Polytechnic Institute and State University")),
        new RelativeDistinguishedName(
          new AttributeTypeAndValue(
            AttributeType.CommonName,
            "Marvin S Addison")),
        new RelativeDistinguishedName(
          new AttributeTypeAndValue(AttributeType.UserId, "1145718")),
      };

    return new Object[][] {
      {testCert1, expected1},
    };
  }


  /**
   * @param  certFile  File containing X.509 certificate data.
   * @param  expectedSubjectRDNs  Expected array of relative distinguished names
   * that should be collected upon iterating over the subject DN of the cert.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "x509"},
    dataProvider = "rdndata"
  )
  public void testIterator(
    final File certFile,
    final RelativeDistinguishedName[] expectedSubjectRDNs)
    throws Exception
  {
    logger.info("Iterating over subject DN of {}", certFile);

    final RDNSequenceIterator iterator = new RDNSequenceIterator(
      ((X509Certificate) CryptReader.readCertificate(certFile))
          .getSubjectX500Principal().getEncoded());
    final List<RelativeDistinguishedName> actual =
      new ArrayList<RelativeDistinguishedName>();
    for (RelativeDistinguishedName rdn : iterator) {
      actual.add(rdn);
    }
    AssertJUnit.assertEquals(Arrays.asList(expectedSubjectRDNs), actual);
  }
}
