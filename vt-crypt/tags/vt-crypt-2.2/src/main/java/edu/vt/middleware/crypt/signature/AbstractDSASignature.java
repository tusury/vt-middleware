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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import edu.vt.middleware.crypt.CryptException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.ParametersWithRandom;

/**
 * Base class for all signatures that implement the DSA scheme.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractDSASignature extends SignatureAlgorithm
{

  /** Signer that implements DSA algorithm. */
  protected DSA signer;

  /**
   * Creates a new instance of a the given signature algorithm.
   *
   * @param  alg  Signature algorithm name, e.g. DSA, RSA.
   */
  protected AbstractDSASignature(final String alg)
  {
    super(alg);
  }


  /** {@inheritDoc} */
  public byte[] sign(final byte[] data)
    throws CryptException
  {
    final BigInteger[] out = signer.generateSignature(digest.digest(data));
    return encode(out[0], out[1]);
  }


  /** {@inheritDoc} */
  public byte[] sign(final InputStream in)
    throws CryptException, IOException
  {
    final BigInteger[] out = signer.generateSignature(digest.digest(in));
    return encode(out[0], out[1]);
  }


  /** {@inheritDoc} */
  public boolean verify(final byte[] data, final byte[] signature)
    throws CryptException
  {
    final BigInteger[] sig = decode(signature);
    return signer.verifySignature(digest.digest(data), sig[0], sig[1]);
  }


  /** {@inheritDoc} */
  public boolean verify(final InputStream in, final byte[] signature)
    throws CryptException, IOException
  {
    final BigInteger[] sig = decode(signature);
    return signer.verifySignature(digest.digest(in), sig[0], sig[1]);
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


  /**
   * Produces DER-encoded byte array output from the raw DSA signature output
   * parameters, r and s.
   *
   * @param  r  DSA signature output integer r.
   * @param  s  DSA signature output integer s.
   *
   * @return  DER-encoded concatenation of byte representations of r and s.
   *
   * @throws  edu.vt.middleware.crypt.CryptException  On cryptographic errors.
   */
  private byte[] encode(final BigInteger r, final BigInteger s)
    throws CryptException
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ASN1EncodableVector v = new ASN1EncodableVector();
    v.add(new DERInteger(r));
    v.add(new DERInteger(s));
    try {
      new DEROutputStream(out).writeObject(new DERSequence(v));
    } catch (IOException e) {
      throw new CryptException("Error encoding DSA signature.", e);
    }
    return out.toByteArray();
  }


  /**
   * Produces the r,s integer pair of a DSA signature from a DER-encoded byte
   * representation.
   *
   * @param  in  DER-encoded concatenation of byte representation of r and s.
   *
   * @return  DSA signature output parameters (r,s).
   *
   * @throws  edu.vt.middleware.crypt.CryptException  On cryptographic errors.
   */
  private BigInteger[] decode(final byte[] in)
    throws CryptException
  {
    ASN1Sequence s;
    try {
      s = (ASN1Sequence) new ASN1InputStream(in).readObject();
    } catch (IOException e) {
      throw new CryptException("Error decoding DSA signature.", e);
    }
    return
      new BigInteger[] {
        ((DERInteger) s.getObjectAt(0)).getValue(),
        ((DERInteger) s.getObjectAt(1)).getValue(),
      };
  }
}
