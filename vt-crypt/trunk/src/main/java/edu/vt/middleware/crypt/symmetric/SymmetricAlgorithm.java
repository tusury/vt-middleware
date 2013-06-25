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
package edu.vt.middleware.crypt.symmetric;

import java.lang.reflect.Constructor;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import edu.vt.middleware.crypt.AbstractEncryptionAlgorithm;

/**
 * Provides symmetric encryption and decryption operations using a secret key.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class SymmetricAlgorithm extends AbstractEncryptionAlgorithm
{

  /** Default mode used for encryption and decryption. */
  public static final String DEFAULT_MODE = "CBC";

  /** Default padding used for encryption and decryption. */
  public static final String DEFAULT_PADDING = "PKCS5Padding";

  /** Multiple of block size (in bytes) common to all supported ciphers. */
  private static final int COMMON_BLOCK_SIZE = 16;

  /** Chunk size used in stream-based encryption/decryption. */
  private static final int CHUNK_SIZE = COMMON_BLOCK_SIZE * 128;

  /** Map of digest algorithm names to classes. */
  private static final Map<String, Class<? extends SymmetricAlgorithm>> NAME_CLASS_MAP =
    new HashMap<String, Class<? extends SymmetricAlgorithm>>();


  /**
   * Class initializer.
   */
  static {
    NAME_CLASS_MAP.put("AES", AES.class);
    NAME_CLASS_MAP.put("BLOWFISH", Blowfish.class);
    NAME_CLASS_MAP.put("CAST5", CAST5.class);
    NAME_CLASS_MAP.put("CAST6", CAST6.class);
    NAME_CLASS_MAP.put("DES", DES.class);
    NAME_CLASS_MAP.put("DESEDE", DESede.class);
    NAME_CLASS_MAP.put("RC2", RC2.class);
    NAME_CLASS_MAP.put("RC4", RC4.class);
    NAME_CLASS_MAP.put("RC5", RC5.class);
    NAME_CLASS_MAP.put("RC6", RC6.class);
    NAME_CLASS_MAP.put("RIJNDAEL", Rijndael.class);
    NAME_CLASS_MAP.put("SERPENT", Serpent.class);
    NAME_CLASS_MAP.put("SKIPJACK", Skipjack.class);
    NAME_CLASS_MAP.put("TWOFISH", Twofish.class);
  }

  /** Initialization vector used for encryption. */
  protected byte[] iv;

  /** Cipher-specific initialization parameters provided to factory methods. */
  protected AlgorithmParameterSpec paramSpec;


  /**
   * Creates a new instance that uses a cipher of the given algorithm.
   *
   * @param  cipherAlgorithm  Cipher algorithm name.
   * @param  cipherModeName  Cipher mode.
   * @param  cipherPadding  Cipher padding method.
   */
  protected SymmetricAlgorithm(
    final String cipherAlgorithm,
    final String cipherModeName,
    final String cipherPadding)
  {
    super(cipherAlgorithm, cipherModeName, cipherPadding);
  }


  /**
   * Creates a new instance that uses a cipher of the given algorithm and the
   * default mode and padding.
   *
   * @param  cipherAlgorithm  Cipher algorithm name.
   *
   * @return  Symmetric algorithm instance that implements the given cipher
   * algorithm.
   */
  public static SymmetricAlgorithm newInstance(final String cipherAlgorithm)
  {
    return newInstance(cipherAlgorithm, DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a new instance from an algorithm specification.
   *
   * @param  spec  Algorithm specification.
   *
   * @return  Symmetric algorithm instance based on the given specification.
   */
  public static SymmetricAlgorithm newInstance(final AlgorithmSpec spec)
  {
    return
      newInstance(
        spec.getName(),
        spec.getMode() != null ? spec.getMode() : DEFAULT_MODE,
        spec.getPadding() == null ? spec.getPadding() : DEFAULT_PADDING);
  }


  /**
   * Creates a new instance that uses a cipher of the given algorithm.
   *
   * @param  cipherAlgorithm  Cipher algorithm name.
   * @param  cipherModeName  Cipher mode.
   * @param  cipherPadding  Cipher padding method.
   *
   * @return  Symmetric algorithm instance that implements the given cipher
   * algorithm.
   */
  public static SymmetricAlgorithm newInstance(
    final String cipherAlgorithm,
    final String cipherModeName,
    final String cipherPadding)
  {
    final Class<? extends SymmetricAlgorithm> clazz = NAME_CLASS_MAP.get(
      cipherAlgorithm.toUpperCase());
    if (clazz != null) {
      try {
        final Constructor<? extends SymmetricAlgorithm> cons =
          clazz.getConstructor(new Class[] {String.class, String.class});
        return cons.newInstance(
          new Object[] {
            cipherModeName,
            cipherPadding,
          });
      } catch (Exception ex) {
        throw new IllegalArgumentException(
          String.format(
            "Invalid cipher %s/%s/%s",
            cipherAlgorithm,
            cipherModeName,
            cipherPadding),
          ex);
      }
    } else {
      // Search provider
      return
        new SymmetricAlgorithm(cipherAlgorithm, cipherModeName, cipherPadding);
    }
  }


  /**
   * Creates a new instance from an algorithm specification and cipher
   * initialization parameters.
   *
   * @param  spec  Algorithm specification.
   * @param  cipherSpec  Cipher-specific initialization parameters.
   *
   * @return  Symmetric algorithm instance that implements the given cipher
   * algorithm.
   */
  public static SymmetricAlgorithm newInstance(
    final AlgorithmSpec spec,
    final AlgorithmParameterSpec cipherSpec)
  {
    final SymmetricAlgorithm cipher = newInstance(spec);
    cipher.paramSpec = cipherSpec;
    return cipher;
  }


  /**
   * Sets the encryption initialization vector. A unique IV should be specified
   * for each encryption operation using the same key for good security. Use the
   * {@link #getRandomIV()} method to obtain random initialization data of the
   * appropriate size for the chosen cipher.
   *
   * <p>IV data is used upon calling either {@link #initEncrypt()} or {@link
   * #initDecrypt()}.</p>
   *
   * @param  ivBytes  Initialization bytes; in many cases the size of data
   * should be equal to the cipher block size.
   */
  public void setIV(final byte[] ivBytes)
  {
    if (ivBytes == null) {
      throw new IllegalArgumentException("IV cannot be null.");
    }
    if (ivBytes.length != getBlockSize()) {
      throw new IllegalArgumentException(
        String.format(
          "IV length (%s) is not equal to block size (%s).",
          ivBytes.length,
          getBlockSize()));
    }
    this.iv = ivBytes;
  }


  /**
   * Determines whether the cipher has been provided with iv data.
   *
   * @return  True if an IV has been provided to the cipher, false otherwise.
   */
  public boolean hasIV()
  {
    return iv != null && iv.length > 0;
  }


  /**
   * Gets a random initialization vector whose size is equal to the underlying
   * cipher's block size.
   *
   * @return  An array of random bytes of a size equal to cipher block size as
   * defined by {@link #getBlockSize()}.
   */
  public byte[] getRandomIV()
  {
    if (cipher == null) {
      throw new IllegalStateException("Cipher not initialized.");
    }
    return getRandomData(getBlockSize());
  }


  /**
   * Gets an array of key lengths that are acceptable for the cipher algorithm.
   * By convention the lengths are returned in descending sort order from
   * longest to shortest. There are some ciphers (e.g. Blowfish) that allow key
   * sizes to be any integral value in a range; in those cases only key sizes
   * that are multiple of the cipher block length are returned. Thus this method
   * always returns lengths that are acceptable, but not necessarily <em>
   * all</em> possible lengths.
   *
   * @return  Array of key lengths in bits.
   */
  public int[] getAllowedKeyLengths()
  {
    throw new UnsupportedOperationException(
      "Available key lengths are not known.");
  }


  /**
   * Gets the minimum key length for this algorithm.
   *
   * @return  Minimum key length in bits.
   */
  public int getMinKeyLength()
  {
    return getAllowedKeyLengths()[getAllowedKeyLengths().length - 1];
  }


  /**
   * Gets the maximum key length for this algorithm.
   *
   * @return  Maximum key length in bits.
   */
  public int getMaxKeyLength()
  {
    return getAllowedKeyLengths()[0];
  }


  /**
   * Determines whether the given key size in bits is valid for this symmetric
   * cipher algorithm.
   *
   * @param  bitLength  Key size in bits.
   *
   * @return  True if given value is a valid key size for this algorithm, false
   * otherwise.
   */
  public boolean isValidKeyLength(final int bitLength)
  {
    final int[] sizes = getAllowedKeyLengths();
    for (int i = 0; i < sizes.length; i++) {
      if (bitLength == sizes[0]) {
        return true;
      }
    }
    return false;
  }


  /** {@inheritDoc} */
  @Override
  public Object clone()
    throws CloneNotSupportedException
  {
    final SymmetricAlgorithm clone = SymmetricAlgorithm.newInstance(
      getAlgorithm(),
      getMode(),
      getPadding());
    clone.setRandomProvider(randomProvider);
    clone.setIV(iv);
    clone.setKey(key);
    return clone;
  }


  /** {@inheritDoc} */
  protected AlgorithmParameterSpec getAlgorithmParameterSpec()
  {
    final AlgorithmParameterSpec spec;
    if (paramSpec != null) {
      spec = paramSpec;
    } else {
      if (iv != null) {
        spec = new IvParameterSpec(this.iv);
      } else {
        if ("CBC".equals(mode)) {
          throw new IllegalStateException("CBC mode requires an IV.");
        }
        return null;
      }
    }
    return spec;
  }


  /** {@inheritDoc} */
  protected int getChunkSize()
  {
    return CHUNK_SIZE;
  }
}
