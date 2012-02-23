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

import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.x509.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for {@link ExtensionReader} class.
 *
 * @author  Middleware Services
 * @version  $Revision: 428 $
 */
public class ExtensionReaderTest
{

  /** Path to directory containing test resources. */
  private static final String RESOURCE_DIR =
    "src/test/resources/edu/vt/middleware/crypt/x509";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  /**
   * @return  Certificate test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "certdata")
  public Object[][] createCertificateTestData()
    throws Exception
  {
    // VT DEV User CA client certificate
    final File testCert1 = new File(RESOURCE_DIR, "marvin.pem");
    final Map<ExtensionType, Object> extMap1 =
      new HashMap<ExtensionType, Object>();
    extMap1.put(
      ExtensionType.SubjectAlternativeName,
      new GeneralNameList(
        new GeneralName[] {
          new GeneralName("serac@vt.edu", GeneralNameType.RFC822Name),
        }));
    extMap1.put(ExtensionType.BasicConstraints, new BasicConstraints(false));

    final PolicyInformation[] policies1 = new PolicyInformation[] {
      new PolicyInformation("1.3.6.1.4.1.6760.5.2.2.2.1"),
      new PolicyInformation("1.3.6.1.4.1.6760.5.2.2.1.1"),
      new PolicyInformation(
        "1.3.6.1.4.1.6760.5.2.2.4.1",
        new PolicyQualifierInfo[] {
          new PolicyQualifierInfo("http://www.pki.vt.edu/vtc1sca/cps/"),
        }),
      new PolicyInformation("1.3.6.1.4.1.6760.5.2.2.3.1"),
    };
    extMap1.put(
      ExtensionType.CertificatePolicies,
      new PolicyInformationList(policies1));
    extMap1.put(
      ExtensionType.SubjectKeyIdentifier,
      new KeyIdentifier(
        "FF:39:94:8A:03:97:04:D2:34:8E:79:0A:F9:5A:36:A2:EF:9B:FC:07"));
    extMap1.put(
      ExtensionType.AuthorityKeyIdentifier,
      new AuthorityKeyIdentifier(
        new KeyIdentifier(
          "B2:25:DC:8A:4D:E6:55:53:DC:D5:1A:65:11:D0:73:98:AF:9E:7F:07")));
    extMap1.put(
      ExtensionType.KeyUsage,
      new KeyUsage(
        new KeyUsageBits[] {
          KeyUsageBits.DigitalSignature,
          KeyUsageBits.NonRepudiation,
        }));
    extMap1.put(
      ExtensionType.ExtendedKeyUsage,
      new KeyPurposeIdList(
        new KeyPurposeId[] {
          KeyPurposeId.EmailProtection,
          KeyPurposeId.ClientAuth,
          KeyPurposeId.SmartCardLogin,
        }));
    extMap1.put(
        ExtensionType.CRLDistributionPoints,
        new DistributionPointList(
          Collections.singletonList(
            new DistributionPoint(
              new GeneralNameList(
                Collections.singletonList(
                  new GeneralName(
                    "http://balamood2.cc.vt.edu:8080/ejbca/publicweb/webdist/" +
                      "certdist?cmd=crl&issuer=CN=DEV Virginia Tech User CA" +
                      ",O=Virginia Polytechnic Institute and State University" +
                      ",DC=vt,DC=edu,C=US",
                    GeneralNameType.UniformResourceIdentifier))),
              null,
              null))));


    // Thawte Premium Server CA cert
    final File testCert2 = new File(
      RESOURCE_DIR,
      "thawte-premium-server-ca-cert.pem");
    final Map<ExtensionType, Object> extMap2 =
      new HashMap<ExtensionType, Object>();
    extMap2.put(ExtensionType.BasicConstraints, new BasicConstraints(true));


    // Microsoft Web server cert for login.live.com
    final File testCert3 = new File(RESOURCE_DIR, "login.live.com-cert.pem");
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
      new KeyPurposeIdList(
        new KeyPurposeId[] {
          KeyPurposeId.ClientAuth,
          KeyPurposeId.ServerAuth,
        }));
    extMap3.put(
      ExtensionType.AuthorityKeyIdentifier,
      new AuthorityKeyIdentifier(
        new KeyIdentifier(
          "FC:8A:50:BA:9E:B9:25:5A:7B:55:85:4F:95:00:63:8F:E9:58:6B:43")));
    extMap3.put(
      ExtensionType.CRLDistributionPoints,
      new DistributionPointList(
        Collections.singletonList(
          new DistributionPoint(
            new GeneralNameList(
              Collections.singletonList(
                new GeneralName(
                  "http://EVSecure-crl.verisign.com/EVSecure2006.crl",
                  GeneralNameType.UniformResourceIdentifier))),
            null,
            null))));
    extMap3.put(
      ExtensionType.AuthorityInformationAccess,
      new AccessDescriptionList(
        new AccessDescription[] {
          new AccessDescription(
            AccessMethod.OCSP,
            new GeneralName(
              "http://EVSecure-ocsp.verisign.com",
              GeneralNameType.UniformResourceIdentifier)),
          new AccessDescription(
            AccessMethod.CAIssuers,
            new GeneralName(
              "http://EVSecure-aia.verisign.com/EVSecure2006.cer",
              GeneralNameType.UniformResourceIdentifier)),
        }));

    return
      new Object[][] {
        {testCert1, extMap1},
        {testCert2, extMap2},
        {testCert3, extMap3},
      };
  }

  /**
   * @param  certFile  File containing X.509 certificate data.
   * @param  expectedExtensionMap  Expected map of extension types to extension
   * data that should be produced from reading the extended attributes of the
   * given certificate.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "x509"},
    dataProvider = "certdata"
  )
  public void testReadAll(
    final File certFile,
    final Map<ExtensionType, Object> expectedExtensionMap)
    throws Exception
  {
    logger.info("Testing read all extended attributes from {}", certFile);

    final Certificate cert = CryptReader.readCertificate(certFile);

    /*
    logger.info(
        "DER dump of {}:\n\n{}",
        certFile,
        ASN1Dump.dumpAsString(
            new ASN1InputStream(cert.getEncoded()).readObject()));
    */

    final ExtensionReader reader = new ExtensionReader((X509Certificate) cert);
    final Map<ExtensionType, Object> actualExtensionMap = reader.readAll();

    final StringBuilder sb = new StringBuilder();
    for (ExtensionType type : actualExtensionMap.keySet()) {
      sb.append('\t').append(type).append('=').append(
          actualExtensionMap.get(type)).append('\n');
    }
    logger.info("Attributes found:\n{}", sb.toString());

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
