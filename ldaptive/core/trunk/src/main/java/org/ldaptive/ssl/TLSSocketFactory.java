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
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * An extension of SSLSocketFactory that leverages an SSL context initializer.
 * Note that {@link #initialize()} must be called prior to using this socket
 * factory. This means that this class cannot be passed to implementations that
 * expect the socket factory to function immediately after construction.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class TLSSocketFactory extends AbstractTLSSocketFactory
{


  /**
   * Creates the underlying SSLContext using truststore and keystore attributes
   * and makes this factory ready for use. Must be called before factory can be
   * used.
   *
   * @throws  GeneralSecurityException  if the SSLContext cannot be created
   */
  public void initialize()
    throws GeneralSecurityException
  {
    SSLContextInitializer contextInitializer = null;
    final SslConfig sslConfig = getSslConfig();
    if (sslConfig != null) {
      final CredentialConfig credConfig = sslConfig.getCredentialConfig();
      if (credConfig != null) {
        contextInitializer = credConfig.createSSLContextInitializer();
      } else {
        contextInitializer = new DefaultSSLContextInitializer();
      }
      final TrustManager[] managers = sslConfig.getTrustManagers();
      if (managers != null) {
        contextInitializer.setTrustManagers(managers);
      }
    } else {
      contextInitializer = new DefaultSSLContextInitializer();
    }
    final SSLContext ctx = contextInitializer.initSSLContext(DEFAULT_PROTOCOL);
    factory = ctx.getSocketFactory();
  }


  /**
   * Returns the default SSL socket factory.
   *
   * @return  socket factory
   */
  public static SocketFactory getDefault()
  {
    final TLSSocketFactory sf = new TLSSocketFactory();
    try {
      sf.initialize();
    } catch (GeneralSecurityException e) {
      throw new IllegalArgumentException(
        "Error initializing socket factory", e);
    }
    return sf;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::factory=%s, sslConfig=%s]",
        getClass().getName(),
        hashCode(),
        getFactory(),
        getSslConfig());
  }
}
