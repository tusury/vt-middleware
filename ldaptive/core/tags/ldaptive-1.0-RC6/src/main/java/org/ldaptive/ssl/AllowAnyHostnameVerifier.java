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
package org.ldaptive.ssl;

import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Hostname verifier that returns true for any hostname. Use with caution.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AllowAnyHostnameVerifier
  implements HostnameVerifier, CertificateHostnameVerifier
{


  /** {@inheritDoc} */
  @Override
  public boolean verify(final String hostname, final SSLSession session)
  {
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean verify(final String hostname, final X509Certificate cert)
  {
    return true;
  }
}
