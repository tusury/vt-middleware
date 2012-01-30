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
import com.novell.ldap.LDAPJSSESecureSocketFactory;

/**
 * Creates LDAPS connections using the JLDAP LDAPConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JLdapSslConnectionFactory
  extends AbstractJLdapConnectionFactory<JLdapSslConnection>
{

  /** SSL socket factory to use for SSL. */
  private SSLSocketFactory sslSocketFactory;


  /**
   * Creates a new jldap ssl connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  timeOut  time in milliseconds that operations will wait
   * @param  factory  SSL socket factory
   */
  public JLdapSslConnectionFactory(
    final String url, final int timeOut, final SSLSocketFactory factory)
  {
    super(url, timeOut);
    sslSocketFactory = factory;
  }


  /** {@inheritDoc} */
  @Override
  protected LDAPConnection createLDAPConnection()
    throws LDAPException
  {
    LDAPConnection conn = null;
    if (sslSocketFactory != null) {
      conn = new LDAPConnection(
        new LDAPJSSESecureSocketFactory(sslSocketFactory));
    } else {
      conn = new LDAPConnection(new LDAPJSSESecureSocketFactory());
    }
    return conn;
  }


  /** {@inheritDoc} */
  @Override
  protected JLdapSslConnection createJLdapConnection(final LDAPConnection conn)
  {
    return new JLdapSslConnection(conn);
  }
}
