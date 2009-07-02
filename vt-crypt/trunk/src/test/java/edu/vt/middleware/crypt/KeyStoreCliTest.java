/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    "src/test/resources/edu/vt/middleware/crypt/";

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(this.getClass());


  /**
   * @return  Test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "testdata")
  public Object[][] createTestData()
    throws Exception
  {
    return
      new Object[][] {
        {
          "store-1.jks",
          "rsa.cert.der",
          "rsa.pri-pkcs8.der",
          "vtcrypt",
          null,
        },
        {
          "store-2.bks",
          "rsa.cert.pem",
          "rsa.pri.pem",
          "VT Crypt",
          "-storetype BKS -keyalg RSA",
        },
        {
          "store-3.p12",
          "rsa.cert.pem",
          "rsa.pri.pem",
          "vt,crypt",
          "-storetype PKCS12 -keyalg RSA",
        },
        {
          "store-4.jks",
          "rsa.cert.pem",
          "rsa.pri.pem",
          "vt:crypt",
          null,
        },
        {
          "store-5.jks",
          "rsa.cert.pem",
          "rsa.pri.pem",
          "vt;crypt",
          null,
        },
        {
          "store-6.jks",
          "rsa.cert.pem",
          "rsa.pri.pem",
          "vt'crypt",
          null,
        },
        {
          "store-7.jks",
          "rsa.cert.pem",
          "rsa.pri.pem",
          "vt-crypt",
          null,
        },
      };
  }


  /**
   * @param  keyStore  Keystore file.
   * @param  cert  Certificate file.
   * @param  privKey  Private key file.
   * @param  alias  Alias of keypair inside keystore.
   * @param  partialLine  Partial command line with additional optional args.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"cli", "keystore"}, dataProvider = "testdata")
  public void testKeyStoreCli(
    final String keyStore,
    final String cert,
    final String privKey,
    final String alias,
    final String partialLine)
    throws Exception
  {
    new File(TEST_OUTPUT_DIR).mkdir();

    final String keyStorePath = TEST_OUTPUT_DIR + keyStore;
    final String certPath = KEY_DIR_PATH + cert;
    final String privKeyPath = KEY_DIR_PATH + privKey;
    final PrintStream oldStdOut = System.out;
    final String listCommandLine = partialLine + " -list" +
      " -keystore " + keyStorePath + " -storepass changeit";
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      String commandLine = partialLine + " -import " +
        " -cert " + certPath + " -key " + privKeyPath + " -keystore " +
        keyStorePath + " -alias " + alias + " -storepass changeit";

      logger.info(
        "Importing keypair into keystore with command line " + commandLine);
      KeyStoreCli.main(CliHelper.splitArgs(commandLine));
      AssertJUnit.assertTrue(new File(keyStorePath).exists());

      // Verify imported entry is present when we list contents
      outStream.reset();
      KeyStoreCli.main(CliHelper.splitArgs(listCommandLine));

      final String output = outStream.toString();
      logger.info("Keystore listing output:\n" + output);
      AssertJUnit.assertTrue(output.indexOf(alias) != -1);

      outStream.reset();

      final String exportCertPath = TEST_OUTPUT_DIR + keyStore + "." + cert;
      final String exportKeyPath = TEST_OUTPUT_DIR + keyStore + "." + privKey;
      commandLine = partialLine + " -export " +
        " -cert " + exportCertPath + " -key " + exportKeyPath + " -keystore " +
        keyStorePath + " -alias " + alias + " -storepass changeit";
      logger.info(
        "Exporting keypair from keystore with command line " + commandLine);
      KeyStoreCli.main(CliHelper.splitArgs(commandLine));
      AssertJUnit.assertTrue(new File(exportCertPath).exists());
      AssertJUnit.assertTrue(new File(exportKeyPath).exists());
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
