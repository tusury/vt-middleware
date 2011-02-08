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
package edu.vt.middleware.crypt.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import edu.vt.middleware.crypt.CryptException;

/**
 * Helper class for performing I/O write operations on cryptographic data.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public final class CryptWriter
{

  /** Protected constructor of utility class. */
  protected CryptWriter() {}


  /**
   * Writes the supplied key to the file using its native encoding. The format
   * and encoding of the key is commonly determined by the key type. See {@link
   * #writeEncodedKey(Key, OutputStream)} for more information.
   *
   * @param  key  Key to write to file.
   * @param  file  Output file descriptor.
   *
   * @throws  IOException  On write errors.
   */
  public static void writeEncodedKey(final Key key, final File file)
    throws IOException
  {
    writeEncodedKey(key, new BufferedOutputStream(new FileOutputStream(file)));
  }


  /**
   * Writes the supplied key to the output stream using its native encoding. The
   * format and encoding of the key is commonly determined by the key type:
   *
   * <ul>
   *   <li><code>SecretKey</code> - RAW format consisting of unmodified key
   *     material bytes.</li>
   *   <li><code>PrivateKey</code> - DER-encoded PKCS#8 format key.</li>
   *   <li><code>PublicKey</code> - DER-encoded X.509 format key.</li>
   * </ul>
   *
   * @param  key  Key to write.
   * @param  out  Ouput stream to write key data to.
   *
   * @throws  IOException  On write errors.
   */
  public static void writeEncodedKey(final Key key, final OutputStream out)
    throws IOException
  {
    writeData(out, key.getEncoded());
  }


  /**
   * Writes the supplied public key to the supplied file in PEM format.
   *
   * @param  key  Public key to write to file.
   * @param  file  Output file descriptor.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemKey(final PublicKey key, final File file)
    throws IOException
  {
    writePemKey(key, new BufferedOutputStream(new FileOutputStream(file)));
  }


  /**
   * Writes the supplied public key to the supplied output stream in PEM format.
   *
   * @param  key  Public key to write to file.
   * @param  out  Ouput stream to write key data to.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemKey(final PublicKey key, final OutputStream out)
    throws IOException
  {
    writeData(out, Convert.toAsciiBytes(PemHelper.encodeKey(key)));
  }


  /**
   * Writes the supplied private key to the supplied file in encrypted PEM
   * format.
   *
   * @param  key  Private key to write to file.
   * @param  password  Password used to encrypt private key using 256-bit AES
   * encryption; may be null to indicate no encryption.
   * @param  random  Secure random provider used for encrypting private key.
   * @param  file  Output file descriptor.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemKey(
    final PrivateKey key,
    final char[] password,
    final SecureRandom random,
    final File file)
    throws IOException
  {
    writePemKey(
      key,
      password,
      random,
      new BufferedOutputStream(new FileOutputStream(file)));
  }


  /**
   * Writes the supplied private key to the supplied output stream in PEM
   * format.
   *
   * @param  key  Private key to write to file.
   * @param  password  Password used to encrypt private key using 256-bit AES
   * encryption; may be null to indicate no encryption.
   * @param  random  Secure random provider used for encrypting private key.
   * @param  out  Ouput stream to write key data to.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemKey(
    final PrivateKey key,
    final char[] password,
    final SecureRandom random,
    final OutputStream out)
    throws IOException
  {
    writeData(
      out,
      Convert.toAsciiBytes(PemHelper.encodeKey(key, password, random)));
  }


  /**
   * Writes the supplied certificate to the file using its native encoding. It
   * is assumed that each certificate type would have only a single form of
   * encoding; for example, X.509 certificates would be encoded as ASN.1 DER.
   *
   * @param  cert  Certificate to write to file.
   * @param  file  Output file descriptor.
   *
   * @throws  IOException  On write errors.
   * @throws  CryptException  If the given cert cannot be decoded to bytes.
   */
  public static void writeEncodedCertificate(
    final Certificate cert,
    final File file)
    throws CryptException, IOException
  {
    writeEncodedCertificate(
      cert,
      new BufferedOutputStream(new FileOutputStream(file)));
  }


  /**
   * Writes the supplied certificate to the output stream using its native
   * encoding. It is assumed that each certificate type would have only a single
   * form of encoding; for example, X.509 certificates would be encoded as ASN.1
   * DER.
   *
   * @param  cert  Certificate to write to file.
   * @param  out  Ouput stream to write cert data to.
   *
   * @throws  IOException  On write errors.
   * @throws  CryptException  If the given cert cannot be decoded to bytes.
   */
  public static void writeEncodedCertificate(
    final Certificate cert,
    final OutputStream out)
    throws CryptException, IOException
  {
    try {
      writeData(out, cert.getEncoded());
    } catch (CertificateEncodingException e) {
      throw new CryptException("Cannot generate encoded certificate.", e);
    }
  }


  /**
   * Writes the supplied certificate to the supplied file in PEM format.
   *
   * @param  cert  Certificate to write to file.
   * @param  file  Output file descriptor.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemCertificate(
    final Certificate cert,
    final File file)
    throws IOException
  {
    writePemCertificate(
      cert,
      new BufferedOutputStream(new FileOutputStream(file)));
  }


  /**
   * Writes the supplied certificate to the supplied output stream in PEM
   * format.
   *
   * @param  cert  Certificate to write to the output stream.
   * @param  out  Ouput stream to write cert data to.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemCertificate(
    final Certificate cert,
    final OutputStream out)
    throws IOException
  {
    writeData(out, Convert.toAsciiBytes(PemHelper.encodeCert(cert)));
  }


  /**
   * Writes the supplied certificates in sequence to the file using their native
   * encoding. It is assumed that each certificate type would have only a single
   * form of encoding; for example, X.509 certificates would be encoded as ASN.1
   * DER.
   *
   * @param  certs  Certificates to write to file.
   * @param  file  Output file descriptor.
   *
   * @throws  IOException  On write errors.
   * @throws  CryptException  If the given cert cannot be decoded to bytes.
   */
  public static void writeEncodedCertificates(
    final Certificate[] certs,
    final File file)
    throws CryptException, IOException
  {
    writeEncodedCertificates(
      certs,
      new BufferedOutputStream(new FileOutputStream(file)));
  }


  /**
   * Writes the supplied certificates in sequence to the output stream using
   * their native encoding. It is assumed that each certificate type would have
   * only a single form of encoding; for example, X.509 certificates would be
   * encoded as ASN.1 DER.
   *
   * @param  certs  Certificates to write to the output stream.
   * @param  out  Ouput stream to write cert data to.
   *
   * @throws  IOException  On write errors.
   * @throws  CryptException  If the given cert cannot be decoded to bytes.
   */
  public static void writeEncodedCertificates(
    final Certificate[] certs,
    final OutputStream out)
    throws CryptException, IOException
  {
    try {
      for (int i = 0; i < certs.length; i++) {
        out.write(certs[i].getEncoded());
      }
    } catch (CertificateEncodingException e) {
      throw new CryptException("Cannot generate encoded certificate.", e);
    } finally {
      out.close();
    }
  }


  /**
   * Writes the concatenation of the given certificates in PEM format to the
   * given file.
   *
   * @param  certs  Certificates to write to file.
   * @param  file  Output file descriptor.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemCertificates(
    final Certificate[] certs,
    final File file)
    throws IOException
  {
    writePemCertificates(
      certs,
      new BufferedOutputStream(new FileOutputStream(file)));
  }


  /**
   * Writes the concatenation of the given certificates in PEM format to the
   * given output stream.
   *
   * @param  certs  Certificates to write to the output stream.
   * @param  out  Ouput stream to write cert data to.
   *
   * @throws  IOException  On write errors.
   */
  public static void writePemCertificates(
    final Certificate[] certs,
    final OutputStream out)
    throws IOException
  {
    try {
      for (int i = 0; i < certs.length; i++) {
        out.write(Convert.toAsciiBytes(PemHelper.encodeCert(certs[i])));
      }
    } finally {
      out.close();
    }
  }


  /**
   * Writes the given data to the given stream and closes it on completion.
   *
   * @param  out  Output stream to write data to.
   * @param  data  Data to be written.
   *
   * @throws  IOException  On write errors.
   */
  private static void writeData(final OutputStream out, final byte[] data)
    throws IOException
  {
    try {
      out.write(data);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
}
