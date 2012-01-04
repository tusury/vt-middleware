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
package org.ldaptive.provider.netscape;

import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSocketFactory;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;

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
  public ConnectionFactory<NetscapeProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    if (cc.getTls()) {
      throw new UnsupportedOperationException("TLS not supported");
    }

    final ConnectionFactory<NetscapeProviderConfig> cf =
      new NetscapeConnectionFactory(cc.getLdapUrl());
    if (cc.getConnectTimeout() > 0) {
      config.setConnectTimeout((int) cc.getConnectTimeout());
    }
    if (cc.getResponseTimeout() > 0) {
      config.setOperationTimeLimit((int) cc.getResponseTimeout());
    }
    if (cc.getSslSocketFactory() != null) {
      config.setLDAPSocketFactory(
        new NetscapeLDAPSocketFactory(cc.getSslSocketFactory()));
    }
    cf.setProviderConfig(config);
    return cf;
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

    /** SSL socket factory to deletgate to. */
    private SSLSocketFactory factory;


    /**
     * Creates a new netscape ldap socket factory.
     *
     * @param  sf  ssl socket factory
     */
    public NetscapeLDAPSocketFactory(final SSLSocketFactory sf)
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
