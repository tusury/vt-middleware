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
package edu.vt.middleware.crypt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link KeyStoreCli} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class KeyStoreCliTest
{

  /** Path to test output directory. */
  private static final String TEST_OUTPUT_DIR = "target/test-output/";

  /** Path to directory containing public/private keys. */
  private static final String KEY_DIR_PATH =
    "src/test/resources/edu/vt/middleware/crypt/keys/";

  /** Path to directory containing certificates. */
  private static final String CERT_DIR_PATH =
    "src/test/resources/edu/vt/middleware/crypt/x509/";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  /**
   * @return  Test data for creating key pair entries.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "keypairdata")
  public Object[][] createKeyPairTestData()
    throws Exception
  {
    return
      new Object[][] {
        {
          "store-1.jks",
          null,
          "rsa-pub-cert.der",
          "rsa-pkcs8-priv-nopass.der",
          null,
          "vtcrypt",
        },
        {
          "store-2.bks",
          "BKS",
          "rsa-pub-cert.der",
          "rsa-openssl-priv-nopass.der",
          "RSA",
          "VT Crypt",
        },
        {
          "store-3.p12",
          "PKCS12",
          "rsa-pub-cert.pem",
          "rsa-openssl-priv-nopass.pem",
          "RSA",
          "vt,crypt",
        },
        {
          "store-4.jks",
          null,
          "rsa-pub-cert.pem",
          "rsa-pkcs8-priv-nopass.pem",
          null,
          "vt:crypt",
        },
        {
          "store-5.jks",
          null,
          "rsa-pub-cert.pem",
          "rsa-pkcs8-priv-nopass.pem",
          null,
          "vt;crypt",
        },
        {
          "store-6.jks",
          null,
          "rsa-pub-cert.pem",
          "rsa-pkcs8-priv-nopass.pem",
          null,
          "vt'crypt",
        },
        {
          "store-7.jks",
          null,
          "rsa-pub-cert.der",
          "rsa-pkcs8-priv-nopass.der",
          null,
          "vt-crypt",
        },
      };
  }


  /**
   * @return  Test data for creating trusted cert entries.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "trustedcertdata")
  public Object[][] createTrustedCertTestData()
    throws Exception
  {
    return
      new Object[][] {
        {
          "store-8.jks",
          null,
          "vtmwca.pem",
        },
        {
          "store-9.bks",
          "BKS",
          "thawte-premium-server-ca-cert.pem",
        },
        {
          "store-A.p12",
          "PKCS12",
          "vtmwca.pem",
        },
      };
  }


  /**
   * @param  keyStore  Keystore file.
   * @param  storeType  Keystore type. Uses default type if null.
   * @param  cert  Certificate file.
   * @param  privKey  Private key file.
   * @param  keyAlg  Key algorithm name. Uses default key type if null.
   * @param  alias  Alias of keypair inside keystore.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"cli", "keystore"},
    dataProvider = "keypairdata"
  )
  public void testImportExportKeyPair(
    final String keyStore,
    final String storeType,
    final String cert,
    final String privKey,
    final String keyAlg,
    final String alias)
    throws Exception
  {
    new File(TEST_OUTPUT_DIR).mkdir();

    final PrintStream oldStdOut = System.out;
    final String keyStorePath = TEST_OUTPUT_DIR + keyStore;
    final String exportCertPath = TEST_OUTPUT_DIR + keyStore + "." + cert;
    final String exportKeyPath = TEST_OUTPUT_DIR + keyStore + "." + privKey;

    final OptionData keyStoreOption = new OptionData("keystore", keyStorePath);
    final OptionData storeTypeOption = storeType == null
      ? null : new OptionData("storetype", storeType);
    final OptionData storePassOption = new OptionData("storepass", "changeit");
    final OptionData keyAlgOption = keyAlg == null
      ? null : new OptionData("keyalg", keyAlg);
    final OptionData aliasOption = new OptionData("alias", alias);

    final OptionData[] listOptions = new OptionData[] {
      new OptionData("list"),
      keyStoreOption,
      storeTypeOption,
      storePassOption,
    };
    final OptionData[] importOptions = new OptionData[] {
      new OptionData("import"),
      keyStoreOption,
      storeTypeOption,
      storePassOption,
      new OptionData("cert", KEY_DIR_PATH + cert),
      new OptionData("key", KEY_DIR_PATH + privKey),
      keyAlgOption,
      aliasOption,
    };
    final OptionData[] exportOptions = new OptionData[] {
      new OptionData("export"),
      keyStoreOption,
      storeTypeOption,
      storePassOption,
      new OptionData("cert", exportCertPath),
      new OptionData("key", exportKeyPath),
      keyAlgOption,
      aliasOption,
    };
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      logger.info(
        "Importing keypair into keystore with command line {}",
        CliHelper.toCommandLine(importOptions));
      KeyStoreCli.main(CliHelper.toArgs(importOptions));
      AssertJUnit.assertTrue(new File(keyStorePath).exists());

      // Verify imported entry is present when we list contents
      outStream.reset();
      KeyStoreCli.main(CliHelper.toArgs(listOptions));

      final String output = outStream.toString();
      logger.info("Keystore listing output:\n{}", output);
      AssertJUnit.assertTrue(output.indexOf(alias) != -1);

      outStream.reset();

      logger.info(
        "Exporting keypair from keystore with command line {}",
        CliHelper.toCommandLine(exportOptions));
      KeyStoreCli.main(CliHelper.toArgs(exportOptions));
      AssertJUnit.assertTrue(new File(exportCertPath).exists());
      AssertJUnit.assertTrue(new File(exportKeyPath).exists());
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }


  /**
   * @param  keyStore  Path to JKS keystore file.
   * @param  storeType  Keystore type. Uses default type if null.
   * @param  cert  Certificate/certificate chain file.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"cli", "keystore"},
    dataProvider = "trustedcertdata"
  )
  public void testImportTrustedCert(
    final String keyStore,
    final String storeType,
    final String cert)
    throws Exception
  {
    new File(TEST_OUTPUT_DIR).mkdir();

    final PrintStream oldStdOut = System.out;
    final String keyStorePath = TEST_OUTPUT_DIR + keyStore;

    final String testAlias = "testng";
    final OptionData keyStoreOption = new OptionData("keystore", keyStorePath);
    final OptionData storeTypeOption = storeType == null
      ? null : new OptionData("storetype", storeType);
    final OptionData storePassOption = new OptionData("storepass", "changeit");
    final OptionData aliasOption = new OptionData("alias", testAlias);
    final OptionData[] listOptions = new OptionData[] {
      new OptionData("list"),
      keyStoreOption,
      storeTypeOption,
      storePassOption,
    };
    final OptionData[] importOptions = new OptionData[] {
      new OptionData("import"),
      keyStoreOption,
      storeTypeOption,
      storePassOption,
      new OptionData("cert", CERT_DIR_PATH + cert),
      aliasOption,
    };
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      logger.info(
        "Importing trusted cert into keystore with command line {}",
        CliHelper.toCommandLine(importOptions));
      KeyStoreCli.main(CliHelper.toArgs(importOptions));
      AssertJUnit.assertTrue(new File(keyStorePath).exists());

      // Verify imported entry is present and has correct length
      // when we list contents
      outStream.reset();
      KeyStoreCli.main(CliHelper.toArgs(listOptions));

      final String output = outStream.toString();
      logger.info("Keystore listing output:\n{}", output);
      AssertJUnit.assertTrue(output.indexOf(testAlias) != -1);

      outStream.reset();
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
