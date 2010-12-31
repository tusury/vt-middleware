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
package edu.vt.middleware.crypt.util;

import java.io.File;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.digest.MD2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link CryptReader} and {@link CryptWriter} classes.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class CryptReaderWriterTest
{

  /** Path to directory containing public/private keys. */
  private static final String KEY_DIR_PATH =
    "src/test/resources/edu/vt/middleware/crypt/keys/";

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(this.getClass());

  /**
   * @return  Public key test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "pubkeydata")
  public Object[][] createPubKeyTestData()
    throws Exception
  {
    final KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance("RSA");
    final KeyPair rsaKeys = rsaKeyGen.generateKeyPair();
    final KeyPairGenerator dsaKeyGen = KeyPairGenerator.getInstance("DSA");
    final KeyPair dsaKeys = dsaKeyGen.generateKeyPair();

    return new Object[][] {
      {rsaKeys.getPublic()},
      {dsaKeys.getPublic()},
    };
  }


  /**
   * @return  Private key test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "privkeydata")
  public Object[][] createPrivKeyTestData()
    throws Exception
  {
    final KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance("RSA");
    final KeyPair rsaKeys = rsaKeyGen.generateKeyPair();
    final KeyPairGenerator dsaKeyGen = KeyPairGenerator.getInstance("DSA");
    final KeyPair dsaKeys = dsaKeyGen.generateKeyPair();
    return
      new Object[][] {
        {rsaKeys.getPrivate(), "S33Kr1t!"},
        {dsaKeys.getPrivate(), "S33Kr1t!"},
        {rsaKeys.getPrivate(), null},
        {dsaKeys.getPrivate(), null},
      };
  }


  /**
   * @return  Private key test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "readprivkeydata")
  public Object[][] createReadPrivKeyTestData()
    throws Exception
  {
    return
      new Object[][] {
        {"dsa-openssl-priv-nopass.der", null},
        {"dsa-openssl-priv-nopass.pem", null},
        {"dsa-openssl-priv-des3.pem", "vtcrypt"},
        {"dsa-pkcs8-priv-nopass.pem", null},
        {"dsa-pkcs8-priv-nopass.der", null},
        {"dsa-pkcs8-priv-v2-des3.der", "vtcrypt"},
        {"dsa-pkcs8-priv-v2-des3.pem", "vtcrypt"},
        {"rsa-openssl-priv-nopass.der", null},
        {"rsa-openssl-priv-nopass.pem", null},
        {"rsa-openssl-priv-des.pem", "vtcrypt"},
        {"rsa-pkcs8-priv-nopass.der", null},
        {"rsa-pkcs8-priv-nopass.pem", null},
        {"rsa-pkcs8-priv.der", "vtcrypt"},
        {"rsa-pkcs8-priv.pem", "vtcrypt"},
        {"rsa-pkcs8-priv-v2-aes256.der", "vtcrypt"},
        {"rsa-pkcs8-priv-v2-aes256.pem", "vtcrypt"},
      };
  }


  /**
   * @return  Public key test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "readderpubkeydata")
  public Object[][] createReadDerPubKeyTestData()
    throws Exception
  {
    return new Object[][] {
      {"rsa-pub.der", "RSA"},
      {"dsa-pub.der", "DSA"},
    };
  }


  /**
   * @return  Public key test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "readpempubkeydata")
  public Object[][] createReadPemPubKeyTestData()
    throws Exception
  {
    return new Object[][] {
      {"rsa-pub.pem"},
      {"dsa-pub.pem"},
    };
  }


  /**
   * @param  file  Key file to read.
   * @param  alg  Cipher algorithm of key.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "util"},
    dataProvider = "readderpubkeydata"
  )
  public void testReadDerPublicKey(final String file, final String alg)
    throws Exception
  {
    final File keyFile = new File(KEY_DIR_PATH + file);
    logger.info("Testing read of DER-encoded public key " + keyFile);
    AssertJUnit.assertNotNull(CryptReader.readPublicKey(keyFile, alg));
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"functest", "util"})
  public void testReadDerCertificate()
    throws Exception
  {
    final File certFile = new File(KEY_DIR_PATH + "rsa-pub-cert.der");
    logger.info("Testing read of DER-encoded X.509 certificate " + certFile);
    AssertJUnit.assertNotNull(CryptReader.readCertificate(certFile));
  }


  /**
   * @param  file  Private key file to read.
   * @param  password  Private key encryption password; may be null to indicate
   * key is not encrypted.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "util"},
    dataProvider = "readprivkeydata"
  )
  public void testReadPrivateKey(final String file, final String password)
    throws Exception
  {
    final File keyFile = new File(KEY_DIR_PATH + file);
    logger.info("Testing read of private key " + keyFile);
    final PrivateKey key;
    if (password != null) {
      key = CryptReader.readPrivateKey(keyFile, password.toCharArray());
    } else {
      key = CryptReader.readPrivateKey(keyFile);
    }
    AssertJUnit.assertNotNull(key);
  }


  /**
   * @param  file  Public key file to read.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "util"},
    dataProvider = "readpempubkeydata"
  )
  public void testReadPemPublicKey(final String file)
    throws Exception
  {
    final File keyFile = new File(KEY_DIR_PATH + file);
    logger.info("Testing read of PEM-encoded public key " + keyFile);
    AssertJUnit.assertNotNull(CryptReader.readPemPublicKey(keyFile));
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"functest", "util"})
  public void testReadPemCertificate()
    throws Exception
  {
    final File certFile = new File(KEY_DIR_PATH + "rsa-pub-cert.pem");
    logger.info("Testing read of PEM-encoded X.509 certificate " + certFile);
    AssertJUnit.assertNotNull(CryptReader.readCertificate(certFile));
  }


  /**
   * @param  key  Key to write and read.
   * @param  password  Key encryption password.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "util"},
    dataProvider = "privkeydata"
  )
  public void testReadWriteEncodedPrivateKey(
    final PrivateKey key,
    final String password)
    throws Exception
  {
    logger.info("Testing " + key.getAlgorithm() + " private key.");

    final File keyFile = new File(getKeyPath(key, "DER", null));
    keyFile.getParentFile().mkdir();
    CryptWriter.writeEncodedKey(key, keyFile);
    AssertJUnit.assertEquals(
      key,
      CryptReader.readPrivateKey(keyFile));
  }


  /**
   * @param  key  Key to write and read.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "util"},
    dataProvider = "pubkeydata"
  )
  public void testReadWriteEncodedPublicKey(final PublicKey key)
    throws Exception
  {
    logger.info("Testing " + key.getAlgorithm() + " public key.");

    final File keyFile = new File(getKeyPath(key, "DER", null));
    keyFile.getParentFile().mkdir();
    CryptWriter.writeEncodedKey(key, keyFile);
    AssertJUnit.assertEquals(
      key,
      CryptReader.readPublicKey(keyFile, key.getAlgorithm()));
  }


  /**
   * @param  key  Key to write and read.
   * @param  password  Key encryption password.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "util"},
    dataProvider = "privkeydata"
  )
  public void testReadWritePemPrivateKey(
    final PrivateKey key,
    final String password)
    throws Exception
  {
    logger.info("Testing " + key.getAlgorithm() + " private key.");

    final char[] pwchars;
    if (password != null) {
      pwchars = password.toCharArray();
    } else {
      pwchars = null;
    }
    final File keyFile = new File(getKeyPath(key, "PEM", pwchars));
    keyFile.getParentFile().mkdir();
    CryptWriter.writePemKey(key, pwchars, new SecureRandom(), keyFile);

    final PrivateKey keyFromFile;
    if (pwchars != null) {
      keyFromFile = CryptReader.readPrivateKey(keyFile , pwchars);
    } else {
      keyFromFile = CryptReader.readPrivateKey(keyFile);
    }
    AssertJUnit.assertEquals(key, keyFromFile);
  }


  /**
   * @param  key  Key to write and read.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "util"},
    dataProvider = "pubkeydata"
  )
  public void testReadWritePemPublicKey(final PublicKey key)
    throws Exception
  {
    logger.info("Testing " + key.getAlgorithm() + " public key.");

    final File keyFile = new File(getKeyPath(key, "PEM", null));
    keyFile.getParentFile().mkdir();
    CryptWriter.writePemKey(key, keyFile);
    AssertJUnit.assertEquals(key, CryptReader.readPemPublicKey(keyFile));
  }


  /**
   * Generates a fingerprint for the given key.
   *
   * @param  key  Key to fingerprint.
   *
   * @return  Hashed representation of encoded key bytes.
   *
   * @throws  CryptException  On hash calculation errors.
   */
  private String fingerPrint(final Key key)
    throws CryptException
  {
    final MD2 hash = new MD2();
    return hash.digest(key.getEncoded(), new HexConverter());
  }


  /**
   * Gets a unique path to a private key.
   *
   * @param  key  Private key.
   * @param  type  PEM or DER.
   * @param  password  Password on private key; may be null.
   *
   * @return  Path to key.
   */
  private String getKeyPath(
    final Key key,
    final String type,
    final char[] password)
  {
    final StringBuffer sb = new StringBuffer();
    if (key instanceof PrivateKey) {
      sb.append("target/test-output/privkey_");
    } else if (key instanceof SecretKey) {
      sb.append("target/test-output/secretkey_");
    } else if (key instanceof PublicKey) {
      sb.append("target/test-output/pubkey_");
    } else {
      throw new IllegalArgumentException("Unrecognized key type.");
    }
    sb.append(key.getAlgorithm());
    sb.append("_");
    try {
      sb.append(fingerPrint(key));
    } catch (CryptException e) {
      sb.append(key.hashCode());
    }
    if (!(key instanceof PublicKey)) {
      if (password != null) {
        sb.append("_withpass");
      } else {
        sb.append("_nopass");
      }
    }
    sb.append('.');
    sb.append(type.toLowerCase());
    return sb.toString();
  }
}
