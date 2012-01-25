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

/**
 * TLSSocketFactory implementation that uses a thread local variable to store
 * configuration. Useful for SSL configurations that can only retrieve the
 * SSLSocketFactory from getDefault().
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ThreadLocalTLSSocketFactory extends TLSSocketFactory
{

  /** Thread local instance of the ssl config. */
  private static final ThreadLocalSslConfig THREAD_LOCAL_SSL_CONFIG =
    new ThreadLocalSslConfig();


  /** {@inheritDoc} */
  @Override
  public SslConfig getSslConfig()
  {
    return THREAD_LOCAL_SSL_CONFIG.get();
  }


  /** {@inheritDoc} */
  @Override
  public void setSslConfig(final SslConfig config)
  {
    THREAD_LOCAL_SSL_CONFIG.set(config);
  }


  /**
   * This returns the default SSL socket factory.
   *
   * @return  socket factory
   */
  public static SocketFactory getDefault()
  {
    final ThreadLocalTLSSocketFactory sf = new ThreadLocalTLSSocketFactory();
    if (sf.getSslConfig() == null) {
      throw new NullPointerException("Thread local SslConfig has not been set");
    }
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


  /**
   * Thread local class for {@link SslConfig}.
   */
  private static class ThreadLocalSslConfig extends ThreadLocal<SslConfig> {}
}
