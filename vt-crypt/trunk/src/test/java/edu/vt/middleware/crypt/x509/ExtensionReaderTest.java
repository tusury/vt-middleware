/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.x509.types.AuthorityKeyIdentifier;
import edu.vt.middleware.crypt.x509.types.BasicConstraints;
import edu.vt.middleware.crypt.x509.types.GeneralName;
import edu.vt.middleware.crypt.x509.types.GeneralNameList;
import edu.vt.middleware.crypt.x509.types.GeneralNameType;
import edu.vt.middleware.crypt.x509.types.KeyIdentifier;
import edu.vt.middleware.crypt.x509.types.PolicyInformation;
import edu.vt.middleware.crypt.x509.types.PolicyInformationList;
import edu.vt.middleware.crypt.x509.types.PolicyQualifierInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ExtensionReader} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ExtensionReaderTest
{
  /** Path to directory containing test resources. */
  private static final String RESOURCE_DIR =
    "src/test/resources/edu/vt/middleware/crypt/x509";

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(this.getClass());


  /**
   * @return  Certificate test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "certdata")
  public Object[][] createCertificateTestData()
    throws Exception
  {
    final File testCert1 = new File(RESOURCE_DIR, "serac-dev-test-cert.pem");
    final Map<ExtensionType, Object> extMap1 =
      new HashMap<ExtensionType, Object>();
    extMap1.put(
      ExtensionType.SubjectAlternativeName,
      new GeneralNameList(new GeneralName[] {
        new GeneralName("eprov@vt.edu", GeneralNameType.RFC822Name),
      }));
    extMap1.put(ExtensionType.BasicConstraints, new BasicConstraints(false));
    final PolicyInformation[] policies1 = new PolicyInformation[] {
      new PolicyInformation("1.3.6.1.4.1.6760.5.2.2.2.1"),
      new PolicyInformation("1.3.6.1.4.1.6760.5.2.2.1.1"),
      new PolicyInformation(
        "1.3.6.1.4.1.6760.5.2.2.4.1",
        new PolicyQualifierInfo[] {
          new PolicyQualifierInfo(
            "http://www.pki.vt.edu/vtuca/cps/index.html"),
        }),
      new PolicyInformation("1.3.6.1.4.1.6760.5.2.2.3.1"),
    };
    extMap1.put(
      ExtensionType.CertificatePolicies,
      new PolicyInformationList(policies1));
    extMap1.put(
      ExtensionType.SubjectKeyIdentifier,
      new KeyIdentifier(
        "25:48:2F:28:EC:5D:19:BB:1D:25:AE:94:93:B1:7B:B5:35:96:24:66"));
    extMap1.put(
      ExtensionType.AuthorityKeyIdentifier,
      new AuthorityKeyIdentifier(new KeyIdentifier(
        "38:E0:6F:AE:48:ED:5E:23:F6:22:9B:1E:E7:9C:19:16:47:B8:7E:92")));

    final File testCert2 = new File(RESOURCE_DIR,
        "thawte-premium-server-ca-cert.pem");
    final Map<ExtensionType, Object> extMap2 =
      new HashMap<ExtensionType, Object>();
    extMap2.put(ExtensionType.BasicConstraints, new BasicConstraints(true));

    return new Object[][] {
      {testCert1, extMap1},
      {testCert2, extMap2},
    };
  }

  /**
   * @param  certFile  File containing X.509 certificate data.
   * @param  expectedExtensionMap  Expected map of extension types to extension
   * data that should be produced from reading the extended attributes of
   * the given certificate.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "x509"}, dataProvider = "certdata")
  public void testReadAll(
    final File certFile,
    final Map<ExtensionType, Object> expectedExtensionMap)
    throws Exception
  {
    logger.info("Testing read all extended attributes from " + certFile);
    final ExtensionReader reader = new ExtensionReader(
        (X509Certificate) CryptReader.readCertificate(certFile));
    final Map<ExtensionType, Object> actualExtensionMap = reader.readAll();

    AssertJUnit.assertEquals(
      expectedExtensionMap.size(),
      actualExtensionMap.size());
    for (ExtensionType type : expectedExtensionMap.keySet()) {
      AssertJUnit.assertEquals(
        expectedExtensionMap.get(type),
        actualExtensionMap.get(type));
    }
  }
}
