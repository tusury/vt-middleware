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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import edu.vt.middleware.crypt.util.Converter;

/**
 * Base class for symmetric and asymmetric encryption algorithms. This class is
 * essentially a wrapper for the {@link Cipher} class.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public abstract class AbstractEncryptionAlgorithm extends AbstractAlgorithm
  implements EncryptionAlgorithm
{

  /** Encryption/decription cipher. */
  protected Cipher cipher;

  /** Mode used for encryption and decryption. */
  protected String mode;

  /** Padding used for encryption and decryption. */
  protected String padding;

  /** Either {@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE}. */
  protected int cipherMode;

  /** Key used for encryption or decryption. */
  protected Key key;


  /**
   * Creates a new encryption algorithm that uses a cipher of the given name.
   *
   * @param  cipherAlgorithm  Cipher algorithm name.
   * @param  cipherModeName  Cipher mode.
   * @param  cipherPadding  Cipher padding method.
   */
  protected AbstractEncryptionAlgorithm(
    final String cipherAlgorithm,
    final String cipherModeName,
    final String cipherPadding)
  {
    this.algorithm = cipherAlgorithm;
    this.mode = cipherModeName;
    this.padding = cipherPadding;
    try {
      initCipher();
    } catch (CryptException e) {
      throw new RuntimeException(
        "Error initializing cipher with name " + algorithm,
        e);
    }
  }


  /** {@inheritDoc} */
  public String getMode()
  {
    return mode;
  }


  /** {@inheritDoc} */
  public String getPadding()
  {
    return padding;
  }


  /** {@inheritDoc} */
  public void setKey(final Key k)
  {
    this.key = k;
  }


  /** {@inheritDoc} */
  public int getCipherMode()
  {
    return cipherMode;
  }


  /** {@inheritDoc} */
  public int getBlockSize()
  {
    return cipher.getBlockSize();
  }


  /** {@inheritDoc} */
  public void initEncrypt()
    throws CryptException
  {
    init(Cipher.ENCRYPT_MODE);
  }


  /** {@inheritDoc} */
  public void initDecrypt()
    throws CryptException
  {
    init(Cipher.DECRYPT_MODE);
  }


  /** {@inheritDoc} */
  public byte[] encrypt(final byte[] plaintext)
    throws CryptException
  {
    if (cipherMode != Cipher.ENCRYPT_MODE) {
      throw new CryptException("Cipher is not in encryption mode.");
    }
    return crypt(plaintext);
  }


  /** {@inheritDoc} */
  public String encrypt(final byte[] plaintext, final Converter converter)
    throws CryptException
  {
    return converter.fromBytes(encrypt(plaintext));
  }


  /** {@inheritDoc} */
  public void encrypt(final InputStream in, final OutputStream out)
    throws CryptException, IOException
  {
    if (cipherMode != Cipher.ENCRYPT_MODE) {
      throw new CryptException("Cipher is not in encryption mode.");
    }
    crypt(in, out);
  }


  /** {@inheritDoc} */
  public byte[] decrypt(final byte[] ciphertext)
    throws CryptException
  {
    if (cipherMode != Cipher.DECRYPT_MODE) {
      throw new CryptException("Cipher is not in decryption mode.");
    }
    return crypt(ciphertext);
  }


  /** {@inheritDoc} */
  public byte[] decrypt(final String ciphertext, final Converter converter)
    throws CryptException
  {
    return decrypt(converter.toBytes(ciphertext));
  }


  /** {@inheritDoc} */
  public void decrypt(final InputStream in, final OutputStream out)
    throws CryptException, IOException
  {
    if (cipherMode != Cipher.DECRYPT_MODE) {
      throw new CryptException("Cipher is not in decryption mode.");
    }
    crypt(in, out);
  }


  /** {@inheritDoc} */
  public String toString()
  {
    final StringBuffer sb = new StringBuffer(50);
    sb.append(algorithm);
    sb.append('/');
    sb.append(mode);
    sb.append('/');
    sb.append(padding);
    return sb.toString();
  }


  /**
   * Initializes the underlying {@link Cipher} object using {@link #algorithm},
   * {@link #mode}, and {@link #padding}.
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or if the provider is not available in the environment.
   */
  protected void initCipher()
    throws CryptException
  {
    cipher = CryptProvider.getCipher(algorithm, mode, padding);
    if (randomProvider == null) {
      randomProvider = new SecureRandom();
    }
  }


  /**
   * Initializes {@link #cipher} for either encryption or decryption.
   *
   * @param  encryptOrDecrypt  Either <a
   * href="http://java.sun.com/j2se/1.5.0/docs/api/
   * javax/crypto/Cipher.html#ENCRYPT_MODE">Cipher.ENCRYPT_MODE</a> or <a
   * href="http://java.sun.com/j2se/1.5.0/docs/api/
   * javax/crypto/Cipher.html#DECRYPT_MODE">Cipher.DECRYPT_MODE</a>.
   *
   * @throws  CryptException  On cryptographic configuration errors.
   */
  protected void init(final int encryptOrDecrypt)
    throws CryptException
  {
    if (cipher == null) {
      throw new CryptException("Cipher not initialized.");
    }
    cipherMode = encryptOrDecrypt;
    try {
      final AlgorithmParameterSpec algSpec = getAlgorithmParameterSpec();
      if (algSpec != null) {
        cipher.init(encryptOrDecrypt, key, algSpec, randomProvider);
      } else {
        cipher.init(encryptOrDecrypt, key, randomProvider);
      }
    } catch (InvalidKeyException e) {
      throw new CryptException("Invalid key for " + this, e);
    } catch (InvalidAlgorithmParameterException e) {
      throw new CryptException("Invalid cipher parameters.", e);
    }
  }


  /**
   * Based on current cipher mode, encrypts or decrypts the given input data.
   *
   * @param  in  Cipher input data.
   *
   * @return  Cipher output data.
   *
   * @throws  CryptException  On encryption errors.
   */
  protected byte[] crypt(final byte[] in)
    throws CryptException
  {
    try {
      return cipher.doFinal(in);
    } catch (IllegalBlockSizeException e) {
      throw new CryptException("Bad block size.", e);
    } catch (BadPaddingException e) {
      throw new CryptException("Bad padding.", e);
    }
  }


  /**
   * Based on current cipher mode, encrypts or decrypts the data in the given
   * input stream into resulting data in the output stream.
   *
   * @param  in  Input stream.
   * @param  out  Output stream.
   *
   * @throws  CryptException  On encryption errors.
   * @throws  IOException  On stream read/write errors.
   */
  protected void crypt(final InputStream in, final OutputStream out)
    throws CryptException, IOException
  {
    if (in == null) {
      throw new IllegalArgumentException("Input stream cannot be null.");
    }
    if (out == null) {
      throw new IllegalArgumentException("Output stream cannot be null.");
    }

    final byte[] inBuffer = new byte[getChunkSize()];
    final byte[] outBuffer = new byte[getChunkSize() * 2];
    int inCount = 0;
    int outCount = 0;
    try {
      while ((inCount = in.read(inBuffer)) > 0) {
        outCount = cipher.update(inBuffer, 0, inCount, outBuffer);
        out.write(outBuffer, 0, outCount);
      }

      final byte[] end = cipher.doFinal();
      out.write(end);
    } catch (BadPaddingException e) {
      throw new CryptException("Bad padding.", e);
    } catch (IllegalBlockSizeException e) {
      throw new CryptException("Bad block size.", e);
    } catch (ShortBufferException e) {
      throw new CryptException("Output buffer is too small.", e);
    }
  }


  /**
   * Gets the algorithm parameter specification for this algorithm.
   *
   * @return  Algorithm parameter specification specific to this algorithm.
   */
  protected abstract AlgorithmParameterSpec getAlgorithmParameterSpec();


  /**
   * Gets the chunk size for buffers using in stream-based encryption and
   * decryption operations.
   *
   * @return  Stream chunk size.
   */
  protected abstract int getChunkSize();
}
