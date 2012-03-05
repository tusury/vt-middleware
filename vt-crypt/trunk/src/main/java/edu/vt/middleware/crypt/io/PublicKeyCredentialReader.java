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
package edu.vt.middleware.crypt.io;

import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.CryptProvider;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEREncodable;

/**
 * Reads encoded public keys in X.509 public key format. Both PEM and DER
 * encodings are supported.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PublicKeyCredentialReader extends AbstractEncodedCredentialReader<PublicKey>
{

  /** {@inheritDoc} */
  protected PublicKey decode(final byte[] encoded) throws CryptException
  {
    try {
      final ASN1Sequence seq = (ASN1Sequence) ASN1Object.fromByteArray(encoded);
      final ASN1Sequence innerSeq = (ASN1Sequence) seq.getObjectAt(0);
      final DEREncodable algId = innerSeq.getObjectAt(0);
      final String algorithm;
      if (RSA_ID.equals(algId)) {
        algorithm = "RSA";
      } else if (DSA_ID.equals(algId)) {
        algorithm = "DSA";
      } else {
        throw new CryptException(
          "Unsupported public key algorithm ID " + algId);
      }
      return CryptProvider.getKeyFactory(algorithm).generatePublic(new X509EncodedKeySpec(encoded));
    } catch (Exception e) {
      throw new CryptException("Invalid public key.", e);
    }
  }

}
