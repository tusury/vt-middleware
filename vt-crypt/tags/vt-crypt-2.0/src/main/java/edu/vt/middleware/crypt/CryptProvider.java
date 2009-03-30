/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(CryptProvider.class);

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
    for (int i = 0; i < providers.length; i++) {
      tmp[i] = providers[i];
    }
    tmp[providers.length] = name;
    providers = tmp;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Added new security provider " + name);
    }
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
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find algorithm " + algorithm + " in " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } catch (NoSuchPaddingException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find padding " + padding + " in " + providers[i]);
        }
      } finally {
        if (cipher != null) {
          break;
        }
      }
    }
    if (cipher == null) {
      try {
        cipher = Cipher.getInstance(transformation);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find algorithm " + algorithm);
        }
        throw new CryptException(e.getMessage());
      } catch (NoSuchPaddingException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find padding " + padding);
        }
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
    for (int i = 0; i < providers.length; i++) {
      try {
        kf = SecretKeyFactory.getInstance(algorithm, providers[i]);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find algorithm " + algorithm + " in " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (kf != null) {
          break;
        }
      }
    }
    if (kf == null) {
      try {
        kf = SecretKeyFactory.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find algorithm " + algorithm);
        }
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
    for (int i = 0; i < providers.length; i++) {
      try {
        kf = KeyFactory.getInstance(algorithm, providers[i]);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find algorithm " + algorithm + " in " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (kf != null) {
          break;
        }
      }
    }
    if (kf == null) {
      try {
        kf = KeyFactory.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find algorithm " + algorithm);
        }
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
    for (int i = 0; i < providers.length; i++) {
      try {
        generator = KeyGenerator.getInstance(algorithm, providers[i]);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find algorithm " + algorithm + " in " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (generator != null) {
          break;
        }
      }
    }
    if (generator == null) {
      try {
        generator = KeyGenerator.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find algorithm " + algorithm);
        }
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
    for (int i = 0; i < providers.length; i++) {
      try {
        generator = KeyPairGenerator.getInstance(algorithm, providers[i]);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find algorithm " + algorithm + " in " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (generator != null) {
          break;
        }
      }
    }
    if (generator == null) {
      try {
        generator = KeyPairGenerator.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find algorithm " + algorithm);
        }
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
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find algorithm " + algorithm + " in " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (sig != null) {
          break;
        }
      }
    }
    if (sig == null) {
      try {
        sig = Signature.getInstance(transformation);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find algorithm " + algorithm);
        }
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
    for (int i = 0; i < providers.length; i++) {
      try {
        digest = MessageDigest.getInstance(algorithm, providers[i]);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not find algorithm " + algorithm + " in " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (digest != null) {
          break;
        }
      }
    }
    if (digest == null) {
      try {
        digest = MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find algorithm " + algorithm);
        }
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
    for (int i = 0; i < providers.length; i++) {
      try {
        store = KeyStore.getInstance(keyStoreType, providers[i]);
      } catch (KeyStoreException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not get instance of keystore type " + type + " from " +
            providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (store != null) {
          break;
        }
      }
    }
    if (store == null) {
      try {
        store = KeyStore.getInstance(keyStoreType);
      } catch (KeyStoreException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not get instance of keystore type " + type);
        }
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
    for (int i = 0; i < providers.length; i++) {
      try {
        cf = CertificateFactory.getInstance(type, providers[i]);
      } catch (CertificateException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not get instance of certificate factory type " + type +
            " from " + providers[i]);
        }
      } catch (NoSuchProviderException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not find provider " + providers[i]);
        }
      } finally {
        if (cf != null) {
          break;
        }
      }
    }
    if (cf == null) {
      try {
        cf = CertificateFactory.getInstance(type);
      } catch (CertificateException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Could not get instance of certificate factory type " + type);
        }
        throw new CryptException(e.getMessage());
      }
    }
    return cf;
  }
}
