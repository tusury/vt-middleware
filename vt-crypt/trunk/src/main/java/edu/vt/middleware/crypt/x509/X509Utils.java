/*
  $Id: $

  Copyright (C) 2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.crypt.x509;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.signature.SignatureAlgorithm;
import edu.vt.middleware.crypt.x509.types.GeneralName;
import edu.vt.middleware.crypt.x509.types.GeneralNameList;
import edu.vt.middleware.crypt.x509.types.GeneralNameType;

/**
 * Utility class providing convenience methods for common operations on X.509
 * certificates.
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public final class X509Utils
{

  /** Message to be signed for keypair verification. */
  private static final byte[] SIGN_BYTES = "Quid est veritas?".getBytes();

  /** Private constructor of utility class. */
  private X509Utils() {}


  /**
   * Gets all subject alternative names defined on the given certificate.
   *
   * @param cert X.509 certificate to examine.
   *
   * @return List of subject alternative names or an empty list if no subject
   * alt names are defined.
   */
  public static List<GeneralName> getSubjectAltNames(
      final X509Certificate cert)
  {
    final GeneralNameList nameList;
    try {
      nameList = new ExtensionReader(cert).readSubjectAlternativeName();
    } catch (CryptException e) {
      throw new RuntimeException("Failed reading subject alt names", e);
    }
    if (nameList == null) {
      return Collections.emptyList();
    }
    return Arrays.asList(nameList.getItems());
  }


  /**
   * Gets all subject alternative names of the given type(s) on the given cert.
   *
   * @param cert X.509 certificate to examine.
   * @param types One or more name types to fetch.
   *
   * @return List of subject alternative names of the matching type(s) or an
   * empty list if no subject alt names are defined or none match given type.
   */
  public static List<GeneralName> getSubjectAltNames(
      final X509Certificate cert, final GeneralNameType ... types)
  {
    final List<GeneralName> altNames = new ArrayList<GeneralName>();
    for (GeneralName altName : getSubjectAltNames(cert)) {
      for (GeneralNameType type : types) {
        if (type.equals(altName.getType())) {
          altNames.add(altName);
        }
      }
    }
    return altNames;
  }


  /**
   * Gets all subject names present on the given certificate, i.e. the set of
   * first subject CN and all alternative names.
   *
   * @param cert X.509 certificate to examine.
   *
   * @return List of subject names.
   */
  public static List<String> getSubjectNames(final X509Certificate cert)
  {
    final List<String> names = new ArrayList<String>();
    names.add(DNUtils.getCN(cert.getSubjectX500Principal()));
    for (GeneralName altName : getSubjectAltNames(cert)) {
      names.add(altName.getName());
    }
    return names;
  }


  /**
   * Gets CN from the subject DN and the set of all alternative names of the
   * given type.
   *
   * @param cert X.509 certificate to examine.
   * @param types One or more name types to fetch.
   *
   * @return List of subject names.
   */
  public static List<String> getSubjectNames(
      final X509Certificate cert, final GeneralNameType ... types)
  {
    final List<String> names = new ArrayList<String>();
    names.add(DNUtils.getCN(cert.getSubjectX500Principal()));
    for (GeneralName altName : getSubjectAltNames(cert, types)) {
      names.add(altName.getName());
    }
    return names;
  }


  /**
   * Finds a certificate whose public key is paired with the given private key.
   *
   * @param candidates Array of candidate certificates.
   * @param key Private key used to find matching public key.
   *
   * @return Certificate among candidates whose public key that forms a keypair
   * with the given private key or null if no match is found.
   */
  public static X509Certificate findEntityCertificate(
      final X509Certificate[] candidates, final PrivateKey key)
  {
    return findEntityCertificate(Arrays.asList(candidates), key);
  }


  /**
   * Finds a certificate whose public key is paired with the given private key.
   *
   * @param candidates Collection of candidate certificates.
   * @param key Private key used to find matching public key.
   *
   * @return Certificate among candidates whose public key that forms a keypair
   * with the given private key or null if no match is found.
   */
  public static X509Certificate findEntityCertificate(
      final Collection<X509Certificate> candidates, final PrivateKey key)
  {
    SignatureAlgorithm alg;
    byte[] sig;
    for (X509Certificate c : candidates) {
      try {
        alg = SignatureAlgorithm.newInstance(c.getPublicKey().getAlgorithm());
        alg.setSignKey(key);
        alg.setVerifyKey(c.getPublicKey());
        alg.initSign();
        sig = alg.sign(SIGN_BYTES);
        alg.initVerify();
        if (alg.verify(SIGN_BYTES, sig)) {
          return c;
        }
      } catch (Exception e) {
        throw new RuntimeException(
            "Error calculating digital signature for key verification", e);
      }
    }
    return null;
  }


  /**
   * Reads a single extension field from the given X.509 certificate.
   *
   * @param cert Certificate from which to read extensions.
   * @param type Type that describes the extension to read.
   *
   * @return Value type of extension from the
   * {@link edu.vt.middleware.crypt.x509.types} package or null if no such
   * extension is defined.
   */
  public static Object readExtension(
      final X509Certificate cert, final ExtensionType type)
  {
    final ExtensionReader reader = new ExtensionReader(cert);
    try {
      return reader.read(type);
    } catch (CryptException e) {
      throw new RuntimeException("Error reading " + type, e);
    }
  }


  /**
   * Reads all the X.509 extension fields from the certificate and makes them
   * available as a map of types to values.
   *
   * @param cert Certificate to read.
   *
   * @return  Map of X.509 extension types to the corresponding value object in
   * the {@link edu.vt.middleware.crypt.x509.types} package.
   */
  public static Map<ExtensionType, Object> readExtensions(
      final X509Certificate cert)
  {
    final Map<ExtensionType, Object> map =
        new HashMap<ExtensionType, Object>(ExtensionType.values().length);
    final ExtensionReader reader = new ExtensionReader(cert);
    for (ExtensionType type : ExtensionType.values()) {
      try {
        final Object extension = reader.read(type);
        if (extension != null) {
          map.put(type, extension);
        }
      } catch (CryptException e) {
        throw new RuntimeException("Error reading " + type, e);
      }
    }
    return map;
  }
}
