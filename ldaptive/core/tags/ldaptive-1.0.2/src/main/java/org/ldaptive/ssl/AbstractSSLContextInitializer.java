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
package org.ldaptive.ssl;

import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Provides common implementation for SSL context initializer.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractSSLContextInitializer
  implements SSLContextInitializer
{

  /** Trust managers. */
  private TrustManager[] trustManagers;


  /** {@inheritDoc} */
  @Override
  public TrustManager[] getTrustManagers()
    throws GeneralSecurityException
  {
    return trustManagers;
  }


  /**
   * Sets the trust managers.
   *
   * @param  managers  trust managers
   */
  @Override
  public void setTrustManagers(final TrustManager... managers)
  {
    trustManagers = managers;
  }


  /** {@inheritDoc} */
  @Override
  public SSLContext initSSLContext(final String protocol)
    throws GeneralSecurityException
  {
    final SSLContext ctx = SSLContext.getInstance(protocol);
    ctx.init(getKeyManagers(), getTrustManagers(), null);
    return ctx;
  }


  /**
   * Creates an {@link AggregateTrustManager} containing the supplied trust
   * managers.
   *
   * @param  managers  to aggregate
   *
   * @return  array containing a single aggregate trust manager
   */
  protected TrustManager[] aggregateTrustManagers(
    final TrustManager... managers)
  {
    X509TrustManager[] x509Managers = null;
    if (managers != null) {
      x509Managers = new X509TrustManager[managers.length];
      for (int i = 0; i < managers.length; i++) {
        x509Managers[i] = (X509TrustManager) managers[i];
      }
    }
    return new TrustManager[] {new AggregateTrustManager(x509Managers)};
  }
}
