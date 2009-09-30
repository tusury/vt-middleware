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
package edu.vt.middleware.crypt.signature;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.digest.SHA1;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;

/**
 * Implements the RSASSA-PKCS1-v1_5 signature algorithm described in <a
 * href="http://www.ietf.org/rfc/rfc2437.txt">
 * http://www.ietf.org/rfc/rfc2437.txt</a>.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class RSASignature extends SignatureAlgorithm
{

  /** Signature algorithm name. */
  private static final String ALGORITHM = "RSA";

  /** Implements the RSA signature operation. */
  private Signer signer;


  /**
   * Creates a new RSA signature class that uses a SHA-1 for message digest
   * computation.
   */
  public RSASignature()
  {
    this(new SHA1());
  }


  /**
   * Creates a new RSA signature class that uses the given digest algorithm for
   * message digest computation.
   *
   * @param  d  Message digest algorithm.
   */
  public RSASignature(final DigestAlgorithm d)
  {
    super(ALGORITHM);
    digest = d;
    signer = new RSADigestSigner(d.getDigest());
  }


  /** {@inheritDoc} */
  public void setSignKey(final PrivateKey key)
  {
    if (!RSAPrivateKey.class.isInstance(key)) {
      throw new IllegalArgumentException("RSA private key required.");
    }
    super.setSignKey(key);
  }


  /** {@inheritDoc} */
  public void setVerifyKey(final PublicKey key)
  {
    if (!RSAPublicKey.class.isInstance(key)) {
      throw new IllegalArgumentException("RSA public key required.");
    }
    super.setVerifyKey(key);
  }


  /** {@inheritDoc} */
  public void initSign()
  {
    if (signKey == null) {
      throw new IllegalStateException(
        "Sign key must be set prior to initialization.");
    }

    final RSAPrivateKey privKey = (RSAPrivateKey) signKey;
    final RSAKeyParameters bcParams = new RSAKeyParameters(
      true,
      privKey.getModulus(),
      privKey.getPrivateExponent());
    init(true, bcParams);
  }


  /** {@inheritDoc} */
  public void initVerify()
  {
    if (verifyKey == null) {
      throw new IllegalStateException(
        "Verify key must be set prior to initialization.");
    }

    final RSAPublicKey pubKey = (RSAPublicKey) verifyKey;
    final RSAKeyParameters bcParams = new RSAKeyParameters(
      false,
      pubKey.getModulus(),
      pubKey.getPublicExponent());
    init(false, bcParams);
  }


  /** {@inheritDoc} */
  public byte[] sign(final byte[] data)
    throws CryptException
  {
    signer.update(data, 0, data.length);
    try {
      return signer.generateSignature();
    } catch (DataLengthException e) {
      throw new CryptException("Data is too long for message digest.", e);
    } catch (CryptoException e) {
      throw new CryptException("Cryptographic error.", e);
    }
  }


  /** {@inheritDoc} */
  public byte[] sign(final InputStream in)
    throws CryptException, IOException
  {
    chunkUpdate(in);
    try {
      return signer.generateSignature();
    } catch (DataLengthException e) {
      throw new CryptException("Data is too long for message digest.", e);
    } catch (CryptoException e) {
      throw new CryptException("Cryptographic error.", e);
    }
  }


  /** {@inheritDoc} */
  public boolean verify(final byte[] data, final byte[] signature)
    throws CryptException
  {
    signer.update(data, 0, data.length);
    return signer.verifySignature(signature);
  }


  /** {@inheritDoc} */
  public boolean verify(final InputStream in, final byte[] signature)
    throws CryptException, IOException
  {
    chunkUpdate(in);
    return signer.verifySignature(signature);
  }


  /**
   * Initialize the signer.
   *
   * @param  forSigning  Whether to initialize signer for the sign operation.
   * @param  params  BC cipher parameters.
   */
  protected void init(final boolean forSigning, final CipherParameters params)
  {
    if (randomProvider != null) {
      signer.init(forSigning, new ParametersWithRandom(params, randomProvider));
    } else {
      signer.init(forSigning, params);
    }
  }


  /**
   * Update the signer in chunks with data read from the input stream.
   *
   * @param  in  Input stream to read from.
   *
   * @throws  IOException  On IO errors.
   */
  protected void chunkUpdate(final InputStream in)
    throws IOException
  {
    final byte[] buffer = new byte[DigestAlgorithm.CHUNK_SIZE];
    int count;
    while ((count = in.read(buffer, 0, DigestAlgorithm.CHUNK_SIZE)) > 0) {
      signer.update(buffer, 0, count);
    }
  }
}
