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
package edu.vt.middleware.crypt;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Signature;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p><code>CryptProvider</code> contains methods for finding cryptographic
 * objects using a set of providers.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public final class CryptProvider
{

  /** Default size of random byte array. */
  public static final int RANDOM_BYTE_ARRAY_SIZE = 256;

  /** List of providers to use. */
  private static String[] providers = new String[0];

  /**
   * Dynamically register the Bouncy Castle provider.
   */
  static {
    // Bouncy Castle provider
    addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider(), "BC");
  }


  /** <p>Default constructor.</p> */
  private CryptProvider() {}


  /**
   * <p>This will add an additional security provider.</p>
   *
   * @param  provider  <code>Provider</code>
   * @param  name  <code>String</code>
   */
  public static void addProvider(final Provider provider, final String name)
  {
    java.security.Security.addProvider(provider);

    final String[] tmp = new String[providers.length + 1];
    for (int i = 0; i < providers.length; i++) {
      tmp[i] = providers[i];
    }
    tmp[providers.length] = name;
    providers = tmp;

    getLogger().debug("Added new security provider {}", name);
  }


  /**
   * <p>This finds a <code>Cipher</code> using the known providers and the
   * supplied parameters.</p>
   *
   * @param  algorithm  <code>String</code> name
   * @param  mode  <code>String</code> name
   * @param  padding  <code>String</code> name
   *
   * @return  <code>Cipher</code>
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or if the provider is not available in the environment
   */
  public static Cipher getCipher(
    final String algorithm,
    final String mode,
    final String padding)
    throws CryptException
  {

    Cipher cipher = null;
    String transformation = null;
    if (mode != null && padding != null) {
      transformation = algorithm + "/" + mode + "/" + padding;
    } else if (mode != null) {
      transformation = algorithm + "/" + mode;
    } else {
      transformation = algorithm;
    }
    for (int i = 0; i < providers.length; i++) {
      try {
        cipher = Cipher.getInstance(transformation, providers[i]);
      } catch (NoSuchPaddingException e) {
        getLogger().debug(
            "{} does not support padding {}", providers[i], padding);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], algorithm, e);
      } finally {
        if (cipher != null) {
          break;
        }
      }
    }
    if (cipher == null) {
      try {
        cipher = Cipher.getInstance(transformation);
      } catch (NoSuchPaddingException e) {
        getLogger().debug(
            "Default provider does not support padding {}", padding);
        throw new CryptException(e.getMessage());
      } catch (GeneralSecurityException e) {
        handleProviderError(null, algorithm, e);
        throw new CryptException(e.getMessage());
      }
    }
    return cipher;
  }


  /**
   * <p>This finds a <code>SecretKeyFactory</code> using the known providers and
   * the supplied algorithm parameter.</p>
   *
   * @param  algorithm  <code>String</code> name
   *
   * @return  <code>SecretKeyFactory</code>
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or if the provider is not available in the environment
   */
  public static SecretKeyFactory getSecretKeyFactory(final String algorithm)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    SecretKeyFactory kf = null;
    for (int i = 0; i < providers.length; i++) {
      try {
        kf = SecretKeyFactory.getInstance(algorithm, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], algorithm, e);
      } finally {
        if (kf != null) {
          break;
        }
      }
    }
    if (kf == null) {
      try {
        kf = SecretKeyFactory.getInstance(algorithm);
      } catch (GeneralSecurityException e) {
        handleProviderError(null, algorithm, e);
        throw new CryptException(e.getMessage());
      }
    }
    return kf;
  }


  /**
   * <p>This finds a <code>KeyFactory</code> using the known providers and the
   * supplied algorithm parameter.</p>
   *
   * @param  algorithm  <code>String</code> name
   *
   * @return  <code>KeyFactory</code>
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or if the provider is not available in the environment
   */
  public static KeyFactory getKeyFactory(final String algorithm)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    KeyFactory kf = null;
    for (int i = 0; i < providers.length; i++) {
      try {
        kf = KeyFactory.getInstance(algorithm, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], algorithm, e);
      } finally {
        if (kf != null) {
          break;
        }
      }
    }
    if (kf == null) {
      try {
        kf = KeyFactory.getInstance(algorithm);
      } catch (GeneralSecurityException e) {
        handleProviderError(null, algorithm, e);
        throw new CryptException(e.getMessage());
      }
    }
    return kf;
  }


  /**
   * <p>This finds a <code>KeyGenerator</code> using the known providers and the
   * supplied algorithm parameter.</p>
   *
   * @param  algorithm  <code>String</code> name
   *
   * @return  <code>KeyGenerator</code>
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or if the provider is not available in the environment
   */
  public static KeyGenerator getKeyGenerator(final String algorithm)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    KeyGenerator generator = null;
    for (int i = 0; i < providers.length; i++) {
      try {
        generator = KeyGenerator.getInstance(algorithm, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], algorithm, e);
      } finally {
        if (generator != null) {
          break;
        }
      }
    }
    if (generator == null) {
      try {
        generator = KeyGenerator.getInstance(algorithm);

      } catch (GeneralSecurityException e) {
        handleProviderError(null, algorithm, e);
        throw new CryptException(e.getMessage());
      }
    }
    return generator;
  }


  /**
   * <p>This finds a <code>KeyPairGenerator</code> using the known providers and
   * the supplied algorithm parameter.</p>
   *
   * @param  algorithm  <code>String</code> name
   *
   * @return  <code>KeyPairGenerator</code>
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or if the provider is not available in the environment
   */
  public static KeyPairGenerator getKeyPairGenerator(final String algorithm)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    KeyPairGenerator generator = null;
    for (int i = 0; i < providers.length; i++) {
      try {
        generator = KeyPairGenerator.getInstance(algorithm, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], algorithm, e);
      } finally {
        if (generator != null) {
          break;
        }
      }
    }
    if (generator == null) {
      try {
        generator = KeyPairGenerator.getInstance(algorithm);

      } catch (GeneralSecurityException e) {
        handleProviderError(null, algorithm, e);
        throw new CryptException(e.getMessage());
      }
    }
    return generator;
  }


  /**
   * <p>This finds a <code>Signature</code> using the known providers and the
   * supplied parameters.</p>
   *
   * @param  digestAlgorithm  <code>String</code> name
   * @param  algorithm  <code>String</code> name
   * @param  padding  <code>String</code> name
   *
   * @return  <code>Signature</code>
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or if the provider is not available in the environment
   */
  public static Signature getSignature(
    final String digestAlgorithm,
    final String algorithm,
    final String padding)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    Signature sig = null;
    String transformation = null;
    if (digestAlgorithm != null && padding != null) {
      transformation = digestAlgorithm + "/" + algorithm + "/" + padding;
    } else if (digestAlgorithm != null) {
      transformation = digestAlgorithm + "/" + algorithm;
    } else {
      transformation = algorithm;
    }
    for (int i = 0; i < providers.length; i++) {
      try {
        sig = Signature.getInstance(transformation, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], algorithm, e);
      } finally {
        if (sig != null) {
          break;
        }
      }
    }
    if (sig == null) {
      try {
        sig = Signature.getInstance(transformation);

      } catch (GeneralSecurityException e) {
        handleProviderError(null, algorithm, e);
        throw new CryptException(e.getMessage());
      }
    }
    return sig;
  }


  /**
   * <p>This creates a <code>MessageDigest</code> using the supplied algorithm
   * name.</p>
   *
   * @param  algorithm  <code>String</code> name
   *
   * @return  <code>MessageDigest</code>
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or the provider is not available in the environment
   */
  public static MessageDigest getMessageDigest(final String algorithm)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    MessageDigest digest = null;
    for (int i = 0; i < providers.length; i++) {
      try {
        digest = MessageDigest.getInstance(algorithm, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], algorithm, e);
      } finally {
        if (digest != null) {
          break;
        }
      }
    }
    if (digest == null) {
      try {
        digest = MessageDigest.getInstance(algorithm);

      } catch (GeneralSecurityException e) {
        handleProviderError(null, algorithm, e);
        throw new CryptException(e.getMessage());
      }
    }
    return digest;
  }


  /**
   * <p>This creates a <code>KeyStore</code> using the supplied type name.</p>
   *
   * @param  type  <code>String</code>
   *
   * @return  <code>KeyStore</code>
   *
   * @throws  CryptException  if the type is not available from any provider or
   * the provider is not available in the environment
   */
  public static KeyStore getKeyStore(final String type)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    KeyStore store = null;
    String keyStoreType = type;
    if (keyStoreType == null) {
      keyStoreType = KeyStore.getDefaultType();
    }
    for (int i = 0; i < providers.length; i++) {
      try {
        store = KeyStore.getInstance(keyStoreType, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], type, e);
      } finally {
        if (store != null) {
          break;
        }
      }
    }
    if (store == null) {
      try {
        store = KeyStore.getInstance(keyStoreType);

      } catch (GeneralSecurityException e) {
        handleProviderError(null, type, e);
        throw new CryptException(e.getMessage());
      }
    }
    return store;
  }


  /**
   * <p>This creates a <code>KeyStore</code> using the default keystore type.
   * </p>
   *
   * @return  <code>KeyStore</code>
   *
   * @throws  CryptException  if the default type is not available from any
   * provider or the provider is not available in the environment
   */
  public static KeyStore getKeyStore()
    throws CryptException
  {
    return getKeyStore(null);
  }


  /**
   * <p>This creates a <code>CertificateFactory</code> using the supplied type
   * name.</p>
   *
   * @param  type  <code>String</code>
   *
   * @return  <code>CertificateFactory</code>
   *
   * @throws  CryptException  if the type is not available from any provider or
   * the provider is not available in the environment
   */
  public static CertificateFactory getCertificateFactory(final String type)
    throws CryptException
  {
    final Logger logger = LoggerFactory.getLogger(CryptProvider.class);
    CertificateFactory cf = null;
    for (int i = 0; i < providers.length; i++) {
      try {
        cf = CertificateFactory.getInstance(type, providers[i]);
      } catch (GeneralSecurityException e) {
        handleProviderError(providers[i], type, e);
      } finally {
        if (cf != null) {
          break;
        }
      }
    }
    if (cf == null) {
      try {
        cf = CertificateFactory.getInstance(type);
      } catch (GeneralSecurityException e) {
        handleProviderError(null, type, e);
        throw new CryptException(e.getMessage());
      }
    }
    return cf;
  }


  /**
   * Handles a provider algorithm lookup error.
   *
   * @param provider Name of provider that was queried for the algorithm or null
   *                 for the default provider.
   * @param algorithm Algorithm name.
   * @param error Exception thrown on provider lookup error.
   */
  private static void handleProviderError(
      final String provider,
      final String algorithm,
      final GeneralSecurityException error)
  {
    if (error instanceof NoSuchProviderException) {
      if (provider != null) {
        getLogger().debug("{} not found", provider);
      }
    } else if (error instanceof NoSuchAlgorithmException) {
      if (provider != null) {
        getLogger().debug("{} does not support {}", provider, algorithm);
      } else {
        getLogger().debug("Default provider does not support {}", provider);
      }
    }
  }


  /**
   * Gets the class logger.
   *
   * @return Logger.
   */
  private static Logger getLogger()
  {
    return LoggerFactory.getLogger(CryptProvider.class);
  }
}
