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
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import org.slf4j.LoggerFactory;

/**
 * TLSSocketFactory implementation that uses a static variables to store
 * configuration. Useful for SSL configurations that can only retrieve the
 * SSLSocketFactory from getDefault().
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SingletonTLSSocketFactory extends TLSSocketFactory
{

  /** SSLContextInitializer used for initializing SSL contexts. */
  private static SSLContextInitializer staticContextInitializer;

  /** Hostname verifier for this socket factory. */
  private static HostnameVerifier staticHostnameVerifier;

  /** Handshake completed listeners for this socket factory. */
  private static HandshakeCompletedListener[] staticHandshakeCompletedListeners;

  /** Enabled cipher suites. */
  private static String[] staticCipherSuites;

  /** Enabled protocol versions. */
  private static String[] staticEnabledProtocols;


  /** {@inheritDoc} */
  @Override
  public void setSSLContextInitializer(final SSLContextInitializer initializer)
  {
    if (staticContextInitializer != null) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).warn(
        "SSLContextInitializer is being overridden");
    }
    staticContextInitializer = initializer;
  }


  /** {@inheritDoc} */
  @Override
  public void setHostnameVerifier(final HostnameVerifier verifier)
  {
    if (staticHostnameVerifier != null) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).warn(
        "HostnameVerifier is being overridden");
    }
    staticHostnameVerifier = verifier;
  }


  /** {@inheritDoc} */
  @Override
  public void setHandshakeCompletedListeners(
    final HandshakeCompletedListener ... listeners)
  {
    if (staticHandshakeCompletedListeners != null) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).warn(
        "Handshake completed listeners are being overridden");
    }
    staticHandshakeCompletedListeners = listeners;
  }


  /** {@inheritDoc} */
  @Override
  public void setEnabledCipherSuites(final String[] suites)
  {
    if (staticCipherSuites != null) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).warn(
        "Cipher suites is being overridden");
    }
    staticCipherSuites = suites;
  }


  /** {@inheritDoc} */
  @Override
  public void setEnabledProtocols(final String[] protocols)
  {
    if (staticEnabledProtocols != null) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).warn(
        "Enabled protocols is being overridden");
    }
    staticEnabledProtocols = protocols;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
    throws GeneralSecurityException
  {
    super.setSSLContextInitializer(staticContextInitializer);
    super.setHostnameVerifier(staticHostnameVerifier);
    super.setHandshakeCompletedListeners(staticHandshakeCompletedListeners);
    super.setEnabledCipherSuites(staticCipherSuites);
    super.setEnabledProtocols(staticEnabledProtocols);
    super.initialize();
  }


  /**
   * This returns the default SSL socket factory.
   *
   * @return  socket factory
   */
  public static SocketFactory getDefault()
  {
    final SingletonTLSSocketFactory sf = new SingletonTLSSocketFactory();
    try {
      sf.initialize();
    } catch (GeneralSecurityException e) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).error(
        "Error initializing socket factory",
        e);
    }
    return sf;
  }
}
