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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.x509.types.AuthorityKeyIdentifier;
import edu.vt.middleware.crypt.x509.types.BasicConstraints;
import edu.vt.middleware.crypt.x509.types.DistributionPoint;
import edu.vt.middleware.crypt.x509.types.DistributionPointList;
import edu.vt.middleware.crypt.x509.types.GeneralName;
import edu.vt.middleware.crypt.x509.types.GeneralNameList;
import edu.vt.middleware.crypt.x509.types.GeneralNameType;
import edu.vt.middleware.crypt.x509.types.KeyIdentifier;
import edu.vt.middleware.crypt.x509.types.KeyPurposeId;
import edu.vt.middleware.crypt.x509.types.KeyPurposeIdList;
import edu.vt.middleware.crypt.x509.types.KeyUsage;
import edu.vt.middleware.crypt.x509.types.KeyUsageBits;
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
    // VT User CA client certificate
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
    extMap1.put(
      ExtensionType.KeyUsage,
      new KeyUsage(
        new KeyUsageBits[] {
          KeyUsageBits.DigitalSignature,
          KeyUsageBits.NonRepudiation,
        }));
    extMap1.put(
      ExtensionType.ExtendedKeyUsage,
      new KeyPurposeIdList(new KeyPurposeId[] {
        KeyPurposeId.EmailProtection,
        KeyPurposeId.ClientAuth,
        KeyPurposeId.SmartCardLogin,
      }));


    // Thawte Premium Server CA cert
    final File testCert2 = new File(RESOURCE_DIR,
        "thawte-premium-server-ca-cert.pem");
    final Map<ExtensionType, Object> extMap2 =
      new HashMap<ExtensionType, Object>();
    extMap2.put(ExtensionType.BasicConstraints, new BasicConstraints(true));


    // Microsoft Web server cert for login.live.com
    final File testCert3 = new File(RESOURCE_DIR,
        "login.live.com-cert.pem");
    final Map<ExtensionType, Object> extMap3 =
      new HashMap<ExtensionType, Object>();
    extMap3.put(ExtensionType.BasicConstraints, new BasicConstraints(false));
    extMap3.put(
      ExtensionType.SubjectKeyIdentifier,
      new KeyIdentifier(
        "31:AE:F1:7C:98:67:E9:1F:19:69:A2:A7:84:1E:67:5C:AA:C3:6B:75"));
    extMap3.put(
      ExtensionType.KeyUsage,
      new KeyUsage(
        new KeyUsageBits[] {
          KeyUsageBits.DigitalSignature,
          KeyUsageBits.KeyEncipherment,
        }));
    extMap3.put(
      ExtensionType.CertificatePolicies,
      new PolicyInformationList(
        Collections.singletonList(
          new PolicyInformation(
            "2.16.840.1.113733.1.7.23.6",
            new PolicyQualifierInfo[] {
              new PolicyQualifierInfo("https://www.verisign.com/rpa"),
            }))));
    extMap3.put(
      ExtensionType.ExtendedKeyUsage,
      new KeyPurposeIdList(new KeyPurposeId[] {
        KeyPurposeId.ClientAuth,
        KeyPurposeId.ServerAuth,
      }));
    extMap3.put(
      ExtensionType.AuthorityKeyIdentifier,
      new AuthorityKeyIdentifier(new KeyIdentifier(
        "FC:8A:50:BA:9E:B9:25:5A:7B:55:85:4F:95:00:63:8F:E9:58:6B:43")));
    extMap3.put(
      ExtensionType.CRLDistributionPoints,
      new DistributionPointList(Collections.singletonList(
        new DistributionPoint(
          new GeneralNameList(Collections.singletonList(
            new GeneralName(
              "http://EVSecure-crl.verisign.com/EVSecure2006.crl",
              GeneralNameType.UniformResourceIdentifier))),
          null,
          null))));

    return new Object[][] {
      {testCert1, extMap1},
      {testCert2, extMap2},
      {testCert3, extMap3},
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

    logger.info("Attributes found:");
    for (ExtensionType type : actualExtensionMap.keySet()) {
      logger.info("\t" + actualExtensionMap.get(type));
    }

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
