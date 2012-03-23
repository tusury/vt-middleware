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
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
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
    // Unboundid does not do hostname verification by default
    // set a default hostname verifier if no trust settings have been configured
    if (cc.getUseStartTLS()) {
      sslContext = getHostnameVerifierSSLContext(cc);
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
    final LDAPConnectionOptions options = new LDAPConnectionOptions();
    options.setUseSynchronousMode(true);
    options.setConnectTimeoutMillis(
      cc.getConnectTimeout() > 0 ? (int) cc.getConnectTimeout() : 0);
    options.setResponseTimeoutMillis(cc.getResponseTimeout());
    ConnectionFactory<UnboundIdProviderConfig> cf = null;
    if (cc.getUseStartTLS()) {
      cf = new UnboundIdStartTLSConnectionFactory(
        cc.getLdapUrl(), factory, sslContext, options);
    } else {
      cf = new UnboundIdConnectionFactory(cc.getLdapUrl(), factory, options);
    }
    cf.setProviderConfig(config);
    return cf;
  }


  /**
   * Returns an SSLContext configured with a default hostname verifier. Uses a
   * {@link DefaultHostnameVerifier} if no trust managers have been configured.
   *
   * @param  cc  connection configuration
   *
   * @return  SSL Context
   */
  protected SSLContext getHostnameVerifierSSLContext(final ConnectionConfig cc)
  {
    SSLContext sslContext = null;
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
    return sslContext;
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
