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
package org.ldaptive.provider.apache;

import java.security.GeneralSecurityException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.shared.ldap.codec.controls.manageDsaIT.ManageDsaITFactory;
import org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsFactory;
import org.apache.directory.shared.ldap.codec.standalone.StandaloneLdapApiService;
import org.apache.directory.shared.ldap.extras.controls.ppolicy.PasswordPolicyFactory;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;
import org.ldaptive.ssl.CredentialConfig;
import org.ldaptive.ssl.DefaultHostnameVerifier;
import org.ldaptive.ssl.DefaultSSLContextInitializer;
import org.ldaptive.ssl.HostnameVerifyingTrustManager;
import org.ldaptive.ssl.SSLContextInitializer;

/**
 * Exposes a connection factory for creating ldap connections with Apache LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapProvider implements Provider<ApacheLdapProviderConfig>
{

  /** Controls to add to the default control list. */
  protected static final String[] DEFAULT_CONTROLS = new String[] {
    ManageDsaITFactory.class.getName(),
    PagedResultsFactory.class.getName(),
    PasswordPolicyFactory.class.getName(),
  };

  /**
   * Initialize this provider.
   */
  static {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < DEFAULT_CONTROLS.length; i++) {
      sb.append(DEFAULT_CONTROLS[i]);
      if (i + 1 < DEFAULT_CONTROLS.length) {
        sb.append(",");
      }
    }
    if (!"".equals(sb.toString())) {
      System.setProperty(
        StandaloneLdapApiService.DEFAULT_CONTROLS_LIST,
        sb.toString());
    }
  }

  /** Provider configuration. */
  private ApacheLdapProviderConfig config = new ApacheLdapProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory<ApacheLdapProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    config.makeImmutable();
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
    TrustManager[] trustManagers = config.getTrustManagers();
    KeyManager[] keyManagers = config.getKeyManagers();
    try {
      if (trustManagers == null) {
        trustManagers = contextInit.getTrustManagers();
      }
      if (keyManagers == null) {
        keyManagers = contextInit.getKeyManagers();
      }
    } catch (GeneralSecurityException e) {
      throw new IllegalArgumentException(e);
    }

    final LdapConnectionConfig lcc = new LdapConnectionConfig();
    lcc.setUseSsl(cc.getUseSSL());
    if (keyManagers != null) {
      lcc.setKeyManagers(keyManagers);
    }
    if (trustManagers != null) {
      lcc.setTrustManagers(trustManagers);
    }
    if (cc.getSslConfig() != null &&
        cc.getSslConfig().getEnabledCipherSuites() != null) {
      lcc.setEnabledCipherSuites(cc.getSslConfig().getEnabledCipherSuites());
    }
    if (cc.getSslConfig() != null &&
        cc.getSslConfig().getEnabledProtocols() != null) {
      lcc.setSslProtocol(cc.getSslConfig().getEnabledProtocols()[0]);
    }
    final ConnectionFactory<ApacheLdapProviderConfig> cf =
      new ApacheLdapConnectionFactory(
        cc.getLdapUrl(), lcc, cc.getUseStartTLS(), cc.getResponseTimeout());
    cf.setProviderConfig(config);
    return cf;
  }


  /** {@inheritDoc} */
  @Override
  public ApacheLdapProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final ApacheLdapProviderConfig alpc)
  {
    config = alpc;
  }


  /** {@inheritDoc} */
  @Override
  public ApacheLdapProvider newInstance()
  {
    return new ApacheLdapProvider();
  }
}
