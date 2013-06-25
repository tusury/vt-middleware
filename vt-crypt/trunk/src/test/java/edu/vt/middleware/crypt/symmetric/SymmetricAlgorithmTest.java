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
package edu.vt.middleware.crypt.symmetric;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import edu.vt.middleware.crypt.FileHelper;
import edu.vt.middleware.crypt.io.Base64FilterInputStream;
import edu.vt.middleware.crypt.io.Base64FilterOutputStream;
import edu.vt.middleware.crypt.io.HexFilterInputStream;
import edu.vt.middleware.crypt.io.HexFilterOutputStream;
import edu.vt.middleware.crypt.util.Base64Converter;
import edu.vt.middleware.crypt.util.Converter;
import edu.vt.middleware.crypt.util.HexConverter;
import org.bouncycastle.util.test.FixedSecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for all subclasses of {@link SymmetricAlgorithm}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SymmetricAlgorithmTest
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
    final AES aes = new AES();
    final Blowfish blowfish = new Blowfish();
    final CAST5 cast5 = new CAST5();
    final CAST6 cast6 = new CAST6();
    final DES des = new DES();
    final DESede desede = new DESede();
    final RC2 rc2 = new RC2();
    final RC4 rc4 = new RC4();
    final RC5 rc5 = new RC5();
    final RC6 rc6 = new RC6();
    final Rijndael rijndael = new Rijndael();
    final Serpent serpent = new Serpent();
    final Skipjack skipjack = new Skipjack();
    final Twofish twofish = new Twofish();
    return
      new Object[][] {
        {aes, SecretKeyUtils.generate("AES", aes.getMinKeyLength())},
        {aes, SecretKeyUtils.generate("AES", aes.getMaxKeyLength())},
        {blowfish, SecretKeyUtils.generate("Blowfish", blowfish.getMinKeyLength())},
        {blowfish, SecretKeyUtils.generate("Blowfish", blowfish.getMaxKeyLength())},
        {cast5, SecretKeyUtils.generate("CAST5", cast5.getMinKeyLength())},
        {cast5, SecretKeyUtils.generate("CAST5", cast5.getMaxKeyLength())},
        {cast6, SecretKeyUtils.generate("CAST6", cast6.getMinKeyLength())},
        {cast5, SecretKeyUtils.generate("CAST6", cast6.getMaxKeyLength())},
        {des, SecretKeyUtils.generate("DES", des.getMinKeyLength())},
        {desede, SecretKeyUtils.generate("DESede", desede.getMinKeyLength())},
        {rc2, SecretKeyUtils.generate("RC2", rc2.getMinKeyLength())},
        {rc2, SecretKeyUtils.generate("RC2", rc2.getMaxKeyLength())},
        {rc4, SecretKeyUtils.generate("RC4", rc4.getMinKeyLength())},
        {rc4, SecretKeyUtils.generate("RC4", rc4.getMaxKeyLength())},
        {rc5, SecretKeyUtils.generate("RC5", rc5.getMinKeyLength())},
        {rc5, SecretKeyUtils.generate("RC5", rc5.getMaxKeyLength())},
        {rc6, SecretKeyUtils.generate("RC6", rc6.getMinKeyLength())},
        {rc6, SecretKeyUtils.generate("RC6", rc6.getMaxKeyLength())},
        {rijndael, SecretKeyUtils.generate("Rijndael", rijndael.getMinKeyLength())},
        {rijndael, SecretKeyUtils.generate("Rijndael", rijndael.getMaxKeyLength())},
        {serpent, SecretKeyUtils.generate("Serpent", serpent.getMinKeyLength())},
        {serpent, SecretKeyUtils.generate("Serpent", serpent.getMaxKeyLength())},
        {skipjack, SecretKeyUtils.generate("Skipjack", skipjack.getMinKeyLength())},
        {twofish, SecretKeyUtils.generate("Twofish", twofish.getMinKeyLength())},
        {twofish, SecretKeyUtils.generate("Twofish", twofish.getMaxKeyLength())},
      };
  }


  /**
   * @return  Test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "testdataconv")
  public Object[][] createTestDataForConv()
    throws Exception
  {
    final AES aes = new AES();
    return
      new Object[][] {
        {aes, new HexConverter()},
        {aes, new Base64Converter()},
      };
  }


  /**
   * @return  Test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "testdatastreams")
  public Object[][] createTestDataForStreams()
    throws Exception
  {
    return
      new Object[][] {
        {new AES("CBC", "NoPadding")},
        {new AES("CBC", "PKCS5Padding")},
        {new AES("CFB", "NoPadding")},
        {new AES("CFB", "PKCS5Padding")},
        {new AES("ECB", "NoPadding")},
        {new AES("ECB", "PKCS5Padding")},
        {new AES("OFB", "NoPadding")},
        {new AES("OFB", "PKCS5Padding")},
        {new DES("CBC", "NoPadding")},
        {new DES("CBC", "PKCS5Padding")},
        {new DES("CFB", "NoPadding")},
        {new DES("CFB", "PKCS5Padding")},
        {new DES("ECB", "NoPadding")},
        {new DES("ECB", "PKCS5Padding")},
        {new DES("OFB", "NoPadding")},
        {new DES("OFB", "PKCS5Padding")},
        {new Twofish("CBC", "NoPadding")},
        {new Twofish("CBC", "PKCS5Padding")},
        {new Twofish("CFB", "NoPadding")},
        {new Twofish("CFB", "PKCS5Padding")},
        {new Twofish("ECB", "NoPadding")},
        {new Twofish("ECB", "PKCS5Padding")},
        {new Twofish("OFB", "NoPadding")},
        {new Twofish("OFB", "PKCS5Padding")},
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

    final SymmetricAlgorithm aes1 = new AES();
    aes1.setRandomProvider(new FixedSecureRandom(aes1.getRandomData(1024)));
    aes1.setIV(aes1.getRandomIV());
    aes1.setKey(SecretKeyUtils.generate("AES", 256));
    return
      new Object[][] {
        {aes1},
      };
  }


  /**
   * @param  symmetric  A symmetric crypt algorithm to test.
   * @param  key  Symmetric encryption key.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "symmetric"},
    dataProvider = "testdata"
  )
  public void testEncryptDecryptText(
    final SymmetricAlgorithm symmetric,
    final Key key)
    throws Exception
  {
    logger.info("Testing symmetric algorithm {} with key {}", symmetric, key);

    symmetric.setKey(key);
    if (!RC4.ALGORITHM.equals(symmetric.getAlgorithm())) {
      symmetric.setIV(symmetric.getRandomIV());
    }
    symmetric.initEncrypt();

    final byte[] ciphertext = symmetric.encrypt(CLEARTEXT.getBytes());
    symmetric.initDecrypt();
    AssertJUnit.assertEquals(
      CLEARTEXT.getBytes(),
      symmetric.decrypt(ciphertext));
  }


  /**
   * @param  symmetric  A symmetric crypt algorithm to test.
   * @param  converter  Converts ciphertext to/from string representation.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "symmetric"},
    dataProvider = "testdataconv"
  )
  public void testEncryptDecryptTextConverter(
    final SymmetricAlgorithm symmetric,
    final Converter converter)
    throws Exception
  {
    logger.info(
        "Testing symmetric algorithm {} with converter {}",
        symmetric,
        converter);

    symmetric.setKey(SecretKeyUtils.generate(
        symmetric.getAlgorithm(), symmetric.getMinKeyLength()));
    symmetric.setIV(symmetric.getRandomIV());
    symmetric.initEncrypt();

    final String ciphertext = symmetric.encrypt(
      CLEARTEXT.getBytes(),
      converter);
    symmetric.initDecrypt();
    AssertJUnit.assertEquals(
      CLEARTEXT.getBytes(),
      symmetric.decrypt(ciphertext, converter));
  }


  /**
   * @param  symmetric  A symmetric crypt algorithm to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "symmetric"},
    dataProvider = "testdatastreams"
  )
  public void testEncryptDecryptStream(final SymmetricAlgorithm symmetric)
    throws Exception
  {
    logger.info("Testing stream handling of algorithm {}", symmetric);

    encrypt(
      symmetric,
      getClass().getResourceAsStream(BIG_FILE_PATH),
      new Base64FilterOutputStream(
        FileHelper.getOut("ciphertext", symmetric, null)));

    decrypt(
      symmetric,
      new Base64FilterInputStream(
        FileHelper.getIn("ciphertext", symmetric, null)),
      FileHelper.getOut("plaintext", symmetric, null));

    // Compare original plaintext with that resulting from encrypt/decrypt
    final InputStream refIn = getClass().getResourceAsStream(BIG_FILE_PATH);
    final InputStream testIn = FileHelper.getIn("plaintext", symmetric, null);
    try {
      AssertJUnit.assertTrue(FileHelper.equal(refIn, testIn));
    } finally {
      if (refIn != null) {
        refIn.close();
      }
      if (testIn != null) {
        testIn.close();
      }
    }
  }


  /**
   * @param  symmetric  A symmetric crypt algorithm to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "symmetric"},
    dataProvider = "testdatastreams"
  )
  public void testEncryptDecryptStreamHex(final SymmetricAlgorithm symmetric)
    throws Exception
  {
    logger.info("Testing hex stream handling of algorithm {}", symmetric);

    encrypt(
      symmetric,
      getClass().getResourceAsStream(BIG_FILE_PATH),
      new HexFilterOutputStream(
        FileHelper.getOut("ciphertext", symmetric, "HEX")));

    decrypt(
      symmetric,
      new HexFilterInputStream(
        FileHelper.getIn("ciphertext", symmetric, "HEX")),
      FileHelper.getOut("plaintext", symmetric, "HEX"));

    // Compare original plaintext with that resulting from encrypt/decrypt
    final InputStream refIn = getClass().getResourceAsStream(BIG_FILE_PATH);
    final InputStream testIn = FileHelper.getIn("plaintext", symmetric, "HEX");
    try {
      AssertJUnit.assertTrue(FileHelper.equal(refIn, testIn));
    } finally {
      if (refIn != null) {
        refIn.close();
      }
      if (testIn != null) {
        testIn.close();
      }
    }
  }


  /**
   * @param  symmetric  A symmetric crypt algorithm to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "symmetric"},
    dataProvider = "testdatastreams"
  )
  public void testEncryptDecryptStreamBase64(final SymmetricAlgorithm symmetric)
    throws Exception
  {
    logger.info("Testing base-64 stream handling of algorithm {}", symmetric);

    encrypt(
      symmetric,
      getClass().getResourceAsStream(BIG_FILE_PATH),
      new Base64FilterOutputStream(
        FileHelper.getOut("ciphertext", symmetric, "B64")));

    decrypt(
      symmetric,
      new Base64FilterInputStream(
        FileHelper.getIn("ciphertext", symmetric, "B64")),
      FileHelper.getOut("plaintext", symmetric, "B64"));

    // Compare original plaintext with that resulting from encrypt/decrypt
    final InputStream refIn = getClass().getResourceAsStream(BIG_FILE_PATH);
    final InputStream testIn = FileHelper.getIn("plaintext", symmetric, "B64");
    try {
      AssertJUnit.assertTrue(FileHelper.equal(refIn, testIn));
    } finally {
      if (refIn != null) {
        refIn.close();
      }
      if (testIn != null) {
        testIn.close();
      }
    }
  }


  /**
   * @param  algorithm  Symmetric algorithm to clone and test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "symmetric"},
    dataProvider = "testclone"
  )
  public void testClone(final SymmetricAlgorithm algorithm)
    throws Exception
  {
    final SymmetricAlgorithm clone = (SymmetricAlgorithm) algorithm.clone();
    algorithm.initEncrypt();
    clone.initEncrypt();
    final byte[] cleartext = "Able was I ere I saw elba".getBytes();
    AssertJUnit.assertArrayEquals(algorithm.encrypt(cleartext), clone.encrypt(cleartext));
  }


  /**
   * Encrypts an input stream of plaintext into an output stream of ciphertext
   * using the given algorithm.
   *
   * @param  symmetric  Symmetric algorithm used for encryption.
   * @param  in  Input stream containing plaintext to encrypt.
   * @param  out  Output stream containing resulting ciphertext.
   *
   * @throws  Exception  On errors.
   */
  private void encrypt(
    final SymmetricAlgorithm symmetric,
    final InputStream in,
    final OutputStream out)
    throws Exception
  {
    try {
      symmetric.setKey(SecretKeyUtils.generate(
          symmetric.getAlgorithm(), symmetric.getMinKeyLength()));
      if (
        !RC4.ALGORITHM.equals(symmetric.getAlgorithm()) &&
          !"ECB".equals(symmetric.getMode())) {
        symmetric.setIV(symmetric.getRandomIV());
      }
      symmetric.initEncrypt();
      logger.info("Encrypting plaintext.");
      symmetric.encrypt(in, out);
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }


  /**
   * Decrypts an input stream of ciphertext into an output stream of plaintext
   * using the given algorithm.
   *
   * @param  symmetric  Symmetric algorithm used for decryption.
   * @param  in  Input stream containing ciphertext to decrypt.
   * @param  out  Output stream containing resulting plaintext.
   *
   * @throws  Exception  On errors.
   */
  private void decrypt(
    final SymmetricAlgorithm symmetric,
    final InputStream in,
    final OutputStream out)
    throws Exception
  {
    try {
      symmetric.initDecrypt();
      logger.info("Decrypting ciphertext.");
      symmetric.decrypt(in, out);
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }
}
