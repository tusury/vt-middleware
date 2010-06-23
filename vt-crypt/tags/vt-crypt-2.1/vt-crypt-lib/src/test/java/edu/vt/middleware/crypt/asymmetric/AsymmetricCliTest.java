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
package edu.vt.middleware.crypt.asymmetric;

import java.io.File;
import edu.vt.middleware.crypt.CliHelper;
import edu.vt.middleware.crypt.FileHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link AsymmetricCli} class.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class AsymmetricCliTest
{

  /** Small key length. */
  private static final int SMALL_KEY_LENGTH = 1024;

  /** Medium key length. */
  private static final int MEDIUM_KEY_LENGTH = 1536;

  /** Large key length. */
  private static final int LARGE_KEY_LENGTH = 2048;

  /**
   * Classpath location of large plaintext data file. Must ensure we choose keys
   * of 1024 or larger to meet requirement for RSA key size to be larger than
   * plaintext data.
   */
  private static final String TEST_PLAINTEXT =
    "src/test/resources/edu/vt/middleware/crypt/plaintext-127.txt";

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
    // Key size should be unique for each run
    return
      new Object[][] {
        {
          "rsa",
          null,
          new Integer(SMALL_KEY_LENGTH),
        },
        {
          "RSA",
          "base64",
          new Integer(MEDIUM_KEY_LENGTH),
        },
        {
          "RSA",
          "hex",
          new Integer(LARGE_KEY_LENGTH),
        },
      };
  }

  /**
   * @param  cipherName  Asymmetric cipher name.
   * @param  encoding  Name of ciphertext encoding format.
   * @param  keySize  Size of keys in bits.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"cli", "asymmetric"},
    dataProvider = "testdata"
  )
  public void testAsymmetricCli(
    final String cipherName,
    final String encoding,
    final Integer keySize)
    throws Exception
  {
    final File refFile = new File(TEST_PLAINTEXT);
    final File outDir = new File("target/test-output");
    outDir.mkdir();

    final File pubKeyFile = new File(
      outDir + "/" + cipherName + "-" + keySize + "-pub.key");
    final File privKeyFile = new File(
      outDir + "/" + cipherName + "-" + keySize + "-priv.key");
    final File cipherFile = new File(
      outDir + "/asymmetric-cli-cipher-" + cipherName + "-" + encoding +
      ".out");
    final File plainFile = new File(
      outDir + "/asymmetric-cli-plain-" + cipherName + "-" + encoding + ".txt");

    // Generate key
    String commandLine = " -cipher " + cipherName + " -genkeys " + keySize +
      " -privkey " + privKeyFile + " -out " + pubKeyFile;
    logger.info(
      "Testing asymmetric key generation with command line:\n\t" + commandLine);
    AsymmetricCli.main(CliHelper.splitArgs(commandLine));
    AssertJUnit.assertTrue(pubKeyFile.length() > 0L);
    AssertJUnit.assertTrue(privKeyFile.length() > 0L);

    // Encrypt plaintext
    commandLine = "-encrypt " + pubKeyFile + " -cipher " + cipherName +
      " -in " + refFile + " -out " + cipherFile;
    if (encoding != null) {
      commandLine += " -encoding " + encoding;
    }
    logger.info(
      "Testing asymmetric encryption with command line:\n\t" + commandLine);
    AsymmetricCli.main(CliHelper.splitArgs(commandLine));
    AssertJUnit.assertTrue(cipherFile.length() > 0L);

    // Decrypt ciphertext
    commandLine = "-decrypt " + privKeyFile + " -cipher " + cipherName +
      " -in " + cipherFile + " -out " + plainFile;
    if (encoding != null) {
      commandLine += " -encoding " + encoding;
    }
    logger.info(
      "Testing asymmetric decryption with command line:\n\t" + commandLine);
    AsymmetricCli.main(CliHelper.splitArgs(commandLine));
    AssertJUnit.assertTrue(plainFile.length() > 0L);
    AssertJUnit.assertTrue(FileHelper.equal(refFile, plainFile));
  }
}
