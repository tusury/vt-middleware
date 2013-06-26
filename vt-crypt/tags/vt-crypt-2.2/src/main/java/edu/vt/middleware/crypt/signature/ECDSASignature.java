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
package edu.vt.middleware.crypt.signature;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.digest.SHA1;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.provider.asymmetric.ec.ECUtil;

/**
 * Implements the ECDSA algorithm.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ECDSASignature extends AbstractDSASignature
{

  /** Signature algorithm name. */
  private static final String ALGORITHM = "ECDSA";


  /**
   * Creates a new ECDSA signature instance that uses SHA-1 for computation of
   * message digests.
   */
  public ECDSASignature()
  {
    this(new SHA1());
  }


  /**
   * Creates a new ECDSA signature instance that uses the given digest algorithm
   * for message digest computation.
   *
   * @param  d  Message digest algorithm.
   */
  public ECDSASignature(final DigestAlgorithm d)
  {
    super(ALGORITHM);
    digest = d;
    signer = new ECDSASigner();
  }


  /** {@inheritDoc} */
  public void setSignKey(final PrivateKey key)
  {
    if (!ECPrivateKey.class.isInstance(key)) {
      throw new IllegalArgumentException("EC private key required.");
    }
    super.setSignKey(key);
  }


  /** {@inheritDoc} */
  public void setVerifyKey(final PublicKey key)
  {
    if (!ECPublicKey.class.isInstance(key)) {
      throw new IllegalArgumentException("EC public key required.");
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
    try {
      init(true, ECUtil.generatePrivateKeyParameter(signKey));
    } catch (InvalidKeyException e) {
      throw new RuntimeException("Cannot convert private key to BC format", e);
    }
  }


  /** {@inheritDoc} */
  public void initVerify()
  {
    if (verifyKey == null) {
      throw new IllegalStateException(
        "Verify key must be set prior to initialization.");
    }
    try {
      init(false, ECUtil.generatePublicKeyParameter(verifyKey));
    } catch (InvalidKeyException e) {
      throw new RuntimeException("Cannot convert public key to BC format", e);
    }
  }


  /**
   * Initialize the signer.
   *
   * @param  forSigning  Whether to initialize signer for the sign operation.
   * @param  params  BC cipher parameters.
   */
  protected void init(final boolean forSigning, final CipherParameters params)
  {
    if (forSigning && randomProvider != null) {
      signer.init(forSigning, new ParametersWithRandom(params, randomProvider));
    } else {
      signer.init(forSigning, params);
    }
  }

}
