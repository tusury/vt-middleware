/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.asymmetric;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.CryptProvider;
import edu.vt.middleware.crypt.signature.SignatureAlgorithm;

/**
 * Utility methods for public and private keys used for asymmetric encryption.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public final class PublicKeyUtils
{

  /** Default random data provider for key generation. */
  private static final SecureRandom DEFAULT_RANDOM = new SecureRandom();

  /** Data used to verify keypairs. */
  private static final byte[] SIGN_BYTES = "Mr. Watson--come here--I want to see you."
    .getBytes();


  /** Private constructor of utility class. */
  private PublicKeyUtils() {}


  /**
   * Gets the length in bits of a public key where key size is dependent on the
   * particulars of the algorithm.
   *
   * <ul>
   *   <li>DSA - length of p</li>
   *   <li>EC - length of p for prime fields, m for binary fields</li>
   *   <li>RSA - length of modulus</li>
   * </ul>
   *
   * @param  pubKey  Public key.
   *
   * @return  Size of the key in bits.
   */
  public static int length(final PublicKey pubKey)
  {
    final int size;
    if (pubKey instanceof DSAPublicKey) {
      size = ((DSAPublicKey) pubKey).getParams().getP().bitLength();
    } else if (pubKey instanceof RSAPublicKey) {
      size = ((RSAPublicKey) pubKey).getModulus().bitLength();
    } else if (pubKey instanceof ECPublicKey) {
      size = ((ECPublicKey) pubKey).getParams().getCurve().getField()
        .getFieldSize();
    } else {
      throw new IllegalArgumentException(pubKey + " not supported.");
    }
    return size;
  }


  /**
   * Gets the length in bits of a private key where key size is dependent on the
   * particulars of the algorithm.
   *
   * <ul>
   *   <li>DSA - length of q in bits</li>
   *   <li>EC - length of p for prime fields, m for binary fields</li>
   *   <li>RSA - modulus length in bits</li>
   * </ul>
   *
   * @param  privKey  Private key.
   *
   * @return  Size of the key in bits.
   */
  public static int length(final PrivateKey privKey)
  {
    final int size;
    if (privKey instanceof DSAPrivateKey) {
      size = ((DSAPrivateKey) privKey).getParams().getQ().bitLength();
    } else if (privKey instanceof RSAPrivateKey) {
      size = ((RSAPrivateKey) privKey).getModulus().bitLength();
    } else if (privKey instanceof ECPrivateKey) {
      size = ((ECPrivateKey) privKey).getParams().getCurve().getField()
        .getFieldSize();
    } else {
      throw new IllegalArgumentException(privKey + " not supported.");
    }
    return size;
  }


  /**
   * Generates a key pair of the given length with default algorithm parameters.
   *
   * @param  algorithm  Name of a cipher algorithm for which a suitable key pair
   * will be generated.
   * @param  bitLength  Size of each key in pair in bits.
   *
   * @return  Key pair that may be used for encryption/decryption on a cipher of
   * the given algorithm.
   *
   * @throws  CryptException  On key pair generation errors.
   */
  public static KeyPair generate(final String algorithm, final int bitLength)
    throws CryptException
  {
    return generate(algorithm, bitLength, DEFAULT_RANDOM);
  }


  /**
   * Generates a key pair of the given length with default algorithm parameters.
   *
   * @param  algorithm  Name of a cipher algorithm for which a suitable key pair
   * will be generated.
   * @param  bitLength  Size of each key in pair in bits.
   * @param  random  Source of randomness used for key generation.
   *
   * @return  Key pair that may be used for encryption/decryption on a cipher of
   * the given algorithm.
   *
   * @throws  CryptException  On key pair generation errors.
   */
  public static KeyPair generate(
    final String algorithm,
    final int bitLength,
    final SecureRandom random)
    throws CryptException
  {
    if (random == null) {
      throw new CryptException("Source of random data cannot be null.");
    }

    final KeyPairGenerator generator = CryptProvider.getKeyPairGenerator(
      algorithm);
    try {
      generator.initialize(bitLength, random);
      return generator.generateKeyPair();
    } catch (Exception ex) {
      throw new CryptException(
        String.format("Error generatig %s-bit %s key", bitLength, algorithm),
        ex);
    }
  }


  /**
   * Generates a key pair with precise control over algorithm parameters.
   *
   * @param  algorithm  Name of a cipher algorithm for which a suitable key pair
   * will be generated.
   * @param  params  Algorithm-specific domain parameters.
   *
   * @return  Key pair that may be used for encryption/decryption on a cipher of
   * the given algorithm.
   *
   * @throws  CryptException  On key pair generation errors.
   */
  public static KeyPair generate(
    final String algorithm,
    final AlgorithmParameterSpec params)
    throws CryptException
  {
    return generate(algorithm, params, DEFAULT_RANDOM);
  }


  /**
   * Generates a key pair with precise control over algorithm parameters.
   *
   * @param  algorithm  Name of a cipher algorithm for which a suitable key pair
   * will be generated.
   * @param  params  Algorithm-specific domain parameters.
   * @param  random  Source of randomness used for key generation.
   *
   * @return  Key pair that may be used for encryption/decryption on a cipher of
   * the given algorithm.
   *
   * @throws  CryptException  On key pair generation errors.
   */
  public static KeyPair generate(
    final String algorithm,
    final AlgorithmParameterSpec params,
    final SecureRandom random)
    throws CryptException
  {
    if (random == null) {
      throw new CryptException("Source of random data cannot be null.");
    }

    final KeyPairGenerator generator = CryptProvider.getKeyPairGenerator(
      algorithm);
    try {
      generator.initialize(params, random);
      return generator.generateKeyPair();
    } catch (Exception ex) {
      throw new CryptException(
        String.format("Error generatig %s key with %s", algorithm, params),
        ex);
    }
  }


  /**
   * Determines whether the given public and private keys form a proper key pair
   * by computing and verifying a digital signature with the keys.
   *
   * @param  pubKey  Public key.
   * @param  privKey  Private key.
   *
   * @return  True if the keys form a compatible assymetric keypair, false
   * otherwise. Errors during signature verification are treated as false.
   */
  public static boolean isKeyPair(
    final PublicKey pubKey,
    final PrivateKey privKey)
  {
    final String alg = pubKey.getAlgorithm();
    if (!alg.equals(privKey.getAlgorithm())) {
      return false;
    }

    final SignatureAlgorithm signer;
    if ("DSA".equals(alg) || "RSA".equals(alg)) {
      signer = SignatureAlgorithm.newInstance(alg);
    } else if ("EC".equals(alg)) {
      signer = SignatureAlgorithm.newInstance("ECDSA");
    } else {
      throw new IllegalArgumentException(alg + " not supported.");
    }

    boolean match;
    try {
      signer.setSignKey(privKey);
      signer.setVerifyKey(pubKey);
      signer.initSign();

      final byte[] sig = signer.sign(SIGN_BYTES);
      signer.initVerify();
      match = signer.verify(SIGN_BYTES, sig);
    } catch (Exception e) {
      match = false;
    }
    return match;
  }
}
