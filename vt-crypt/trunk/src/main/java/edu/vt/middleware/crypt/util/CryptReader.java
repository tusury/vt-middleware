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
package edu.vt.middleware.crypt.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.CryptProvider;
import edu.vt.middleware.crypt.asymmetric.RSA;
import edu.vt.middleware.crypt.pbe.EncryptionScheme;
import edu.vt.middleware.crypt.pbe.PBES1EncryptionScheme;
import edu.vt.middleware.crypt.pbe.PBES2EncryptionScheme;
import edu.vt.middleware.crypt.pkcs.PBEParameter;
import edu.vt.middleware.crypt.pkcs.PBES1Algorithm;
import edu.vt.middleware.crypt.pkcs.PBES2CipherGenerator;
import edu.vt.middleware.crypt.pkcs.PBKDF2Parameters;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

/**
 * Helper class for performing I/O read operations on cryptographic data.
 *
 * @author  Middleware Services
 * @version  $Revision: 578 $
 */
public class CryptReader
{

  /** X.509 certificate type. */
  public static final String DEFAULT_CERTIFICATE_TYPE = "X.509";

  /** DSA algorithm OID */
  public static final String DSA_ALGORITHM_ID = "1.2.840.10040.4.1";

  /** RSA algorithm OID */
  public static final String RSA_ALGORITHM_ID = "1.2.840.113549.1.1.1";

  /** Buffer size for read operations. */
  private static final int BUFFER_SIZE = 4096;

  /** Class logger */
  private static final Log LOGGER = LogFactory.getLog(CryptReader.class);


  /** Protected constructor of utility class. */
  protected CryptReader() {}


  /**
   * Reads the raw bytes of a symmetric encryption key from a file.
   *
   * @param  keyFile  File containing key data.
   * @param  algorithm  Symmetric cipher algorithm for which key is used.
   *
   * @return  Secret key.
   *
   * @throws  IOException  On IO errors.
   */
  public static SecretKey readSecretKey(
    final File keyFile,
    final String algorithm)
    throws IOException
  {
    return
      readSecretKey(
        new BufferedInputStream(new FileInputStream(keyFile)),
        algorithm);
  }


  /**
   * Reads the raw bytes of a symmetric encryption key from an input stream.
   *
   * @param  keyStream  Stream containing key data.
   * @param  algorithm  Symmetric cipher algorithm for which key is used.
   *
   * @return  Secret key.
   *
   * @throws  IOException  On IO errors.
   */
  public static SecretKey readSecretKey(
    final InputStream keyStream,
    final String algorithm)
    throws IOException
  {
    return new SecretKeySpec(readData(keyStream), algorithm);
  }


  /**
   * Reads a DER-encoded private key in PKCS#8 or OpenSSL "traditional" format
   * from a file into a {@link PrivateKey} object.
   *
   * @param  keyFile  Private key file.
   *
   * @return  Private key containing data read from file.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PrivateKey readPrivateKey(final File keyFile)
    throws CryptException, IOException
  {
    return
      readPrivateKey(new BufferedInputStream(new FileInputStream(keyFile)));
  }


  /**
   * Reads a DER-encoded private key in PKCS#8 or OpenSSL "traditional" format
   * from an input stream into a {@link PrivateKey} object.
   *
   * @param  keyStream  Input stream containing private key data.
   *
   * @return  Private key containing data read from stream.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PrivateKey readPrivateKey(final InputStream keyStream)
    throws CryptException, IOException
  {
    byte[] bytes = readData(keyStream);
    if (PemHelper.isPem(bytes)) {
      bytes = PemHelper.decode(bytes);
    }
    return generatePrivateKey((ASN1Sequence) ASN1Object.fromByteArray(bytes));
  }


  /**
   * Reads an encrypted private key in PKCS#8 or OpenSSL "traditional" format
   * from a file into a {@link PrivateKey} object.  Both DER and PEM encoded
   * keys are supported.
   *
   * @param  keyFile  Private key file.
   * @param  password  Password to decrypt private key.
   *
   * @return  Private key containing data read from file.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PrivateKey readPrivateKey(
    final File keyFile, final char[] password)
    throws CryptException, IOException
  {
    return readPrivateKey(
          new BufferedInputStream(new FileInputStream(keyFile)),
          password);
  }


  /**
   * Reads an encrypted private key in PKCS#8 or OpenSSL "traditional" format
   * from a file into a {@link PrivateKey} object.  Both DER and PEM encoded
   * keys are supported.
   *
   * @param  keyStream  Input stream containing private key data.
   * @param  password  Password to decrypt private key; MUST NOT be null.
   *
   * @return  Private key containing data read from file.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PrivateKey readPrivateKey(
    final InputStream keyStream, final char[] password)
    throws CryptException, IOException
  {
    if (password == null || password.length == 0) {
      throw new IllegalArgumentException(
          "Password is required for decrypting an encrypted private key.");
    }
    byte[] bytes = readData(keyStream);
    if (PemHelper.isPem(bytes)) {
      LOGGER.debug("Reading PEM encoded private key.");
      // Try using BC PemReader to handle OpenSSL traditional format private key
      final String pem = new String(bytes, "ASCII");
      try {
        final PEMReader reader = new PEMReader(
            new StringReader(pem),
            new PasswordFinder() {
              public char[] getPassword()
              {
                return password;
              }
            });
        final KeyPair keyPair = (KeyPair) reader.readObject();
        if (keyPair != null) {
          return keyPair.getPrivate();
        } else {
          throw new CryptException("Private key not found in key pair.");
        }
      } catch (Exception e) {
        LOGGER.debug("Failed reading key in OpenSSL format.", e);
        LOGGER.debug("Trying PKCS#8 format.");
        bytes = PemHelper.decode(bytes);
      }
    }
    bytes = decryptKey(bytes, password);
    return generatePrivateKey((ASN1Sequence) ASN1Object.fromByteArray(bytes));
  }


  /**
   * Reads a DER-encoded X.509 public key from an input stream into a {@link
   * PublicKey} object.
   *
   * @param  keyFile  File containing DER-encoded X.509 public key.
   * @param  algorithm  Name of encryption algorithm used by key.
   *
   * @return  Public key containing data read from file.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PublicKey readPublicKey(
    final File keyFile,
    final String algorithm)
    throws CryptException, IOException
  {
    return
      readPublicKey(
        new BufferedInputStream(new FileInputStream(keyFile)),
        algorithm);
  }


  /**
   * Reads a DER-encoded X.509 public key from an input stream into a {@link
   * PublicKey} object.
   *
   * @param  keyStream  Input stream containing DER-encoded X.509 public key.
   * @param  algorithm  Name of encryption algorithm used by key.
   *
   * @return  Public key containing data read from stream.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PublicKey readPublicKey(
    final InputStream keyStream,
    final String algorithm)
    throws CryptException, IOException
  {
    final KeyFactory kf = CryptProvider.getKeyFactory(algorithm);
    try {
      final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
        readData(keyStream));
      return kf.generatePublic(keySpec);
    } catch (InvalidKeySpecException e) {
      throw new CryptException("Invalid public key format.", e);
    }
  }


  /**
   * Reads a PEM-encoded public key from a file into a {@link PublicKey} object.
   *
   * @param  keyFile  File containing public key data in PEM format.
   *
   * @return  Public key containing data read from file.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PublicKey readPemPublicKey(final File keyFile)
    throws CryptException, IOException
  {
    return
      readPemPublicKey(new BufferedInputStream(new FileInputStream(keyFile)));
  }


  /**
   * Reads a PEM-encoded public key from an input stream into a {@link
   * PublicKey} object.
   *
   * @param  keyStream  Input stream containing public key data in PEM format.
   *
   * @return  Public key containing data read from stream.
   *
   * @throws  CryptException  On key format errors.
   * @throws  IOException  On key read errors.
   */
  public static PublicKey readPemPublicKey(final InputStream keyStream)
    throws CryptException, IOException
  {
    return PemHelper.decodeKey(readPem(keyStream));
  }


  /**
   * Reads a PEM or DER-encoded certificate of the default type from a file into
   * a {@link Certificate} object.
   *
   * @param  certFile  Path to certificate file.
   *
   * @return  Certificate containing data read from file.
   *
   * @throws  CryptException  On certificate format errors.
   * @throws  IOException  On read errors.
   */
  public static Certificate readCertificate(final File certFile)
    throws CryptException, IOException
  {
    return readCertificate(certFile, DEFAULT_CERTIFICATE_TYPE);
  }


  /**
   * Reads a PEM or DER-encoded certificate of the given type from a file into a
   * {@link Certificate} object.
   *
   * @param  certFile  Path to certificate file.
   * @param  type  Type of certificate to read, e.g. X.509.
   *
   * @return  Certificate containing data read from file.
   *
   * @throws  CryptException  On certificate format errors.
   * @throws  IOException  On read errors.
   */
  public static Certificate readCertificate(
    final File certFile,
    final String type)
    throws CryptException, IOException
  {
    return
      readCertificate(new BufferedInputStream(new FileInputStream(certFile)));
  }


  /**
   * Reads a PEM or DER-encoded certificate of the default type from an input
   * stream into a {@link Certificate} object.
   *
   * @param  certStream  Input stream with certificate data.
   *
   * @return  Certificate created from data read from stream.
   *
   * @throws  CryptException  On certificate read or format errors.
   */
  public static Certificate readCertificate(final InputStream certStream)
    throws CryptException
  {
    return readCertificate(certStream, DEFAULT_CERTIFICATE_TYPE);
  }


  /**
   * Reads a PEM or DER-encoded certificate of the default type from an input
   * stream into a {@link Certificate} object.
   *
   * @param  certStream  Input stream with certificate data.
   * @param  type  Type of certificate to read, e.g. X.509.
   *
   * @return  Certificate created from data read from stream.
   *
   * @throws  CryptException  On certificate read or format errors.
   */
  public static Certificate readCertificate(
    final InputStream certStream,
    final String type)
    throws CryptException
  {
    final CertificateFactory cf = CryptProvider.getCertificateFactory(type);
    try {
      return cf.generateCertificate(certStream);
    } catch (CertificateException e) {
      throw new CryptException("Certificate read/format error.", e);
    }
  }


  /**
   * Reads a certificate chain of the default certificate type from a file
   * containing data in any of the formats supported by {@link
   * #readCertificateChain(InputStream, String)}.
   *
   * @param  chainFile  Path to certificate chain file.
   *
   * @return  Array of certificates in the order in which they appear in the
   * given file.
   *
   * @throws  CryptException  On certificate format errors.
   * @throws  IOException  On read errors.
   */
  public static Certificate[] readCertificateChain(final File chainFile)
    throws CryptException, IOException
  {
    return readCertificateChain(chainFile, DEFAULT_CERTIFICATE_TYPE);
  }


  /**
   * Reads a certificate chain of the given type from a file containing data in
   * any of the formats supported by {@link #readCertificateChain(InputStream,
   * String)}.
   *
   * @param  chainFile  Path to certificate chain file.
   * @param  type  Type of certificate to read, e.g. X.509.
   *
   * @return  Array of certificates in the order in which they appear in the
   * given file.
   *
   * @throws  CryptException  On certificate format errors.
   * @throws  IOException  On read errors.
   */
  public static Certificate[] readCertificateChain(
    final File chainFile,
    final String type)
    throws CryptException, IOException
  {
    return
      readCertificateChain(
        new BufferedInputStream(new FileInputStream(chainFile)));
  }


  /**
   * Reads a certificate chain of the default certificate type from an input
   * stream containing data in any of the formats supported by {@link
   * #readCertificateChain(InputStream, String)}.
   *
   * @param  chainStream  Stream containing certificate chain data.
   *
   * @return  Array of certificates in the order in which they appear in the
   * given input stream.
   *
   * @throws  CryptException  On certificate read or format errors.
   */
  public static Certificate[] readCertificateChain(
    final InputStream chainStream)
    throws CryptException
  {
    return readCertificateChain(chainStream, DEFAULT_CERTIFICATE_TYPE);
  }


  /**
   * Reads a certificate chain of the default certificate type from an input
   * stream containing data in any of the following formats:
   *
   * <ul>
   *   <li>Sequence of DER-encoded certificates</li>
   *   <li>Concatenation of PEM-encoded certificates</li>
   *   <li>PKCS#7 certificate chain</li>
   * </ul>
   *
   * @param  chainStream  Stream containing certificate chain data.
   * @param  type  Type of certificate to read, e.g. X.509.
   *
   * @return  Array of certificates in the order in which they appear in the
   * stream.
   *
   * @throws  CryptException  On certificate read or format errors.
   */
  public static Certificate[] readCertificateChain(
    final InputStream chainStream,
    final String type)
    throws CryptException
  {
    final CertificateFactory cf = CryptProvider.getCertificateFactory(type);
    InputStream in = chainStream;
    if (!chainStream.markSupported()) {
      in = new BufferedInputStream(chainStream);
    }

    final List<Certificate> certList = new ArrayList<Certificate>();
    try {
      while (in.available() > 0) {
        final Certificate cert = cf.generateCertificate(in);
        if (cert != null) {
          certList.add(cert);
        }
      }
    } catch (CertificateException e) {
      throw new CryptException("Certificate read/format error.", e);
    } catch (IOException e) {
      throw new CryptException("Stream I/O error.");
    }
    return certList.toArray(new Certificate[certList.size()]);
  }


  /**
   * Attempts to create a Bouncy Castle <code>DERObject</code> from a byte array
   * representing ASN.1 encoded data.
   *
   * @param  data  ASN.1 encoded data as byte array.
   * @param  discardWrapper  In some cases the value of the encoded data may
   * itself be encoded data, where the latter encoded data is desired. Recall
   * ASN.1 data is of the form {TAG, SIZE, DATA}. Set this flag to true to skip
   * the first two bytes, e.g. TAG and SIZE, and treat the remaining bytes as
   * the encoded data.
   *
   * @return  DER object.
   *
   * @throws  IOException  On I/O errors.
   */
  public static DERObject readEncodedBytes(
    final byte[] data,
    final boolean discardWrapper)
    throws IOException
  {
    final ByteArrayInputStream inBytes = new ByteArrayInputStream(data);
    int size = data.length;
    if (discardWrapper) {
      inBytes.skip(2);
      size = data.length - 2;
    }

    final ASN1InputStream in = new ASN1InputStream(inBytes, size);
    try {
      return in.readObject();
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        final Log logger = LogFactory.getLog(CryptReader.class);
        if (logger.isWarnEnabled()) {
          logger.warn("Error closing ASN.1 input stream.", e);
        }
      }
    }
  }


  /**
   * Reads a PEM object from an input stream into a string.
   *
   * @param  in  Input stream containing PEM-encoded data.
   *
   * @return  Entire contents of stream as a string.
   *
   * @throws  IOException  On I/O read errors.
   */
  private static String readPem(final InputStream in)
    throws IOException
  {
    return new String(readData(in), "ASCII");
  }


  /**
   * Reads all the data in the given stream and returns the contents as a byte
   * array.
   *
   * @param  in  Input stream to read.
   *
   * @return  Entire contents of stream.
   *
   * @throws  IOException  On read errors.
   */
  private static byte[] readData(final InputStream in)
    throws IOException
  {
    final byte[] buffer = new byte[BUFFER_SIZE];
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFER_SIZE);
    int count = 0;
    try {
      while ((count = in.read(buffer, 0, BUFFER_SIZE)) > 0) {
        bos.write(buffer, 0, count);
      }
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        final Log logger = LogFactory.getLog(CryptProvider.class);
        if (logger.isWarnEnabled()) {
          logger.warn("Error closing input stream.", e);
        }
      }
    }
    return bos.toByteArray();
  }


  /**
   * Generates a private key from an ASN.1 sequence representing an unencrypted
   * private key structure in either PKCS#8 or OpenSSL "traditional" format.
   *
   * @param  sequence  ASN.1 sequence of key data.
   *
   * @return Private key.
   *
   * @throws  CryptException On key format errors.
   */
  private static PrivateKey generatePrivateKey(final ASN1Sequence sequence)
    throws CryptException
  {
    final KeySpec spec;
    final String algorithm;

    // Assume PKCS#8 and try OpenSSL "traditional" format as backup
    PrivateKeyInfo pi;
    try {
      pi = PrivateKeyInfo.getInstance(sequence);
    } catch (Exception e) {
      pi = null;
    }
    if (pi != null) {
      final String algOid = pi.getAlgorithmId().getObjectId().getId();
      if (RSA_ALGORITHM_ID.equals(algOid)) {
        algorithm = RSA.ALGORITHM;
      } else if (DSA_ALGORITHM_ID.equals(algOid)) {
        algorithm = "DSA";
      } else {
        throw new CryptException("Unsupported PKCS#8 algorithm ID " + algOid);
      }
      try {
        spec = new PKCS8EncodedKeySpec(sequence.getEncoded());
      } catch (Exception e) {
        throw new CryptException("Invalid PKCS#8 private key format.", e);
      }
    } else {
      // OpenSSL "traditional" format is an ASN.1 sequence of key parameters

      // Detect key type based on number of parameters:
      // RSA -> {version, mod, pubExp, privExp, prime1, prime2, exp1, exp2, c}
      // DSA -> {version, p, q, g, pubExp, privExp}
      if (sequence.size() == 9) {
        LOGGER.debug("Reading OpenSSL format RSA private key.");
        algorithm = "RSA";
        spec = new RSAPrivateCrtKeySpec(
            DERInteger.getInstance(sequence.getObjectAt(1)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(2)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(3)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(4)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(5)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(6)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(7)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(8)).getValue());
      } else if (sequence.size() == 6) {
        LOGGER.debug("Reading OpenSSL format DSA private key.");
        algorithm = "DSA";
        spec = new DSAPrivateKeySpec(
            DERInteger.getInstance(sequence.getObjectAt(5)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(1)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(2)).getValue(),
            DERInteger.getInstance(sequence.getObjectAt(3)).getValue());
      } else {
        throw new CryptException(
            "Invalid OpenSSL traditional private key format.");
      }
    }
    try {
      return CryptProvider.getKeyFactory(algorithm).generatePrivate(spec);
    } catch (InvalidKeySpecException e) {
      throw new CryptException("Invalid key specification", e);
    }
  }


  /**
   * Decrypts a DER-encoded private key in PKCS#8 format.
   *
   * @param  encoded  Bytes of DER-encoded private key.
   * @param  password  Password to decrypt private key.
   *
   * @return  ASN.1 encoded bytes of decrypted key.
   *
   * @throws  CryptException  On key decryption errors.
   */
  private static byte[] decryptKey(
    final byte[] encoded, final char[] password)
    throws CryptException
  {
    final EncryptionScheme scheme;
    try {
      final EncryptedPrivateKeyInfo ki = EncryptedPrivateKeyInfo.getInstance(
          ASN1Object.fromByteArray(encoded));
      final AlgorithmIdentifier alg = ki.getEncryptionAlgorithm();
      if (PKCSObjectIdentifiers.id_PBES2.equals(alg.getObjectId())) {
        // PBES2 has following parameters:
        // {
        //    {id-PBKDF2, {salt, iterationCount, keyLength (optional)}}
        //    {encryptionAlgorithmOid, iv}
        // }
        final DERSequence pbeSeq = (DERSequence) alg.getParameters();
        final PBKDF2Parameters kdfParms = PBKDF2Parameters.decode(
            (DERSequence) pbeSeq.getObjectAt(0));
        final PBES2CipherGenerator cipherGen = new PBES2CipherGenerator(
            (DERSequence) pbeSeq.getObjectAt(1));
        if (kdfParms.getLength() <= 0) {
          kdfParms.setLength(cipherGen.getKeySize() / 8);
        }
        scheme = new PBES2EncryptionScheme(kdfParms, cipherGen.generate());
      } else {
        // Use PBES1 encryption scheme to decrypt key
        final PBES1Algorithm a =
            PBES1Algorithm.fromOid(alg.getObjectId().getId());
        final PBEParameter pbeParms = PBEParameter.decode(
            (DERSequence) alg.getParameters());
        scheme = new PBES1EncryptionScheme(
            a.getDigest(),
            pbeParms,
            SymmetricAlgorithm.newInstance(a.getSpec()));
      }
      return scheme.decrypt(password, ki.getEncryptedData());
    } catch (Exception e) {
      throw new CryptException("Private key decryption failed", e);
    }
  }
}
