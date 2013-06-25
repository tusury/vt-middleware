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

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.crypt.AbstractEncryptionAlgorithm;

/**
 * Provides asymmetric encryption and decryption operations using a
 * public/private key pair.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class AsymmetricAlgorithm extends AbstractEncryptionAlgorithm
{

  /** Mode used for encryption and decryption. */
  public static final String MODE = "NONE";

  /** Padding used for encryption and decryption. */
  public static final String PADDING = "OAEPPadding";

  /** Size of chunks in stream-based encryption. */
  private static final int CHUNK_SIZE = 2048;

  /** Map of digest algorithm names to classes. */
  private static final Map<String, Class<? extends AsymmetricAlgorithm>> NAME_CLASS_MAP =
    new HashMap<String, Class<? extends AsymmetricAlgorithm>>();


  /**
   * Class initializer.
   */
  static {
    NAME_CLASS_MAP.put("RSA", RSA.class);
  }


  /**
   * Creates a new instance that uses a cipher of the given algorithm and the
   * default mode and padding.
   *
   * @param  cipherAlgorithm  Cipher algorithm name.
   */
  protected AsymmetricAlgorithm(final String cipherAlgorithm)
  {
    super(cipherAlgorithm, MODE, PADDING);
  }


  /**
   * Creates a new instance that uses a cipher of the given name.
   *
   * @param  algorithm  Cipher algorithm name.
   *
   * @return  Asymmetric algorithm instance that implements the given cipher
   * algorithm.
   */
  public static AsymmetricAlgorithm newInstance(final String algorithm)
  {
    final Class<? extends AsymmetricAlgorithm> clazz = NAME_CLASS_MAP.get(
      algorithm.toUpperCase());
    if (clazz != null) {
      try {
        return clazz.newInstance();
      } catch (Exception ex) {
        throw new IllegalArgumentException(ex.getMessage());
      }
    } else {
      // Search provider
      return new AsymmetricAlgorithm(algorithm);
    }
  }


  /** {@inheritDoc} */
  @Override
  public Object clone()
    throws CloneNotSupportedException
  {
    final AsymmetricAlgorithm clone = AsymmetricAlgorithm.newInstance(
      getAlgorithm());
    clone.setRandomProvider(randomProvider);
    clone.setKey(key);
    return clone;
  }


  /** {@inheritDoc} */
  protected AlgorithmParameterSpec getAlgorithmParameterSpec()
  {
    return null;
  }


  /** {@inheritDoc} */
  protected int getChunkSize()
  {
    return CHUNK_SIZE;
  }
}
