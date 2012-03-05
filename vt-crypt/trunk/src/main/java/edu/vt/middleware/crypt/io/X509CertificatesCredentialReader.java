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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.CryptProvider;

/**
 * Reads collections of encoded X.509 certificates from a resource. Both PEM and
 * DER encodings are supported, as well as certificate chains in PKCS#7 format.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class X509CertificatesCredentialReader implements CredentialReader<X509Certificate[]>
{

  /** Certificate type. */
  private static final String CERTIFICATE_TYPE = "X.509";


  /** {@inheritDoc} */
  public X509Certificate[] read(final File file) throws IOException, CryptException
  {
    return read(new BufferedInputStream(new FileInputStream(file)));
  }


  /** {@inheritDoc} */
  public X509Certificate[] read(final InputStream in) throws IOException, CryptException
  {
    try {
      final CertificateFactory cf = CryptProvider.getCertificateFactory(CERTIFICATE_TYPE);
      final Collection<? extends Certificate> certList = cf.generateCertificates(in);
      final X509Certificate[] certs = new X509Certificate[certList.size()];
      return certList.toArray(certs);
    } catch (CertificateException e) {
      throw new CryptException("Failed reading X.509 certificate.", e);
    }
  }
}
