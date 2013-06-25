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

import java.io.InputStream;
import java.security.KeyPair;
import java.security.SecureRandom;
import edu.vt.middleware.crypt.asymmetric.PublicKeyUtils;
import edu.vt.middleware.crypt.digest.MD5;
import edu.vt.middleware.crypt.digest.SHA256;
import edu.vt.middleware.crypt.digest.SHA512;
import edu.vt.middleware.crypt.digest.Tiger;
import edu.vt.middleware.crypt.digest.Whirlpool;
import edu.vt.middleware.crypt.util.Base64Converter;
import edu.vt.middleware.crypt.util.Converter;
import edu.vt.middleware.crypt.util.HexConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Unit test for all subclasses of {@link SignatureAlgorithm}.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class SignatureAlgorithmTest
{

  /** Data for testing. */
  private static final String CLEARTEXT = "Able was I ere I saw Elba";

  /** Classpath location of large plaintext data file. */
  private static final String BIG_FILE_PATH =
    "/edu/vt/middleware/crypt/plaintext.txt";

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
          new DSASignature(),
          PublicKeyUtils.generate("DSA", 1024),
          null,
        },
        {
          new DSASignature(),
          PublicKeyUtils.generate("DSA", 512),
          new Base64Converter(),
        },
        {
          new DSASignature(new SHA512()),
          PublicKeyUtils.generate("DSA", 1024),
          new HexConverter(),
        },
        {
          new DSASignature(new Tiger()),
          PublicKeyUtils.generate("DSA", 1024),
          new HexConverter(),
        },
        {
          new RSASignature(),
          PublicKeyUtils.generate("RSA", 1024),
          null,
        },
        {
          new RSASignature(),
          PublicKeyUtils.generate("RSA", 512),
          new Base64Converter(),
        },
        {
          new RSASignature(new MD5()),
          PublicKeyUtils.generate("RSA", 1048),
          new HexConverter(),
        },
        {
          new RSASignature(new Whirlpool()),
          PublicKeyUtils.generate("RSA", 1048),
          new HexConverter(),
        },
        {
          new ECDSASignature(),
          PublicKeyUtils.generate("EC", 256),
          null,
        },
        {
          new ECDSASignature(new SHA256()),
          PublicKeyUtils.generate("EC", 384),
          new HexConverter(),
        },
      };
  }


  /**
   * @return  Test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "testclone")
  public Object[][] createTestDataForClone()
    throws Exception
  {
    final SignatureAlgorithm rsa = new RSASignature(new SHA256());
    rsa.setSignKey(PublicKeyUtils.generate("RSA", 1576).getPrivate());
    final SignatureAlgorithm ecdsa = new ECDSASignature();
    ecdsa.setSignKey(PublicKeyUtils.generate("EC", 512).getPrivate());
    return
      new Object[][] {
        {rsa},
        {ecdsa},
      };
  }


  /**
   * @param  signature  A crypto signature algorithm to test.
   * @param  keys  Public/private key pair used for signing.
   * @param  converter  Converter used to convert sig bytes to String.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "signature"},
    dataProvider = "testdata"
  )
  public void testSignVerify(
    final SignatureAlgorithm signature,
    final KeyPair keys,
    final Converter converter)
    throws Exception
  {
    logger.info(
      "Testing signature algorithm {} with converter {}", signature, converter);
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
  @Test(
    groups = {"functest", "signature"},
    dataProvider = "testdata"
  )
  public void testRandomizedSignVerify(
    final SignatureAlgorithm signature,
    final KeyPair keys,
    final Converter converter)
    throws Exception
  {
    logger.info(
        "Testing randomized signature algorithm {} with converter {}",
        signature,
        converter);
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
  @Test(
    groups = {"functest", "signature"},
    dataProvider = "testdata"
  )
  public void testSignVerifyOnStream(
    final SignatureAlgorithm signature,
    final KeyPair keys,
    final Converter converter)
    throws Exception
  {
    logger.info(
        "Testing signature stream handling for {} with converter {}",
        signature,
        converter);

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


  /**
   * @param  algorithm  Signature algorithm to clone and test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "signature"},
    dataProvider = "testclone"
  )
  public void testClone(final SignatureAlgorithm algorithm)
    throws Exception
  {
    final SignatureAlgorithm clone = (SignatureAlgorithm) algorithm.clone();
    algorithm.initSign();
    clone.initSign();
    final byte[] cleartext = "Able was I ere I saw elba".getBytes();
    AssertJUnit.assertArrayEquals(algorithm.sign(cleartext), clone.sign(cleartext));
  }
}
