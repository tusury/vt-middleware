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
package org.ldaptive.provider.unboundid;

import java.security.GeneralSecurityException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;
import org.ldaptive.ssl.CredentialConfig;
import org.ldaptive.ssl.DefaultHostnameVerifier;
import org.ldaptive.ssl.DefaultSSLContextInitializer;
import org.ldaptive.ssl.HostnameVerifyingTrustManager;
import org.ldaptive.ssl.SSLContextInitializer;
import org.ldaptive.ssl.TLSSocketFactory;

/**
 * UnboundId provider implementation. Provides connection factories for clear,
 * SSL, and TLS connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIdProvider implements Provider<UnboundIdProviderConfig>
{

  /** Provider configuration. */
  private UnboundIdProviderConfig config = new UnboundIdProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory<UnboundIdProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    config.makeImmutable();
    SocketFactory factory = config.getSocketFactory();
    SSLContext sslContext = null;
    if (cc.getUseStartTLS()) {
      SSLContextInitializer contextInit = null;
      if (cc.getSslConfig() != null &&
          cc.getSslConfig().getCredentialConfig() != null) {
        try {
          final CredentialConfig credConfig =
            cc.getSslConfig().getCredentialConfig();
          contextInit = credConfig.createSSLContextInitializer();
        } catch (GeneralSecurityException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        contextInit = new DefaultSSLContextInitializer();
      }
      if (cc.getSslConfig() != null &&
          cc.getSslConfig().getTrustManagers() != null) {
        contextInit.setTrustManagers(cc.getSslConfig().getTrustManagers());
      } else {
        final LdapURL ldapUrl = new LdapURL(cc.getLdapUrl());
        contextInit.setTrustManagers(
          new TrustManager[]{
            new HostnameVerifyingTrustManager(
              new DefaultHostnameVerifier(), ldapUrl.getEntriesAsString()), });
      }
      try {
        sslContext = contextInit.initSSLContext("TLS");
      } catch (GeneralSecurityException e) {
        throw new IllegalArgumentException(e);
      }
    } else if (cc.getUseSSL() && factory == null) {
      factory = getHostnameVerifierSocketFactory(cc);
    }
    if (cc.getSslConfig() != null &&
        cc.getSslConfig().getEnabledCipherSuites() != null) {
      throw new UnsupportedOperationException(
        "UnboundID provider does not support the cipher suites property");
    }
    if (cc.getSslConfig() != null &&
        cc.getSslConfig().getEnabledProtocols() != null) {
      throw new UnsupportedOperationException(
        "UnboundID provider does not support the protocols property");
    }
    final ConnectionFactory<UnboundIdProviderConfig> cf =
      new UnboundIdConnectionFactory(
        cc.getLdapUrl(),
        factory,
        sslContext,
        cc.getUseStartTLS(),
        cc.getConnectTimeout() > 0 ? (int) cc.getConnectTimeout() : 0,
        cc.getResponseTimeout());
    cf.setProviderConfig(config);
    return cf;
  }


  /**
   * Returns an SSL socket factory configured with a default hostname verifier.
   *
   * @param  cc  connection configuration
   *
   * @return  SSL socket factory
   */
  protected SocketFactory getHostnameVerifierSocketFactory(
    final ConnectionConfig cc)
  {
    // Unboundid does not do hostname verification by default
    // set a default hostname verifier
    final LdapURL ldapUrl = new LdapURL(cc.getLdapUrl());
    return TLSSocketFactory.getHostnameVerifierFactory(
      cc.getSslConfig(), ldapUrl.getEntriesAsString());
  }


  /** {@inheritDoc} */
  @Override
  public UnboundIdProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final UnboundIdProviderConfig jpc)
  {
    config = jpc;
  }


  /** {@inheritDoc} */
  @Override
  public UnboundIdProvider newInstance()
  {
    return new UnboundIdProvider();
  }
}
