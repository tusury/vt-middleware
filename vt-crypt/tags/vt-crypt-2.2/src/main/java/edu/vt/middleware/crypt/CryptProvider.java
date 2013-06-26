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
 * @version  $Revision$
 */

public final class CryptProvider
{

  /** Default size of random byte array. */
  public static final int RANDOM_BYTE_ARRAY_SIZE = 256;

  /** Class logger instance. */
  private static final Logger LOGGER = LoggerFactory.getLogger(
    CryptProvider.class);

  /** List of providers to use. */
  private static String[] providers = new String[0];

  /**
   * Dynamically register the Bouncy Castle provider.
   */
  static {
    // Bouncy Castle provider
    addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider(), "BC");
  }


  /**
   * <p>Default constructor.</p>
   */
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
    System.arraycopy(providers, 0, tmp, 0, providers.length);
    tmp[providers.length] = name;
    providers = tmp;

    LOGGER.debug("Added new security provider {}", name);
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
    String transformation;
    if (mode != null && padding != null) {
      transformation = algorithm + "/" + mode + "/" + padding;
    } else if (mode != null) {
      transformation = algorithm + "/" + mode;
    } else {
      transformation = algorithm;
    }
    for (String provider : providers) {
      try {
        cipher = Cipher.getInstance(transformation, provider);
        break;
      } catch (NoSuchPaddingException e) {
        LOGGER.debug("{} does not support padding {}", provider, padding);
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, algorithm, e);
      }
    }
    if (cipher == null) {
      try {
        cipher = Cipher.getInstance(transformation);
      } catch (NoSuchPaddingException e) {
        LOGGER.debug("Default provider does not support padding {}", padding);
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
    SecretKeyFactory kf = null;
    for (String provider : providers) {
      try {
        kf = SecretKeyFactory.getInstance(algorithm, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, algorithm, e);
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
    KeyFactory kf = null;
    for (String provider : providers) {
      try {
        kf = KeyFactory.getInstance(algorithm, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, algorithm, e);
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
    KeyGenerator generator = null;
    for (String provider : providers) {
      try {
        generator = KeyGenerator.getInstance(algorithm, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, algorithm, e);
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
    KeyPairGenerator generator = null;
    for (String provider : providers) {
      try {
        generator = KeyPairGenerator.getInstance(algorithm, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, algorithm, e);
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
    Signature sig = null;
    String transformation;
    if (digestAlgorithm != null && padding != null) {
      transformation = digestAlgorithm + "/" + algorithm + "/" + padding;
    } else if (digestAlgorithm != null) {
      transformation = digestAlgorithm + "/" + algorithm;
    } else {
      transformation = algorithm;
    }
    for (String provider : providers) {
      try {
        sig = Signature.getInstance(transformation, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, algorithm, e);
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
    MessageDigest digest = null;
    for (String provider : providers) {
      try {
        digest = MessageDigest.getInstance(algorithm, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, algorithm, e);
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
    KeyStore store = null;
    String keyStoreType = type;
    if (keyStoreType == null) {
      keyStoreType = KeyStore.getDefaultType();
    }
    for (String provider : providers) {
      try {
        store = KeyStore.getInstance(keyStoreType, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, type, e);
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
    CertificateFactory cf = null;
    for (String provider : providers) {
      try {
        cf = CertificateFactory.getInstance(type, provider);
        break;
      } catch (GeneralSecurityException e) {
        handleProviderError(provider, type, e);
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
   * @param  provider  Name of provider that was queried for the algorithm or
   * null for the default provider.
   * @param  algorithm  Algorithm name.
   * @param  error  Exception thrown on provider lookup error.
   */
  private static void handleProviderError(
    final String provider,
    final String algorithm,
    final GeneralSecurityException error)
  {
    if (error instanceof NoSuchProviderException) {
      if (provider != null) {
        LOGGER.debug("{} not found", provider);
      }
    } else if (error instanceof NoSuchAlgorithmException) {
      if (provider != null) {
        LOGGER.debug("{} does not support {}", provider, algorithm);
      } else {
        LOGGER.debug("Default provider does not support {}", provider);
      }
    }
  }
}
