/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.io;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import org.ldaptive.LdapUtil;

/**
 * Encodes a certificate for use in an ldap attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CertificateValueEncoder
  implements LdapAttributeValueEncoder<Certificate>
{

  /** PEM cert header. */
  private static final String BEGIN_CERT =
    "-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator");

  /** PEM cert footer. */
  private static final String END_CERT =
    System.getProperty("line.separator") + "-----END CERTIFICATE-----";


  /** {@inheritDoc} */
  @Override
  public String encodeStringValue(final Certificate value)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(BEGIN_CERT);
    sb.append(LdapUtil.base64Encode(encodeBinaryValue(value)));
    sb.append(END_CERT);
    return sb.toString();
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encodeBinaryValue(final Certificate value)
  {
    try {
      return value.getEncoded();
    } catch (CertificateEncodingException e) {
      throw new IllegalArgumentException("Certificate could not be encoded", e);
    }
  }
}
