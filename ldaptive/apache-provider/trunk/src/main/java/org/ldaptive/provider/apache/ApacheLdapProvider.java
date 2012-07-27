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
import org.apache.directory.shared.ldap.extras.controls.ppolicy_impl.PasswordPolicyFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncDoneValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncInfoValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncRequestValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncStateValueFactory;
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
    SyncDoneValueFactory.class.getName(),
    SyncInfoValueFactory.class.getName(),
    SyncRequestValueFactory.class.getName(),
    SyncStateValueFactory.class.getName(),
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
    LdapConnectionConfig lcc = config.getLdapConnectionConfig();
    if (lcc == null) {
      lcc = getDefaultLdapConnectionConfig(cc);
    }
    return new ApacheLdapConnectionFactory(
      cc.getLdapUrl(),
      config,
      lcc,
      cc.getUseStartTLS(),
      cc.getResponseTimeout());
  }


  /**
   * Returns an SSLContextInitializer configured with a default hostname
   * verifier. Uses a {@link DefaultHostnameVerifier} if no credential config
   * has been configured.
   *
   * @param  cc  connection configuration
   *
   * @return  SSL Context Initializer
   */
  protected SSLContextInitializer getHostnameVerifierSSLContextInitializer(
    final ConnectionConfig cc)
  {
    SSLContextInitializer contextInit;
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
        new HostnameVerifyingTrustManager(
          new DefaultHostnameVerifier(), ldapUrl.getEntriesAsString()));
    }
    return contextInit;
  }


  /**
   * Returns the default connection configuration for this provider.
   *
   * @param  cc  to configure with
   *
   * @return  ldap connection configuration
   */
  protected LdapConnectionConfig getDefaultLdapConnectionConfig(
    final ConnectionConfig cc)
  {
    final LdapConnectionConfig lcc = new LdapConnectionConfig();
    if (cc.getUseSSL() || cc.getUseStartTLS()) {
      final SSLContextInitializer contextInit =
        getHostnameVerifierSSLContextInitializer(cc);
      TrustManager[] trustManagers;
      KeyManager[] keyManagers;
      try {
        trustManagers = contextInit.getTrustManagers();
        keyManagers = contextInit.getKeyManagers();
      } catch (GeneralSecurityException e) {
        throw new IllegalArgumentException(e);
      }

      lcc.setUseSsl(cc.getUseSSL());
      lcc.setTrustManagers(trustManagers);
      lcc.setKeyManagers(keyManagers);
      if (cc.getSslConfig() != null &&
          cc.getSslConfig().getEnabledCipherSuites() != null) {
        lcc.setEnabledCipherSuites(
          cc.getSslConfig().getEnabledCipherSuites());
      }
      if (cc.getSslConfig() != null &&
          cc.getSslConfig().getEnabledProtocols() != null) {
        lcc.setSslProtocol(cc.getSslConfig().getEnabledProtocols()[0]);
      }
    }
    return lcc;
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
