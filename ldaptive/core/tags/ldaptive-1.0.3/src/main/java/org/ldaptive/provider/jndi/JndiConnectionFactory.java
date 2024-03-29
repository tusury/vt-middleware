/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.provider.jndi;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import org.ldaptive.LdapException;
import org.ldaptive.provider.AbstractProviderConnectionFactory;
import org.ldaptive.provider.ConnectionException;

/**
 * Creates connections using the JNDI {@link InitialLdapContext} class.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiConnectionFactory
  extends AbstractProviderConnectionFactory<JndiProviderConfig>
{

  /** Environment properties. */
  private final Map<String, Object> environment;


  /**
   * Creates a new jndi connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  config  provider configuration
   * @param  env  jndi context environment
   */
  public JndiConnectionFactory(
    final String url,
    final JndiProviderConfig config,
    final Map<String, Object> env)
  {
    super(url, config);
    environment = Collections.unmodifiableMap(env);
  }


  /**
   * Returns the JNDI environment for this connection factory. This map cannot
   * be modified.
   *
   * @return  jndi environment
   */
  protected Map<String, Object> getEnvironment()
  {
    return environment;
  }


  /** {@inheritDoc} */
  @Override
  protected JndiConnection createInternal(final String url)
    throws LdapException
  {
    // CheckStyle:IllegalType OFF
    // the JNDI API requires the Hashtable type
    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      getEnvironment());
    // CheckStyle:IllegalType ON
    env.put(JndiProvider.PROVIDER_URL, url);

    JndiConnection conn;
    try {
      conn = new JndiConnection(
        new InitialLdapContext(env, null),
        getProviderConfig());
    } catch (NamingException e) {
      throw new ConnectionException(
        e,
        NamingExceptionUtils.getResultCode(e.getClass()));
    }
    return conn;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::connectionCount=%s, environment=%s, providerConfig=%s]",
        getClass().getName(),
        hashCode(),
        getConnectionCount(),
        environment,
        getProviderConfig());
  }
}
