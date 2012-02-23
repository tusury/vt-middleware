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
package edu.vt.middleware.crypt.asymmetric;

import java.security.KeyPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for all subclasses of {@link AsymmetricAlgorithm}.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class AsymmetricAlgorithmTest
{

  /** Data for testing. */
  private static final String CLEARTEXT = "Able was I ere I saw Elba";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  /**
   * @return  Test datsymmetric.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "testdata")
  public Object[][] createTestData()
    throws Exception
  {
    final RSA rsa = new RSA();
    return
      new Object[][] {
        {
          rsa,
          rsa.generateKeys(),
        },
        {
          rsa,
          rsa.generateKeys(rsa.getDefaultKeyLength() * 2),
        },
      };
  }


  /**
   * @param  asymmetric  A symmetric crypt algorithm to test.
   * @param  keys  Key pair used for encryption/decryption.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "asymmetric"},
    dataProvider = "testdata"
  )
  public void testAsymmetricAlgorithm(
    final AsymmetricAlgorithm asymmetric,
    final KeyPair keys)
    throws Exception
  {
    logger.info("Testing symmetric algorithm {}", asymmetric);

    asymmetric.setKey(keys.getPublic());
    asymmetric.initEncrypt();

    final byte[] ciphertext = asymmetric.encrypt(CLEARTEXT.getBytes());
    asymmetric.setKey(keys.getPrivate());
    asymmetric.initDecrypt();
    AssertJUnit.assertEquals(
      CLEARTEXT.getBytes(),
      asymmetric.decrypt(ciphertext));
  }
}
