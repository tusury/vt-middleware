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
 * @version  $Revision$
 */
public class CryptReaderWriterTest
{

  /** Path to directory containing public/private keys. */
  private static final String KEY_DIR_PATH =
    "src/test/resources/edu/vt/middleware/crypt/";

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

    final char[] password = "S33Kr1t!".toCharArray();
    return new Object[][] {
      {rsaKeys.getPrivate(), password},
      {dsaKeys.getPrivate(), password},
      {rsaKeys.getPrivate(), null},
      {dsaKeys.getPrivate(), null},
    };
  }


  /**
   * @return  Private key test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "readderprivkeydata")
  public Object[][] createReadDerPrivKeyTestData()
    throws Exception
  {
    return new Object[][] {
      {"rsa.pri.der", "RSA"},
      {"rsa.pri-pkcs8.der", "RSA"},
      {"dsa.pri-pkcs8.der", "DSA"},
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
      {"rsa.pub.der", "RSA"},
      {"dsa.pub.der", "DSA"},
    };
  }


  /**
   * @return  Private key test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "readpemprivkeydata")
  public Object[][] createReadPemPrivKeyTestData()
    throws Exception
  {
    return new Object[][] {
      {"rsa.pri.pem", null},
      {"dsa.pri.pem", null},
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
      {"rsa.pub.pem"},
      {"dsa.pub.pem"},
    };
  }

  /**
   * @param  file  Key file to read.
   * @param  alg  Cipher algorithm of key.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"}, dataProvider = "readderprivkeydata")
  public void testReadDerPrivateKey(final String file, final String alg)
    throws Exception
  {
    final File keyFile = new File(KEY_DIR_PATH + file);
    logger.info("Testing read of DER-encoded private key " + keyFile);
    AssertJUnit.assertNotNull(CryptReader.readPrivateKey(keyFile, alg));
  }


  /**
   * @param  file  Key file to read.
   * @param  alg  Cipher algorithm of key.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"}, dataProvider = "readderpubkeydata")
  public void testReadDerPublicKey(final String file, final String alg)
    throws Exception
  {
    final File keyFile = new File(KEY_DIR_PATH + file);
    logger.info("Testing read of DER-encoded public key " + keyFile);
    AssertJUnit.assertNotNull(CryptReader.readPublicKey(keyFile, alg));
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"})
  public void testReadDerCertificate()
    throws Exception
  {
    final File certFile = new File(KEY_DIR_PATH + "rsa.cert.der");
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
  @Test(groups = {"functest", "util"}, dataProvider = "readpemprivkeydata")
  public void testReadPemPrivateKey(final String file, final char[] password)
    throws Exception
  {
    final File keyFile = new File(KEY_DIR_PATH + file);
    logger.info("Testing read of PEM-encoded private key " + keyFile);
    AssertJUnit.assertNotNull(CryptReader.readPemPrivateKey(keyFile, password));
  }


  /**
   * @param  file  Public key file to read.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"}, dataProvider = "readpempubkeydata")
  public void testReadPemPublicKey(final String file)
    throws Exception
  {
    final File keyFile = new File(KEY_DIR_PATH + file);
    logger.info("Testing read of PEM-encoded public key " + keyFile);
    AssertJUnit.assertNotNull(CryptReader.readPemPublicKey(keyFile));
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"})
  public void testReadPemCertificate()
    throws Exception
  {
    final File certFile = new File(KEY_DIR_PATH + "rsa.cert.pem");
    logger.info("Testing read of PEM-encoded X.509 certificate " + certFile);
    AssertJUnit.assertNotNull(CryptReader.readCertificate(certFile));
  }


  /**
   * @param  key  Key to write and read.
   * @param  password  Key encryption password.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"}, dataProvider = "privkeydata")
  public void testReadWriteEncodedPrivateKey(
    final PrivateKey key,
    final char[] password)
    throws Exception
  {
    logger.info("Testing " + key.getAlgorithm() + " private key.");

    final File keyFile = new File(getKeyPath(key, "DER", null));
    keyFile.getParentFile().mkdir();
    CryptWriter.writeEncodedKey(key, keyFile);
    AssertJUnit.assertEquals(
      key,
      CryptReader.readPrivateKey(keyFile, key.getAlgorithm()));
  }


  /**
   * @param  key  Key to write and read.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"}, dataProvider = "pubkeydata")
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
  @Test(groups = {"functest", "util"}, dataProvider = "privkeydata")
  public void testReadWritePemPrivateKey(
    final PrivateKey key,
    final char[] password)
    throws Exception
  {
    logger.info("Testing " + key.getAlgorithm() + " private key.");

    final File keyFile = new File(getKeyPath(key, "PEM", password));
    keyFile.getParentFile().mkdir();
    CryptWriter.writePemKey(key, password, new SecureRandom(), keyFile);

    final PrivateKey keyFromFile = CryptReader.readPemPrivateKey(
      keyFile,
      password);
    AssertJUnit.assertEquals(key, keyFromFile);
  }


  /**
   * @param  key  Key to write and read.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "util"}, dataProvider = "pubkeydata")
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
