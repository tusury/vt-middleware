/*
  $Id: PemHelper.java 3 2008-11-11 20:58:48Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3 $
  Updated: $Date: 2008-11-11 15:58:48 -0500 (Tue, 11 Nov 2008) $
*/
package edu.vt.middleware.crypt.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

/**
 * Helper class provides operations for encoding/decoding cryptographic keys and
 * certificates to PEM format.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class PemHelper
{

  /** Encryption algorithm used for password-protected private keys. */
  public static final String KEY_ENCRYPTION_ALGORITHM = "AES-256-CBC";

  /** Hidden constructor of utility class. */
  protected PemHelper() {}


  /**
   * Encodes the given public key to PEM format.
   *
   * @param  key  Public key to encode.
   *
   * @return  Key as PEM-encoded text.
   *
   * @throws  IOException  On encoding error.
   */
  public static String encodeKey(final PublicKey key)
    throws IOException
  {
    return encodeObject(key);
  }


  /**
   * Decodes the given public key from PEM format.
   *
   * @param  pemKey  PEM-encoded public key text to decode.
   *
   * @return  Public key.
   *
   * @throws  IOException  On decoding error.
   */
  public static PublicKey decodeKey(final String pemKey)
    throws IOException
  {
    final PEMReader reader = new PEMReader(new StringReader(pemKey));
    final PublicKey key = (PublicKey) reader.readObject();
    if (key != null) {
      return key;
    } else {
      throw new IOException("Error decoding public key.");
    }
  }


  /**
   * Encodes the given private key to PEM format.
   *
   * @param  key  Private key to encode.
   * @param  password  Password used to encrypt private key using 256-bit AES
   * encryption; may be null to indicate no encryption.
   * @param  random  Secure random provider used for encrypting private key.
   *
   * @return  Key as PEM-encoded text.
   *
   * @throws  IOException  On encoding error.
   */
  public static String encodeKey(
    final PrivateKey key,
    final char[] password,
    final SecureRandom random)
    throws IOException
  {
    if (password == null || password.length == 0) {
      return encodeObject(key);
    } else {
      final StringWriter sw = new StringWriter();
      PEMWriter writer = null;
      try {
        writer = new PEMWriter(sw);
        writer.writeObject(key, KEY_ENCRYPTION_ALGORITHM, password, random);
      } finally {
        if (writer != null) {
          writer.close();
        }
      }
      return sw.toString();
    }
  }


  /**
   * Decodes the given private key from PEM format.
   *
   * @param  pemKey  PEM-encoded private key text to decode.
   * @param  password  Optional password that is used to decrypt private key
   * using DESEDE algorithm when specified.
   *
   * @return  Private key.
   *
   * @throws  IOException  On decoding error.
   */
  public static PrivateKey decodeKey(final String pemKey, final char[] password)
    throws IOException
  {
    PEMReader reader = null;
    if (password == null || password.length == 0) {
      reader = new PEMReader(new StringReader(pemKey));
    } else {
      reader = new PEMReader(
        new StringReader(pemKey),
        new PasswordFinder() {
          public char[] getPassword()
          {
            return password;
          }
        });
    }

    final KeyPair keyPair = (KeyPair) reader.readObject();
    if (keyPair != null) {
      return keyPair.getPrivate();
    } else {
      throw new IOException("Error decoding private key.");
    }
  }


  /**
   * Encodes the given certificate to PEM format.
   *
   * @param  key  Certificate to encode.
   *
   * @return  Certificate as PEM-encoded text.
   *
   * @throws  IOException  On encoding error.
   */
  public static String encodeCert(final Certificate key)
    throws IOException
  {
    return encodeObject(key);
  }


  /**
   * Decodes the given certificate from PEM format.
   *
   * @param  pemCert  PEM-encoded certificate text to decode.
   *
   * @return  Certificate.
   *
   * @throws  IOException  On decoding error.
   */
  public static Certificate decodeCert(final String pemCert)
    throws IOException
  {
    final PEMReader reader = new PEMReader(new StringReader(pemCert));
    final Certificate cert = (Certificate) reader.readObject();
    if (cert != null) {
      return cert;
    } else {
      throw new IOException("Error decoding certificate.");
    }
  }


  /**
   * Encodes the given object to PEM format if possible.
   *
   * @param  o  Object to encode.
   *
   * @return  Object as PEM-encoded text.
   *
   * @throws  IOException  On encoding error.
   */
  private static String encodeObject(final Object o)
    throws IOException
  {
    final StringWriter sw = new StringWriter();
    PEMWriter writer = null;
    try {
      writer = new PEMWriter(sw);
      writer.writeObject(o);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
    return sw.toString();
  }
}
