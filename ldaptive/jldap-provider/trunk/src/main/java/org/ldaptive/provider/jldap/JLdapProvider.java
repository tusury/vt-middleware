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
import org.ldaptive.ConnectionConfig;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;

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
    ConnectionFactory<JLdapProviderConfig> cf = null;
    if (cc.isTlsEnabled()) {
      cf = new JLdapTlsConnectionFactory(cc.getLdapUrl());
    } else if (cc.isSslEnabled()) {
      cf = new JLdapSslConnectionFactory(cc.getLdapUrl());
    } else {
      cf = new JLdapConnectionFactory(cc.getLdapUrl());
    }
    if (cc.getResponseTimeout() > 0) {
      config.setSocketTimeOut((int) cc.getResponseTimeout());
    }
    config.setSslSocketFactory(cc.getSslSocketFactory());
    cf.setProviderConfig(config);
    return cf;
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
