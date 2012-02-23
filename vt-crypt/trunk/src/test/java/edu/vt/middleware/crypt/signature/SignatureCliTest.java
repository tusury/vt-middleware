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
package edu.vt.middleware.crypt.signature;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.vt.middleware.crypt.CliHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SignatureCli} class.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class SignatureCliTest
{

  /** Path to test output directory. */
  private static final String TEST_OUTPUT_DIR = "target/test-output/";

  /** Classpath location of file to be signed. */
  private static final String TEST_PLAINTEXT =
    "src/test/resources/edu/vt/middleware/crypt/plaintext-127.txt";

  /** Path to directory containing public/private keys. */
  private static final String KEY_DIR_PATH =
    "src/test/resources/edu/vt/middleware/crypt/keys/";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


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
          "-alg dsa -encoding hex",
          "dsa-pub.der",
          "dsa-openssl-priv-nopass.der",
        },
        {
          "-alg DSA -digest MD5 -encoding hex",
          "dsa-pub.pem",
          "dsa-openssl-priv-nopass.pem",
        },
        {
          "-alg DSA -digest whirlpool -encoding base64",
          "dsa-pub.der",
          "dsa-pkcs8-priv-nopass.der",
        },
        {
          "-alg rsa -encoding hex",
          "rsa-pub-cert.der",
          "rsa-openssl-priv-nopass.der",
        },
        {
          "-alg RSA -digest SHA384 -encoding hex",
          "rsa-pub-cert.pem",
          "rsa-openssl-priv-nopass.pem",
        },
        {
          "-alg RSA -digest ripemd160 -encoding base64",
          "rsa-pub.der",
          "rsa-pkcs8-priv-nopass.der",
        },
      };
  }


  /**
   * @param  partialLine  Partial command line.
   * @param  pubKey  Public key file.
   * @param  privKey  Private key file.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"cli", "signature"},
    dataProvider = "testdata"
  )
  public void testSignatureCli(
    final String partialLine,
    final String pubKey,
    final String privKey)
    throws Exception
  {
    final String pubKeyPath = KEY_DIR_PATH + pubKey;
    final String privKeyPath = KEY_DIR_PATH + privKey;
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      // Compute signature and verify it
      String fullLine = partialLine + " -sign " +
        " -key " + privKeyPath + " -in " + TEST_PLAINTEXT;
      logger.info(
        "Testing signature CLI sign operation with command line:\n\t{}",
        fullLine);
      SignatureCli.main(CliHelper.splitArgs(fullLine));

      final String sig = outStream.toString();
      Assert.assertTrue(sig.length() > 0);

      // Write signature out to file for use in verify step
      new File(TEST_OUTPUT_DIR).mkdir();

      final File sigFile = new File(TEST_OUTPUT_DIR + "sig.out");
      final BufferedOutputStream sigOs = new BufferedOutputStream(
        new FileOutputStream(sigFile));
      try {
        sigOs.write(outStream.toByteArray());
      } finally {
        sigOs.close();
      }
      outStream.reset();

      // Verify signature
      fullLine = partialLine + " -verify " + sigFile + " -key " + pubKeyPath +
        " -in " + TEST_PLAINTEXT;
      logger.info(
        "Testing signature CLI verify operation with command line:\n\t{}",
        fullLine);
      SignatureCli.main(CliHelper.splitArgs(fullLine));

      final String result = outStream.toString();
      AssertJUnit.assertTrue(result.indexOf("SUCCESS") != -1);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
