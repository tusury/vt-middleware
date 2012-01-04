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
import org.apache.directory.shared.ldap.codec.controls.manageDsaIT.ManageDsaITFactory;
import org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsFactory;
import org.apache.directory.shared.ldap.codec.standalone.StandaloneLdapApiService;
import org.apache.directory.shared.ldap.extras.controls.ppolicy.PasswordPolicyFactory;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;
import org.ldaptive.ssl.KeyStoreCredentialConfig;
import org.ldaptive.ssl.SSLContextInitializer;
import org.ldaptive.ssl.TLSSocketFactory;

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
    final ConnectionFactory<ApacheLdapProviderConfig> cf =
      new ApacheLdapConnectionFactory(cc.getLdapUrl());
    if (cc.getResponseTimeout() > 0) {
      config.setTimeOut(cc.getResponseTimeout());
    }
    config.setSsl(cc.getSsl());
    config.setTls(cc.getTls());
    if (cc.getSslSocketFactory() != null) {
      if (
        TLSSocketFactory.class.isAssignableFrom(
            cc.getSslSocketFactory().getClass())) {
        try {
          final TLSSocketFactory factory = (TLSSocketFactory)
            cc.getSslSocketFactory();
          config.setKeyManagers(
            factory.getSSLContextInitializer().getKeyManagers());
          config.setTrustManagers(
            factory.getSSLContextInitializer().getTrustManagers());
          config.setEnabledCipherSuites(factory.getEnabledCipherSuites());
          if (factory.getEnabledProtocols() != null) {
            config.setSslProtocol(factory.getEnabledProtocols()[0]);
          }
        } catch (GeneralSecurityException e) {
          throw new IllegalStateException(
            "Error initializing key and trust managers",
            e);
        }
      } else {
        throw new IllegalArgumentException(
          "SSLSocketFactory must be of type " +
          "org.ldaptive.ssl.TLSSocketFactory");
      }
    } else if (System.getProperty("javax.net.ssl.trustStore") != null) {
      final KeyStoreCredentialConfig ksCc = new KeyStoreCredentialConfig();
      ksCc.setTrustStore(
        String.format(
          "file:%s",
          System.getProperty("javax.net.ssl.trustStore")));
      ksCc.setTrustStoreType(
        System.getProperty("javax.net.ssl.trustStoreType"));
      ksCc.setTrustStorePassword(
        System.getProperty("javax.net.ssl.trustStorePassword"));
      try {
        final SSLContextInitializer init = ksCc.createSSLContextInitializer();
        config.setTrustManagers(init.getTrustManagers());
      } catch (GeneralSecurityException e) {
        throw new IllegalStateException("Error initializing trust managers", e);
      }
    } else if (System.getProperty("javax.net.ssl.keyStore") != null) {
      final KeyStoreCredentialConfig ksCc = new KeyStoreCredentialConfig();
      ksCc.setKeyStore(
        String.format("file:%s", System.getProperty("javax.net.ssl.keyStore")));
      ksCc.setKeyStoreType(System.getProperty("javax.net.ssl.keyStoreType"));
      ksCc.setKeyStorePassword(
        System.getProperty("javax.net.ssl.keyStorePassword"));
      try {
        final SSLContextInitializer init = ksCc.createSSLContextInitializer();
        config.setKeyManagers(init.getKeyManagers());
      } catch (GeneralSecurityException e) {
        throw new IllegalStateException("Error initializing key managers", e);
      }
    }
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
