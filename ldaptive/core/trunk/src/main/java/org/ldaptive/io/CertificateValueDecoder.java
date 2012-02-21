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

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.ldaptive.LdapUtil;

/**
 * Decodes a certificate for use in an ldap attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CertificateValueDecoder
  implements LdapAttributeValueDecoder<Certificate>
{


  /** {@inheritDoc} */
  @Override
  public Certificate decodeStringValue(final String value)
  {
    return decodeBinaryValue(LdapUtil.utf8Encode(value));
  }


  /** {@inheritDoc} */
  @Override
  public Certificate decodeBinaryValue(final byte[] value)
  {
    try {
      final CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return cf.generateCertificate(new ByteArrayInputStream(value));
    } catch (CertificateException e) {
      throw new IllegalArgumentException(
        "Attribute value could not be decoded as a certificate", e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public Class<Certificate> getType()
  {
    return Certificate.class;
  }
}
