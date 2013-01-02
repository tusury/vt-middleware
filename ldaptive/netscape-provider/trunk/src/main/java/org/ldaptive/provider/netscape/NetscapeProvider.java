/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.provider.netscape;

import java.io.IOException;
import java.net.Socket;
import javax.net.SocketFactory;
import netscape.ldap.LDAPConstraints;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSocketFactory;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.Provider;
import org.ldaptive.provider.ProviderConnectionFactory;
import org.ldaptive.ssl.TLSSocketFactory;

/**
 * Netscape provider implementation. Provides connection factories for clear and
 * SSL connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeProvider implements Provider<NetscapeProviderConfig>
{

  /** Provider configuration. */
  private NetscapeProviderConfig config = new NetscapeProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ProviderConnectionFactory<NetscapeProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    if (cc.getUseStartTLS()) {
      throw new UnsupportedOperationException("startTLS is not supported");
    }

    LDAPConstraints constraints = config.getLDAPConstraints();
    if (constraints == null) {
      constraints = getDefaultLDAPConstraints(cc);
    }

    LDAPSocketFactory factory = config.getLDAPSocketFactory();
    if (cc.getUseSSL() && factory == null) {
      factory = getHostnameVerifierSocketFactory(cc);
    }
    return
      new NetscapeConnectionFactory(
        cc.getLdapUrl(),
        config,
        constraints,
        factory,
        (int) cc.getConnectTimeout(),
        (int) cc.getResponseTimeout());
  }


  /**
   * Returns an SSL socket factory configured with a default hostname verifier.
   *
   * @param  cc  connection configuration
   *
   * @return  SSL socket factory
   */
  protected LDAPSocketFactory getHostnameVerifierSocketFactory(
    final ConnectionConfig cc)
  {
    // Netscape does not do hostname verification by default
    // set a default hostname verifier
    final LdapURL ldapUrl = new LdapURL(cc.getLdapUrl());
    return
      new NetscapeLDAPSocketFactory(
        TLSSocketFactory.getHostnameVerifierFactory(
          cc.getSslConfig(),
          ldapUrl.getEntriesAsString()));
  }


  /**
   * Returns the default connection constraints for this provider.
   *
   * @param  cc  to configure options with
   *
   * @return  ldap connection constraints
   */
  protected LDAPConstraints getDefaultLDAPConstraints(final ConnectionConfig cc)
  {
    return new LDAPConstraints();
  }


  /** {@inheritDoc} */
  @Override
  public NetscapeProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final NetscapeProviderConfig npc)
  {
    config = npc;
  }


  /** {@inheritDoc} */
  @Override
  public NetscapeProvider newInstance()
  {
    return new NetscapeProvider();
  }


  /** Implementation of netscape specific LDAPSocketFactory. */
  private static class NetscapeLDAPSocketFactory implements LDAPSocketFactory
  {

    /** SSL socket factory to delegate to. */
    private final SocketFactory factory;


    /**
     * Creates a new netscape ldap socket factory.
     *
     * @param  sf  ssl socket factory
     */
    public NetscapeLDAPSocketFactory(final SocketFactory sf)
    {
      factory = sf;
    }


    /** {@inheritDoc} */
    @Override
    public Socket makeSocket(final String host, final int port)
      throws LDAPException
    {
      try {
        return factory.createSocket(host, port);
      } catch (IOException e) {
        throw new LDAPException(e.getMessage());
      }
    }
  }
}
