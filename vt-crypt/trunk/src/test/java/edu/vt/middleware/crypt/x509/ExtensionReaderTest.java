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
import edu.vt.middleware.crypt.x509.types.GeneralName;
import edu.vt.middleware.crypt.x509.types.GeneralNameType;
import edu.vt.middleware.crypt.x509.types.GeneralNames;

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
        new GeneralNames(new GeneralName[] {
          new GeneralName("eprov@vt.edu", GeneralNameType.RFC822Name),
        }));

    return new Object[][] {
      {testCert1, extMap1},
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
