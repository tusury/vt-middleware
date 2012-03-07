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

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

import edu.vt.middleware.crypt.CryptException;

/**
 * Reads collections of encoded X.509 certificates from a resource. Both PEM and
 * DER encodings are supported, as well as certificate chains in PKCS#7 format.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class X509CertificatesCredentialReader extends AbstractX509CredentialReader<X509Certificate[]>
{
  /** {@inheritDoc} */
  public X509Certificate[] read(final InputStream in) throws IOException, CryptException
  {
    try {
      final Collection<? extends Certificate> certList = getX509CertificateFactory().generateCertificates(in);
      final X509Certificate[] certs = new X509Certificate[certList.size()];
      return certList.toArray(certs);
    } catch (CertificateException e) {
      throw new CryptException("Failed reading X.509 certificate.", e);
    }
  }
}
