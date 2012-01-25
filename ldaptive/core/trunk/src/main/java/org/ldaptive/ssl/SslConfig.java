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

import java.util.Arrays;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import org.ldaptive.AbstractConfig;

/**
 * Contains all the configuration data for SSL and startTLS. Providers are not
 * guaranteed to support all the options contained here.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SslConfig extends AbstractConfig
{

  /**
   * Configuration for the trust and authentication material to use for SSL
   * and startTLS.
   */
  private CredentialConfig credentialConfig;

  /** Hostname verifier for this socket factory. */
  private HostnameVerifier hostnameVerifier;

  /** Enabled cipher suites. */
  private String[] enabledCipherSuites;

  /** Enabled protocol versions. */
  private String[] enabledProtocols;

  /** Handshake completed listeners. */
  private HandshakeCompletedListener[] handshakeCompletedListeners;


  /** Default constructor. */
  public SslConfig() {}


  /**
   * Creates a new ssl config.
   *
   * @param  config  credential config
   */
  public SslConfig(final CredentialConfig config)
  {
    credentialConfig = config;
  }


  /**
   * Creates a new ssl config.
   *
   * @param  verifier  hostname verifier
   */
  public SslConfig(final HostnameVerifier verifier)
  {
    hostnameVerifier = verifier;
  }


  /**
   * Creates a new ssl config.
   *
   * @param  config  credential config
   * @param  verifier  hostname verifier
   */
  public SslConfig(
    final CredentialConfig config, final HostnameVerifier verifier)
  {
    credentialConfig = config;
    hostnameVerifier = verifier;
  }


  /**
   * Returns whether this ssl config contains any configuration data.
   *
   * @return  whether all properties are null
   */
  public boolean isEmpty()
  {
    return credentialConfig == null && hostnameVerifier == null &&
           enabledCipherSuites == null && enabledProtocols == null &&
           handshakeCompletedListeners == null;
  }


  /**
   * Returns the credential config.
   *
   * @return  credential config
   */
  public CredentialConfig getCredentialConfig()
  {
    return credentialConfig;
  }


  /**
   * Sets the credential config.
   *
   * @param  config  credential config
   */
  public void setCredentialConfig(final CredentialConfig config)
  {
    checkImmutable();
    logger.trace("setting credentialConfig: {}", config);
    credentialConfig = config;
  }


  /**
   * Returns the hostname verifier.
   *
   * @return  hostname verifier
   */
  public HostnameVerifier getHostnameVerifier()
  {
    return hostnameVerifier;
  }


  /**
   * Sets the hostname verifier.
   *
   * @param  verifier  hostname verifier
   */
  public void setHostnameVerifier(final HostnameVerifier verifier)
  {
    checkImmutable();
    logger.trace("setting hostnameVerifier: {}", verifier);
    hostnameVerifier = verifier;
  }


  /**
   * Returns the names of the SSL cipher suites to use for secure connections.
   *
   * @return  cipher suites
   */
  public String[] getEnabledCipherSuites()
  {
    return enabledCipherSuites;
  }


  /**
   * Sets the SSL cipher suites to use for secure connections.
   *
   * @param  suites  cipher suites
   */
  public void setEnabledCipherSuites(final String... suites)
  {
    checkImmutable();
    logger.trace("setting enabledCipherSuites: {}", Arrays.toString(suites));
    enabledCipherSuites = suites;
  }


  /**
   * Returns the names of the SSL protocols to use for secure connections.
   *
   * @return  enabled protocols
   */
  public String[] getEnabledProtocols()
  {
    return enabledProtocols;
  }


  /**
   * Sets the SSL protocol versions to use for secure connections.
   *
   * @param  protocols  enabled protocols
   */
  public void setEnabledProtocols(final String... protocols)
  {
    checkImmutable();
    logger.trace("setting enabledProtocols: {}", Arrays.toString(protocols));
    enabledProtocols = protocols;
  }


  /**
   * Returns the handshake completed listeners to use for secure connections.
   *
   * @return  handshake completed listeners
   */
  public HandshakeCompletedListener[] getHandshakeCompletedListeners()
  {
    return handshakeCompletedListeners;
  }


  /**
   * Sets the handshake completed listeners to use for secure connections.
   *
   * @param  listeners  for SSL handshake events
   */
  public void setHandshakeCompletedListeners(
    final HandshakeCompletedListener ... listeners)
  {
    handshakeCompletedListeners = listeners;
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
        "[%s@%d::credentialConfig=%s, hostnameVerifier=%s, " +
        "enabledCipherSuites=%s, enabledProtocols=%s, " +
        "handshakeCompletedListeners=%s]",
        getClass().getName(),
        hashCode(),
        credentialConfig,
        hostnameVerifier,
        enabledCipherSuites,
        enabledProtocols,
        handshakeCompletedListeners);
  }
}
