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
package edu.vt.middleware.crypt.util;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.provider.asymmetric.ec.EC5Util;

import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;

/**
 * Elliptic curve cryptography utilty methods.
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public final class ECUtils
{
  /** Private constructor of utility class. */
  private ECUtils() {}


  /**
   * Reads a ASN.1 encoded EC private key according the structure defined in
   * sections C.4 and C.2 of SEC 1: Elliptic Curve Cryptography,
   * www.secg.org/collateral/sec1_final.pdf.
   *
   * @param  seq  ASN.1 encoded sequence of EC private key parameters.
   *
   * @return  Constructed EC key parameter specification.
   */
  public static ECPrivateKeySpec readEncodedPrivateKey(final ASN1Sequence seq)
  {
    final BigInteger s = DERInteger.getInstance(seq.getObjectAt(1)).getValue();
    final ASN1TaggedObject params = DERTaggedObject.getInstance(seq.getObjectAt(2));
    return new ECPrivateKeySpec(s, readEncodedParams((ASN1Sequence) params.getObject()));
  }


  /**
   * Reads ASN.1 encoded EC domain parameters as defined by section C.2 of
   * SEC 1: Elliptic Curve Cryptography,
   * www.secg.org/collateral/sec1_final.pdf.
   *
   * @param  seq  ASN.1 sequence of EC domain parameters.
   *
   * @return  Constructed EC domain parameter specification.
   */
  public static final ECParameterSpec readEncodedParams(final ASN1Sequence seq)
  {
    final X9ECParameters params = new X9ECParameters(seq);
    final EllipticCurve curve = EC5Util.convertCurve(params.getCurve(), params.getSeed());
    final org.bouncycastle.jce.spec.ECParameterSpec spec =
        new org.bouncycastle.jce.spec.ECParameterSpec(
            params.getCurve(),
            params.getG(),
            params.getN(),
            params.getH(),
            params.getSeed());
    return EC5Util.convertSpec(curve, spec);
  }
}
