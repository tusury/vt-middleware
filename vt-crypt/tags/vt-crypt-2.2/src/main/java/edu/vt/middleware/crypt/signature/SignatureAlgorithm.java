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
package edu.vt.middleware.crypt.signature;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.crypt.AbstractAlgorithm;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.util.Converter;

/**
 * <code>SignatureAlgorithm</code> provides message signing and verification
 * operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class SignatureAlgorithm extends AbstractAlgorithm
{

  /** Map of signature algorithm names to classes. */
  private static final Map<String, Class<? extends SignatureAlgorithm>> NAME_CLASS_MAP =
    new HashMap<String, Class<? extends SignatureAlgorithm>>();


  /**
   * Class initializer.
   */
  static {
    NAME_CLASS_MAP.put("DSA", DSASignature.class);
    NAME_CLASS_MAP.put("ECDSA", ECDSASignature.class);
    NAME_CLASS_MAP.put("RSA", RSASignature.class);
  }

  /** Private key used for signing. */
  protected PrivateKey signKey;

  /** Public key used for verification. */
  protected PublicKey verifyKey;

  /** Message digest used to compute encoded message for signing. */
  protected DigestAlgorithm digest;


  /**
   * Creates a new instance of a the given signature algorithm.
   *
   * @param  alg  Signature algorithm name, e.g. DSA, RSA.
   */
  protected SignatureAlgorithm(final String alg)
  {
    this.algorithm = alg;
  }


  /**
   * Creates a new instance that uses the given signature algorithm to compute
   * and verify signatures.
   *
   * @param  algorithm  Signature algorithm name.
   *
   * @return  New instance of signature algorithm.
   */
  public static SignatureAlgorithm newInstance(final String algorithm)
  {
    final Class<? extends SignatureAlgorithm> clazz = NAME_CLASS_MAP.get(
      algorithm.toUpperCase());
    if (clazz == null) {
      throw new IllegalArgumentException(
        "Signature " + algorithm + " is not available.");
    }
    try {
      return clazz.newInstance();
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }


  /**
   * Creates a new instance that uses the given signature algorithm and digest
   * algorithm to compute and verify signatures.
   *
   * @param  algorithm  Signature algorithm name.
   * @param  digestAlgorithm  Message digest algorithm name.
   *
   * @return  New instance of signature algorithm.
   */
  public static SignatureAlgorithm newInstance(
    final String algorithm,
    final String digestAlgorithm)
  {
    final Class<? extends SignatureAlgorithm> clazz = NAME_CLASS_MAP.get(
      algorithm.toUpperCase());
    if (clazz == null) {
      throw new IllegalArgumentException(
        "Signature " + algorithm + " is not available.");
    }
    try {
      final Constructor<? extends SignatureAlgorithm> cons =
        clazz.getConstructor(new Class[] {DigestAlgorithm.class});
      return
        cons.newInstance(
          new Object[] {DigestAlgorithm.newInstance(digestAlgorithm)});
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }


  /**
   * Sets the private key used for signing.
   *
   * @param  key  Private key.
   */
  public void setSignKey(final PrivateKey key)
  {
    this.signKey = key;
  }


  /**
   * Sets the public key used for verification.
   *
   * @param  key  Public key.
   */
  public void setVerifyKey(final PublicKey key)
  {
    this.verifyKey = key;
  }


  /**
   * Initialize the signature for the {@link #sign(byte[])} operation.
   *
   * @throws  CryptException  On signature initialization failure.
   */
  public void initSign()
    throws CryptException
  {
    throw new UnsupportedOperationException("Not implemented.");
  }


  /**
   * Initialize the signature for the {@link #verify(byte[], byte[])} operation.
   *
   * @throws  CryptException  On verification initialization failure.
   */
  public void initVerify()
    throws CryptException
  {
    throw new UnsupportedOperationException("Not implemented.");
  }


  /**
   * Signs the given data and returns the signature as a byte array.
   *
   * @param  data  Data to be signed.
   *
   * @return  Signature of given data as byte array.
   *
   * @throws  CryptException  On signature failure.
   */
  public byte[] sign(final byte[] data)
    throws CryptException
  {
    throw new UnsupportedOperationException("Not implemented.");
  }


  /**
   * Signs the given data and returns the signature as a string using the
   * conversion strategy provided by the given converter.
   *
   * @param  data  Data to be signed.
   * @param  converter  Converts raw signature bytes to a string.
   *
   * @return  Signature of given data as a string.
   *
   * @throws  CryptException  On signature failure.
   */
  public String sign(final byte[] data, final Converter converter)
    throws CryptException
  {
    return converter.fromBytes(sign(data));
  }


  /**
   * Computes the signature of the data in the given input stream by processing
   * in chunks.
   *
   * @param  in  Input stream containing data to be signed.
   *
   * @return  Signature of given data as byte array.
   *
   * @throws  CryptException  On signature failure.
   * @throws  IOException  On input stream read errors.
   */
  public byte[] sign(final InputStream in)
    throws CryptException, IOException
  {
    throw new UnsupportedOperationException("Not implemented.");
  }


  /**
   * Computes the signature of the data in the given input stream by processing
   * in chunks.
   *
   * @param  in  Input stream containing data to be signed.
   * @param  converter  Converts raw signature bytes to a string.
   *
   * @return  Signature of given data as a string.
   *
   * @throws  CryptException  On signature failure.
   * @throws  IOException  On input stream read errors.
   */
  public String sign(final InputStream in, final Converter converter)
    throws CryptException, IOException
  {
    return converter.fromBytes(sign(in));
  }


  /**
   * Verifies the signature of the given data matches the given signature.
   *
   * @param  data  Data to be verified.
   * @param  signature  Signature to be used for comparison.
   *
   * @return  True if the signed data matches the given signature, false
   * otherwise.
   *
   * @throws  CryptException  On verification failure.
   */
  public boolean verify(final byte[] data, final byte[] signature)
    throws CryptException
  {
    throw new UnsupportedOperationException("Not implemented.");
  }


  /**
   * Verifies the signature of the given data matches the given signature.
   *
   * @param  data  Data to be verified.
   * @param  signature  String representation of signature to be used for
   * comparison.
   * @param  converter  Converts the signature string representation into raw
   * bytes required for verification.
   *
   * @return  True if the signed data matches the given signature, false
   * otherwise.
   *
   * @throws  CryptException  On verification failure.
   */
  public boolean verify(
    final byte[] data,
    final String signature,
    final Converter converter)
    throws CryptException
  {
    return verify(data, converter.toBytes(signature));
  }


  /**
   * Verifies the signature of the given data matches the given signature.
   *
   * @param  in  Input stream containing data to be verified.
   * @param  signature  Signature to be used for comparison.
   *
   * @return  True if the signed data matches the given signature, false
   * otherwise.
   *
   * @throws  CryptException  On verification failure.
   * @throws  IOException  On input stream read errors.
   */
  public boolean verify(final InputStream in, final byte[] signature)
    throws CryptException, IOException
  {
    throw new UnsupportedOperationException("Not implemented.");
  }


  /**
   * Verifies the signature of the data in the given input stream matches the
   * given signature.
   *
   * @param  in  Input stream containing data to be verified.
   * @param  signature  String representation of signature to be used for
   * comparison.
   * @param  converter  Converts the signature string representation into raw
   * bytes required for verification.
   *
   * @return  True if the signed data matches the given signature, false
   * otherwise.
   *
   * @throws  CryptException  On verification failure.
   * @throws  IOException  On input stream read errors.
   */
  public boolean verify(
    final InputStream in,
    final String signature,
    final Converter converter)
    throws CryptException, IOException
  {
    return verify(in, converter.toBytes(signature));
  }


  /** {@inheritDoc} */
  @Override
  public Object clone()
    throws CloneNotSupportedException
  {
    final SignatureAlgorithm clone = SignatureAlgorithm.newInstance(
      getAlgorithm(),
      digest.getAlgorithm());
    clone.setRandomProvider(randomProvider);
    clone.setSignKey(signKey);
    clone.setVerifyKey(verifyKey);
    return clone;
  }
}
