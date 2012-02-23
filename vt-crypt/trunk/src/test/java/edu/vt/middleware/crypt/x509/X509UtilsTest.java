/*
  $Id: $

  Copyright (C) 2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.crypt.x509;

import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.x509.types.GeneralNameType;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Description of X509UtilsTest.
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public class X509UtilsTest
{
  /** Path to cert directory. */
  private static final String CERT_DIR =
      "src/test/resources/edu/vt/middleware/crypt/x509/";


  /**
   * @return  Certificate subject name test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "subject-names")
  public Object[][] createSubjectNameTestData() throws Exception
  {
    return new Object[][] {
        new Object[] {
          "marvin.pem",
          new GeneralNameType[] { GeneralNameType.EdiPartyName },
          new String[] { "Marvin S Addison" },
        },
        new Object[] {
            "marvin.pem",
            new GeneralNameType[] { GeneralNameType.RFC822Name },
            new String[] { "Marvin S Addison", "serac@vt.edu" },
        },
    };
  }


  /**
   * @return  Certificate entity finder test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "entity-certs")
  public Object[][] createEntityCertTestData() throws Exception
  {
    return new Object[][] {
        new Object[] {
            new String[] { "marvin.pem", "entity-cert.pem" },
            "entity-key.pem",
            "entity-cert.pem",
        },
        new Object[] {
            new String[] { "marvin.pem", "login.live.com-cert.pem" },
            "entity-key.pem",
            null,
        },
    };
  }


  @Test(
      groups = {"functest", "x509"},
      dataProvider = "subject-names"
  )
  public void testGetSubjectNames(
      final String certName,
      final GeneralNameType[] nameTypes,
      final String[] expectedNames) throws Exception
  {
    final List<String> actualNames = X509Utils.getSubjectNames(
        getCert(certName), nameTypes);
    AssertJUnit.assertEquals(expectedNames.length, actualNames.size());
    for (int i = 0; i < expectedNames.length; i++) {
      AssertJUnit.assertEquals(expectedNames[i], actualNames.get(i));
    }
  }


  @Test(
      groups = {"functest", "x509"},
      dataProvider = "entity-certs"
  )
  public void testFindEntityCertificate(
      final String[] certNames,
      final String keyName,
      final String expectedName) throws Exception
  {
    final X509Certificate[] certs = new X509Certificate[certNames.length];
    for (int i = 0; i < certNames.length; i++) {
      certs[i] = getCert(certNames[i]);
    }
    final PrivateKey key = CryptReader.readPrivateKey(
        new File(CERT_DIR + keyName));
    final X509Certificate expectedCert;
    if (expectedName != null) {
      expectedCert = getCert(expectedName);
    } else {
      expectedCert = null;
    }
    AssertJUnit.assertEquals(
        expectedCert,
        X509Utils.findEntityCertificate(certs, key));
  }


  private X509Certificate getCert(final String name) throws Exception
  {
    return (X509Certificate) CryptReader.readCertificate(
        new File(CERT_DIR + name));
  }
}
