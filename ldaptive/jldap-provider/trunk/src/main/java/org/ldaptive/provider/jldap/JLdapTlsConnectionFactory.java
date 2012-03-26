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

import javax.net.ssl.SSLSocketFactory;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSEStartTLSFactory;

/**
 * Creates ldap connections using the JLDAP LDAPConnection class with the
 * startTLS extended operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JLdapTlsConnectionFactory
  extends AbstractJLdapConnectionFactory<JLdapTlsConnection>
{

  /** SSL socket factory to use for startTLS. */
  private final SSLSocketFactory sslSocketFactory;


  /**
   * Creates a new jldap tls connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  config  provider configuration
   * @param  timeOut  time in milliseconds that operations will wait
   * @param  factory  SSL socket factory
   */
  public JLdapTlsConnectionFactory(
    final String url,
    final JLdapProviderConfig config,
    final int timeOut,
    final SSLSocketFactory factory)
  {
    super(url, config, timeOut);
    sslSocketFactory = factory;
  }


  /** {@inheritDoc} */
  @Override
  protected LDAPConnection createLDAPConnection()
    throws LDAPException
  {
    LDAPConnection conn = null;
    if (sslSocketFactory != null) {
      conn = new LDAPConnection(new LDAPJSSEStartTLSFactory(sslSocketFactory));
    } else {
      conn = new LDAPConnection(new LDAPJSSEStartTLSFactory());
    }
    return conn;
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeConnection(final LDAPConnection conn)
    throws LDAPException
  {
    conn.startTLS();
  }


  /** {@inheritDoc} */
  @Override
  protected JLdapTlsConnection createJLdapConnection(
    final LDAPConnection conn, final JLdapProviderConfig config)
  {
    return new JLdapTlsConnection(conn, config);
  }
}
