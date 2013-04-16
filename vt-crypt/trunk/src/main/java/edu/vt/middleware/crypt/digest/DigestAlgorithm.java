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
package edu.vt.middleware.crypt.digest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.crypt.AbstractRandomizableAlgorithm;
import edu.vt.middleware.crypt.util.Converter;
import org.bouncycastle.crypto.Digest;

/**
 * <code>DigestAlgorithm</code> provides message digest operations.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */

public class DigestAlgorithm extends AbstractRandomizableAlgorithm implements Cloneable
{

  /** Chunk size used in stream-based digestion. */
  public static final int CHUNK_SIZE = 4096;

  /** Map of digest algorithm names to classes. */
  private static final Map<String, Class<? extends DigestAlgorithm>>
  NAME_CLASS_MAP = new HashMap<String, Class<? extends DigestAlgorithm>>();


  /**
   * Class initializer.
   */
  static {
    NAME_CLASS_MAP.put("MD2", MD2.class);
    NAME_CLASS_MAP.put("MD4", MD4.class);
    NAME_CLASS_MAP.put("MD5", MD5.class);
    NAME_CLASS_MAP.put("RIPEMD128", RipeMD128.class);
    NAME_CLASS_MAP.put("RIPEMD160", RipeMD160.class);
    NAME_CLASS_MAP.put("RIPEMD256", RipeMD256.class);
    NAME_CLASS_MAP.put("RIPEMD320", RipeMD320.class);
    NAME_CLASS_MAP.put("SHA1", SHA1.class);
    NAME_CLASS_MAP.put("SHA-1", SHA1.class);
    NAME_CLASS_MAP.put("SHA256", SHA256.class);
    NAME_CLASS_MAP.put("SHA-256", SHA256.class);
    NAME_CLASS_MAP.put("SHA384", SHA384.class);
    NAME_CLASS_MAP.put("SHA-384", SHA384.class);
    NAME_CLASS_MAP.put("SHA512", SHA512.class);
    NAME_CLASS_MAP.put("SHA-512", SHA512.class);
    NAME_CLASS_MAP.put("TIGER", Tiger.class);
    NAME_CLASS_MAP.put("WHIRLPOOL", Whirlpool.class);
  }

  /** Digest instance used for digest computation. */
  protected Digest digest;

  /** Random data used to initialize digest. */
  protected byte[] salt;


  /**
   * Creates a new digest algorithm that uses the given {@link Digest} for
   * digest computation.
   *
   * @param  d  Used for digest computation.
   */
  protected DigestAlgorithm(final Digest d)
  {
    setDigest(d);
  }


  /**
   * Creates a new digest algorithm instance from its name, e.g. SHA-1 for a
   * SHA1 digest.
   *
   * @param  algorithm  Name of a message digest algorithm.
   *
   * @return  Digest algorithm instance that provides the requested algorithm.
   */
  public static DigestAlgorithm newInstance(final String algorithm)
  {
    final Class<? extends DigestAlgorithm> clazz = NAME_CLASS_MAP.get(
      algorithm.toUpperCase());
    if (clazz == null) {
      throw new IllegalArgumentException(
        "Digest " + algorithm + " is not available.");
    }
    try {
      return clazz.newInstance();
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }


  /**
   * Sets the internal object responsible for digest computation.
   *
   * @param  d  Used for digest computation.
   */
  protected void setDigest(final Digest d)
  {
    this.digest = d;
    this.algorithm = d.getAlgorithmName();
  }


  /**
   * Sets the salt used to randomize the digest prior to digestion.
   *
   * @param  randomBytes  Random data to initialize digest.
   */
  public void setSalt(final byte[] randomBytes)
  {
    this.salt = randomBytes;
  }


  /**
   * Gets a random salt in the amount specified by {@link #getRandomByteSize()}.
   *
   * @return  Random salt.
   */
  public byte[] getRandomSalt()
  {
    return getRandomData(getRandomByteSize());
  }


  /**
   * Gets the underlying object that performs digest computation.
   *
   * @return  Digest instance.
   */
  public Digest getDigest()
  {
    return digest;
  }


  /**
   * Computes the digest for the given data in a single operation.
   *
   * @param  input  Data to be digested.
   *
   * @return  Message digest as byte array.
   */
  public byte[] digest(final byte[] input)
  {
    if (salt != null) {
      digest.update(salt, 0, salt.length);
    }
    digest.update(input, 0, input.length);

    final byte[] hash = new byte[digest.getDigestSize()];
    digest.doFinal(hash, 0);
    return hash;
  }


  /**
   * Computes the digest for the given data in a single operation and passes the
   * resulting digest bytes through the given converter to produce text output.
   *
   * @param  input  Data to be digested.
   * @param  converter  Converts digest bytes to some string representation, e.g
   * Base-64 encoded text.
   *
   * @return  String representation of digest as provided by the converter.
   */
  public String digest(final byte[] input, final Converter converter)
  {
    return converter.fromBytes(digest(input));
  }


  /**
   * Computes the digest for all the data in the stream by reading data and
   * hashing it in chunks.
   *
   * @param  in  Input stream containing data to be digested.
   *
   * @return  Message digest as byte array.
   *
   * @throws  IOException  On input stream read errors.
   */
  public byte[] digest(final InputStream in)
    throws IOException
  {
    if (in == null) {
      throw new IllegalArgumentException("Input stream cannot be null.");
    }

    final byte[] buffer = new byte[CHUNK_SIZE];
    int count;
    if (salt != null) {
      digest.update(salt, 0, salt.length);
    }
    while ((count = in.read(buffer, 0, CHUNK_SIZE)) > 0) {
      digest.update(buffer, 0, count);
    }

    final byte[] hash = new byte[digest.getDigestSize()];
    digest.doFinal(hash, 0);
    return hash;
  }


  /**
   * Computes the digest for all the data in the stream by reading data and
   * hashing it in chunks. The output is converted to a string representation by
   * the given converter.
   *
   * @param  in  Input stream containing data to be digested.
   * @param  converter  Converts digest bytes to some string representation, e.g
   * Base-64 encoded text.
   *
   * @return  String representation of digest as provided by the converter.
   *
   * @throws  IOException  On input stream read errors.
   */
  public String digest(final InputStream in, final Converter converter)
    throws IOException
  {
    return converter.fromBytes(digest(in));
  }


  /** {@inheritDoc} */
  @Override
  public Object clone() throws CloneNotSupportedException
  {
    final DigestAlgorithm clone = DigestAlgorithm.newInstance(getAlgorithm());
    clone.setSalt(salt);
    clone.setRandomByteSize(randomByteSize);
    clone.setRandomProvider(randomProvider);
    return clone;
  }
}
