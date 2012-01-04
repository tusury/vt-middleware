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

import org.ldaptive.ConnectionConfig;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;

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
    final ConnectionFactory<UnboundIdProviderConfig> cf =
      new UnboundIdConnectionFactory(cc.getLdapUrl());
    if (cc.getConnectTimeout() > 0) {
      config.setConnectionTimeOut((int) cc.getConnectTimeout());
    }
    config.setResponseTimeout(cc.getResponseTimeout());
    config.setSocketFactory(cc.getSslSocketFactory());
    config.setTls(cc.getTls());
    cf.setProviderConfig(config);
    return cf;
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
