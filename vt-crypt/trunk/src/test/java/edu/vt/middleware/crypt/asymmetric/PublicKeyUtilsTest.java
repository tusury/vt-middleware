/*
  $Id$

  Copyright (C) 2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.asymmetric;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link PublicKeyUtils} class.
 *
 * @author Middleware Services
 * @version $Revision$
 */
public class PublicKeyUtilsTest
{
  /**
   * @return  Set of keypairs and their associated lengths.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "keypairs")
  public Object[][] createKeyPairs() throws Exception
  {
    return new Object[][] {
      new Object[] {PublicKeyUtils.generate("RSA", 1152), 1152, 1152},
      new Object[] {PublicKeyUtils.generate("DSA", 1024), 1024, 160},
      new Object[] {PublicKeyUtils.generate("EC", 384), 384, 384},
    };
  }


  /**
   * @return  Set of public and private keys that may or may not be valid key
   * pairs.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "keys")
  public Object[][] createKeys() throws Exception
  {
    final KeyPair dsaKeys = PublicKeyUtils.generate("DSA", 1024);
    final KeyPair ecKeys = PublicKeyUtils.generate("EC", 256);
    final KeyPair rsaKeys = PublicKeyUtils.generate("RSA", 1024);
    return new Object[][] {
      new Object[] {dsaKeys.getPublic(), dsaKeys.getPrivate(), true},
      new Object[] {ecKeys.getPublic(), ecKeys.getPrivate(), true},
      new Object[] {rsaKeys.getPublic(), rsaKeys.getPrivate(), true},
      new Object[] {dsaKeys.getPublic(), rsaKeys.getPrivate(), false},
    };
  }


  /**
   * Unit test for {@link PublicKeyUtils#length(java.security.PrivateKey)} and
   * {@link PublicKeyUtils#length(java.security.PublicKey)}.
   *
   * @param  keys  Valid key pair.
   * @param  expectedPubLen  Expected public key length.
   * @param  expectedPrivLen  Expected private key length.
   *
   * @throws  Exception  On errors.
   */
  @Test(
      groups = {"functest", "asymmetric"},
      dataProvider = "keypairs"
  )
  public void testLength(
      final KeyPair keys,
      final int expectedPubLen,
      final int expectedPrivLen)
    throws Exception
  {
    AssertJUnit.assertEquals(
        expectedPubLen,
        PublicKeyUtils.length(keys.getPublic()));
    AssertJUnit.assertEquals(
        expectedPrivLen,
        PublicKeyUtils.length(keys.getPrivate()));
  }


  /**
   * Unit test for {@link PublicKeyUtils#isKeyPair(java.security.PublicKey,
   * java.security.PrivateKey)}.
   *
   * @param  pubKey  Public key.
   * @param  privKey  Private key.
   * @param  expected  Expected result of keypair test.
   *
   * @throws  Exception  On errors.
   */
  @Test(
      groups = {"functest", "asymmetric"},
      dataProvider = "keys"
  )
  public void testIsKeyPair(
      final PublicKey pubKey,
      final PrivateKey privKey,
      final boolean expected)
    throws Exception
  {
    AssertJUnit.assertEquals(
        expected,
        PublicKeyUtils.isKeyPair(pubKey, privKey));
  }
}
