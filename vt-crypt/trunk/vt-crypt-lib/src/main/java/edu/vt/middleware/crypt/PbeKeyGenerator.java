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
package edu.vt.middleware.crypt;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Generates secret keys from passwords/phrases in support of password-based
 * encryption (PBE).
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class PbeKeyGenerator
{

  /**
   * Default number of applications of mixing function during key generation.
   */
  private static final int DEFAULT_ITERATIONS = 1024;

  /** Number of bits in byte. */
  private static final int BITS_IN_BYTE = 8;

  /** Symmetric cipher algorithm for which this generator can generate keys. */
  private SymmetricAlgorithm symmetricAlg;

  /** Number of iterations "mixing" function is to be applied. */
  private int iterations = DEFAULT_ITERATIONS;


  /**
   * Creates a key generator that can generate PBE keys for use with the given
   * symmetric cipher algorithm.
   *
   * @param  algorithm  Symmetric cipher algorithm.
   */
  public PbeKeyGenerator(final SymmetricAlgorithm algorithm)
  {
    symmetricAlg = algorithm;
  }


  /**
   * Sets the number of iterations the mixing function is applied during key
   * generations.
   *
   * @param  n  Number of applications of mixing function.
   */
  public void setIterations(final int n)
  {
    iterations = n;
  }


  /**
   * Generate a key from a text password using the PKCS#12 method described at
   * http://www.rsa.com/rsalabs/node.asp?id=2138.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  digest  Digest algorithm to use during key generation.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public SecretKey generatePkcs12(
    final char[] password,
    final int keyBitLength,
    final DigestAlgorithm digest,
    final byte[] salt)
  {
    return
      generate(
        new PKCS12ParametersGenerator(digest.getDigest()),
        PBEParametersGenerator.PKCS12PasswordToBytes(password),
        keyBitLength,
        salt);
  }


  /**
   * Generate a key/IV pair from a text password using the PKCS#12 method
   * described at http://www.rsa.com/rsalabs/node.asp?id=2138.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  ivBitLength  Size of generated IV in bits.
   * @param  digest  Digest algorithm to use during key generation.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public KeyWithIV generatePkcs12(
    final char[] password,
    final int keyBitLength,
    final int ivBitLength,
    final DigestAlgorithm digest,
    final byte[] salt)
  {
    return
      generate(
        new PKCS12ParametersGenerator(digest.getDigest()),
        PBEParametersGenerator.PKCS12PasswordToBytes(password),
        keyBitLength,
        ivBitLength,
        salt);
  }


  /**
   * Generate a key from a text password using the PKCS#5 version 1 method
   * described at http://www.rsa.com/rsalabs/node.asp?id=2127.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  digest  Digest algorithm to use during key generation.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public SecretKey generatePkcs5v1(
    final char[] password,
    final int keyBitLength,
    final DigestAlgorithm digest,
    final byte[] salt)
  {
    final int minSize = keyBitLength / BITS_IN_BYTE;
    if (digest.getDigest().getDigestSize() < minSize) {
      throw new IllegalArgumentException(
        "Digest is too small for chosen key size. " +
        "Use a digest whose size is at least " + minSize + " bytes.");
    }
    return
      generate(
        new PKCS5S1ParametersGenerator(digest.getDigest()),
        PBEParametersGenerator.PKCS5PasswordToBytes(password),
        keyBitLength,
        salt);
  }


  /**
   * Generate a key/IV pair from a text password using the PKCS#5 version 1
   * method described at http://www.rsa.com/rsalabs/node.asp?id=2127.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  ivBitLength  Size of generated IV in bits.
   * @param  digest  Digest algorithm to use during key generation.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public KeyWithIV generatePkcs5v1(
    final char[] password,
    final int keyBitLength,
    final int ivBitLength,
    final DigestAlgorithm digest,
    final byte[] salt)
  {
    final int minSize = (keyBitLength + ivBitLength) / BITS_IN_BYTE;
    if (digest.getDigest().getDigestSize() < minSize) {
      throw new IllegalArgumentException(
        "Digest is too small for chosen key + IV size. " +
        "Use a digest whose size is at least " + minSize + " bytes.");
    }
    return
      generate(
        new PKCS5S1ParametersGenerator(digest.getDigest()),
        PBEParametersGenerator.PKCS5PasswordToBytes(password),
        keyBitLength,
        ivBitLength,
        salt);
  }


  /**
   * Generate a key from a text password using the PKCS#5 version 2.0 method
   * described at http://www.rsa.com/rsalabs/node.asp?id=2127. A SHA-1 digest is
   * used as the calculation function.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public SecretKey generatePkcs5v2(
    final char[] password,
    final int keyBitLength,
    final byte[] salt)
  {
    return
      generate(
        new PKCS5S2ParametersGenerator(),
        PBEParametersGenerator.PKCS5PasswordToBytes(password),
        keyBitLength,
        salt);
  }


  /**
   * Generate a key/IV pair from a text password using the PKCS#5 version 2.0
   * method described at http://www.rsa.com/rsalabs/node.asp?id=2127. A SHA-1
   * digest is used as the calculation function.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  ivBitLength  Size of generated IV in bits.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public KeyWithIV generatePkcs5v2(
    final char[] password,
    final int keyBitLength,
    final int ivBitLength,
    final byte[] salt)
  {
    return
      generate(
        new PKCS5S2ParametersGenerator(),
        PBEParametersGenerator.PKCS5PasswordToBytes(password),
        keyBitLength,
        ivBitLength,
        salt);
  }


  /**
   * Generate a key from a text password using a method based ok PKCS#5 version
   * 2 that is consistent with that performed by the openssl enc operation.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public SecretKey generateOpenssl(
    final char[] password,
    final int keyBitLength,
    final byte[] salt)
  {
    return
      generate(
        new OpenSSLPBEParametersGenerator(),
        PBEParametersGenerator.PKCS5PasswordToBytes(password),
        keyBitLength,
        salt);
  }


  /**
   * Generate a key/IV pair from a text password using a strategy compatible
   * with the OpenSSL enc operation. The strategy is based on PKCS#5 version 2,
   * but uses a MD5 hash instead of SHA1 and an iteration count of 1. For
   * compatibility with OpenSSL, the IV size should be equal to key size.
   *
   * @param  password  Raw material used for key generation.
   * @param  keyBitLength  Size of generated key in bits.
   * @param  ivBitLength  Size of generated IV in bits.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key based on password.
   */
  public KeyWithIV generateOpenssl(
    final char[] password,
    final int keyBitLength,
    final int ivBitLength,
    final byte[] salt)
  {
    return
      generate(
        new OpenSSLPBEParametersGenerator(),
        PBEParametersGenerator.PKCS5PasswordToBytes(password),
        keyBitLength,
        ivBitLength,
        salt);
  }


  /**
   * Generate an encryption key from a password using the given parameter
   * generator.
   *
   * @param  generator  Key generator for specific PBE method.
   * @param  password  Password as byte array (depends on PBE method).
   * @param  keyBitLength  Size of generated key in bits.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key derived from password using PBE key generation method.
   */
  private SecretKey generate(
    final PBEParametersGenerator generator,
    final byte[] password,
    final int keyBitLength,
    final byte[] salt)
  {
    generator.init(password, salt, iterations);

    final KeyParameter keyParam = (KeyParameter)
      generator.generateDerivedParameters(keyBitLength);
    final SecretKeySpec spec = new SecretKeySpec(
      keyParam.getKey(),
      symmetricAlg.getAlgorithm());
    return spec;
  }


  /**
   * Generate an encryption key/IV pair from a password using the given
   * parameter generator.
   *
   * @param  generator  Key generator for specific PBE method.
   * @param  password  Password as byte array (depends on PBE method).
   * @param  keyBitLength  Size of generated key in bits.
   * @param  ivBitLength  Size of generated IV in bits.
   * @param  salt  Key initialization data.
   *
   * @return  Secret key derived from password using PBE key generation method.
   */
  private KeyWithIV generate(
    final PBEParametersGenerator generator,
    final byte[] password,
    final int keyBitLength,
    final int ivBitLength,
    final byte[] salt)
  {
    generator.init(password, salt, iterations);

    final ParametersWithIV params = (ParametersWithIV)
      generator.generateDerivedParameters(keyBitLength, ivBitLength);
    final KeyParameter keyParam = (KeyParameter) params.getParameters();
    return
      new KeyWithIV(
        new SecretKeySpec(keyParam.getKey(), symmetricAlg.getAlgorithm()),
        params.getIV());
  }
}
