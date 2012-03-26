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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.ldaptive.LdapUtils;

/**
 * Provides a default implementation of SSL context initializer which allows the
 * setting of trust and key managers in order to create an SSL context.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultSSLContextInitializer extends AbstractSSLContextInitializer
{

  /** Key managers. */
  private KeyManager[] keyManagers;


  /** {@inheritDoc} */
  @Override
  public TrustManager[] getTrustManagers()
    throws GeneralSecurityException
  {
    final TrustManagerFactory tmf = TrustManagerFactory.getInstance(
      TrustManagerFactory.getDefaultAlgorithm());
    tmf.init((KeyStore) null);
    final TrustManager[] tm = tmf.getTrustManagers();
    TrustManager[] aggregate = null;
    if (tm == null) {
      aggregate = super.getTrustManagers() != null ?
        aggregateTrustManagers(super.getTrustManagers()) : null;
    } else {
      aggregate = aggregateTrustManagers(
        LdapUtils.concatArrays(tm, super.getTrustManagers()));
    }
    return aggregate;
  }


  /** {@inheritDoc} */
  @Override
  public KeyManager[] getKeyManagers()
    throws GeneralSecurityException
  {
    return keyManagers;
  }


  /**
   * Sets the key managers.
   *
   * @param  managers  key managers
   */
  public void setKeyManagers(final KeyManager... managers)
  {
    keyManagers = managers;
  }
}
