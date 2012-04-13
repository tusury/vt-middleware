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
package org.ldaptive.provider.jldap;

import java.security.Security;
import javax.net.ssl.SSLSocketFactory;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;
import org.ldaptive.ssl.TLSSocketFactory;

/**
 * JLdap provider implementation. Provides connection factories for clear, SSL,
 * and TLS connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JLdapProvider implements Provider<JLdapProviderConfig>
{

  /**
   * Add novell sasl provider.
   */
  static {
    Security.addProvider(new com.novell.sasl.client.SaslProvider());
  }

  /** Provider configuration. */
  private JLdapProviderConfig config = new JLdapProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory<JLdapProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    ConnectionFactory<JLdapProviderConfig> cf;
    if (cc.getUseStartTLS()) {
      cf = new JLdapTlsConnectionFactory(
        cc.getLdapUrl(),
        config,
        (int) cc.getResponseTimeout(),
        config.getSslSocketFactory() != null ?
          config.getSslSocketFactory() : getHostnameVerifierSocketFactory(cc));
    } else if (cc.getUseSSL()) {
      cf = new JLdapSslConnectionFactory(
        cc.getLdapUrl(),
        config,
        (int) cc.getResponseTimeout(),
        config.getSslSocketFactory() != null ?
          config.getSslSocketFactory() : getHostnameVerifierSocketFactory(cc));
    } else {
      cf = new JLdapConnectionFactory(
        cc.getLdapUrl(), config, (int) cc.getResponseTimeout());
    }
    return cf;
  }


  /**
   * Returns an SSL socket factory configured with a default hostname verifier.
   *
   * @param  cc  connection configuration
   *
   * @return  SSL socket factory
   */
  protected SSLSocketFactory getHostnameVerifierSocketFactory(
    final ConnectionConfig cc)
  {
    // JLdap does not do hostname verification by default
    // set a default hostname verifier
    final LdapURL ldapUrl = new LdapURL(cc.getLdapUrl());
    return TLSSocketFactory.getHostnameVerifierFactory(
      cc.getSslConfig(), ldapUrl.getEntriesAsString());
  }


  /** {@inheritDoc} */
  @Override
  public JLdapProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final JLdapProviderConfig jpc)
  {
    config = jpc;
  }


  /** {@inheritDoc} */
  @Override
  public JLdapProvider newInstance()
  {
    return new JLdapProvider();
  }
}
