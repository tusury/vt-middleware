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
package edu.vt.middleware.crypt.signature;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import edu.vt.middleware.crypt.digest.MD5;
import edu.vt.middleware.crypt.digest.SHA512;
import edu.vt.middleware.crypt.digest.Tiger;
import edu.vt.middleware.crypt.digest.Whirlpool;
import edu.vt.middleware.crypt.util.Base64Converter;
import edu.vt.middleware.crypt.util.Converter;
import edu.vt.middleware.crypt.util.HexConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Unit test for all subclasses of {@link SignatureAlgorithm}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SignatureAlgorithmTest
{

  /** Data for testing. */
  private static final String CLEARTEXT = "Able was I ere I saw Elba";

  /** Classpath location of large plaintext data file. */
  private static final String BIG_FILE_PATH =
    "/edu/vt/middleware/crypt/plaintext.txt";

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
    final KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance("RSA");
    final KeyPair rsaKeys = rsaKeyGen.generateKeyPair();

    final KeyPairGenerator dsaKeyGen = KeyPairGenerator.getInstance("DSA");
    final KeyPair dsaKeys = dsaKeyGen.generateKeyPair();

    return
      new Object[][] {
        {
          new DSASignature(),
          dsaKeys,
          null,
        },
        {
          new DSASignature(),
          dsaKeys,
          new Base64Converter(),
        },
        {
          new DSASignature(new SHA512()),
          dsaKeys,
          new HexConverter(),
        },
        {
          new DSASignature(new Tiger()),
          dsaKeys,
          new HexConverter(),
        },
        {
          new RSASignature(),
          rsaKeys,
          null,
        },
        {
          new RSASignature(),
          rsaKeys,
          new Base64Converter(),
        },
        {
          new RSASignature(new MD5()),
          rsaKeys,
          new HexConverter(),
        },
        {
          new RSASignature(new Whirlpool()),
          rsaKeys,
          new HexConverter(),
        },
      };
  }


  /**
   * @param  signature  A crypto signature algorithm to test.
   * @param  keys  Public/private key pair used for signing.
   * @param  converter  Converter used to convert sig bytes to String.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "signature"}, dataProvider = "testdata")
  public void testSignVerify(
    final SignatureAlgorithm signature,
    final KeyPair keys,
    final Converter converter)
    throws Exception
  {
    logger.info(
      "Testing signature algorithm " + signature + " with converter " +
      converter);
    signature.setSignKey(keys.getPrivate());
    signature.initSign();
    if (converter == null) {
      final byte[] signedBytes = signature.sign(CLEARTEXT.getBytes());
      signature.setVerifyKey(keys.getPublic());
      signature.initVerify();
      AssertJUnit.assertTrue(
        signature.verify(CLEARTEXT.getBytes(), signedBytes));
    } else {
      final String sig = signature.sign(CLEARTEXT.getBytes(), converter);
      signature.setVerifyKey(keys.getPublic());
      signature.initVerify();
      AssertJUnit.assertTrue(
        signature.verify(CLEARTEXT.getBytes(), sig, converter));
    }
  }


  /**
   * @param  signature  A crypto signature algorithm to test.
   * @param  keys  Public/private key pair used for signing.
   * @param  converter  Converter used to convert sig bytes to String.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "signature"}, dataProvider = "testdata")
  public void testRandomizedSignVerify(
    final SignatureAlgorithm signature,
    final KeyPair keys,
    final Converter converter)
    throws Exception
  {
    logger.info(
      "Testing randomized signature algorithm " + signature +
      " with converter " + converter);
    signature.setRandomProvider(new SecureRandom());
    signature.setSignKey(keys.getPrivate());
    signature.initSign();
    if (converter == null) {
      final byte[] signedBytes = signature.sign(CLEARTEXT.getBytes());
      signature.setVerifyKey(keys.getPublic());
      signature.initVerify();
      AssertJUnit.assertTrue(
        signature.verify(CLEARTEXT.getBytes(), signedBytes));
    } else {
      final String sig = signature.sign(CLEARTEXT.getBytes(), converter);
      signature.setVerifyKey(keys.getPublic());
      signature.initVerify();
      AssertJUnit.assertTrue(
        signature.verify(CLEARTEXT.getBytes(), sig, converter));
    }
  }


  /**
   * @param  signature  A crypto signature algorithm to test.
   * @param  keys  Public/private key pair used for signing.
   * @param  converter  Converter used to convert sig bytes to String.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "signature"}, dataProvider = "testdata")
  public void testSignVerifyOnStream(
    final SignatureAlgorithm signature,
    final KeyPair keys,
    final Converter converter)
    throws Exception
  {
    logger.info(
      "Testing signature stream handling for " + signature +
      " with converter " + converter);

    final InputStream in1 = getClass().getResourceAsStream(BIG_FILE_PATH);
    final InputStream in2 = getClass().getResourceAsStream(BIG_FILE_PATH);
    try {
      signature.setRandomProvider(new SecureRandom());
      signature.setSignKey(keys.getPrivate());
      signature.initSign();
      if (converter == null) {
        final byte[] signedBytes = signature.sign(in1);
        signature.setVerifyKey(keys.getPublic());
        signature.initVerify();
        AssertJUnit.assertTrue(signature.verify(in2, signedBytes));
      } else {
        final String sig = signature.sign(in1, converter);
        signature.setVerifyKey(keys.getPublic());
        signature.initVerify();
        AssertJUnit.assertTrue(signature.verify(in2, sig, converter));
      }
    } finally {
      if (in1 != null) {
        in1.close();
      }
      if (in2 != null) {
        in2.close();
      }
    }
  }
}
