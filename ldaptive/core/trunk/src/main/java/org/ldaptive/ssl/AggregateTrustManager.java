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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.X509TrustManager;

/**
 * Trust manager that delegates to multiple trust managers.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AggregateTrustManager implements X509TrustManager
{

  /** Trust managers to invoke. */
  private X509TrustManager[] trustManagers;


  /**
   * Creates a new aggregate trust manager.
   *
   * @param managers  to aggregate
   */
  public AggregateTrustManager(final X509TrustManager... managers)
  {
    trustManagers = managers;
  }


  /** {@inheritDoc} */
  @Override
  public void checkClientTrusted(
    final X509Certificate[] chain, final String authType)
    throws CertificateException
  {
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        tm.checkClientTrusted(chain, authType);
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public void checkServerTrusted(
    final X509Certificate[] chain, final String authType)
    throws CertificateException
  {
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        tm.checkServerTrusted(chain, authType);
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public X509Certificate[] getAcceptedIssuers()
  {
    final List<X509Certificate> issuers = new ArrayList<X509Certificate>();
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        for (X509Certificate cert : tm.getAcceptedIssuers()) {
          issuers.add(cert);
        }
      }
    }
    return issuers.toArray(new X509Certificate[issuers.size()]);
  }
}
