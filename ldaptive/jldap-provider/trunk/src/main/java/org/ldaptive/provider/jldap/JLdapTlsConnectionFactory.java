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

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSEStartTLSFactory;

/**
 * Creates ldap connections using the JLDAP LDAPConnection class with the start
 * tls extended operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JLdapTlsConnectionFactory
  extends AbstractJLdapConnectionFactory<JLdapTlsConnection>
{


  /**
   * Creates a new jldap tls connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public JLdapTlsConnectionFactory(final String url)
  {
    super(url);
  }


  /** {@inheritDoc} */
  @Override
  protected LDAPConnection createLDAPConnection()
    throws LDAPException
  {
    LDAPConnection conn = null;
    if (getProviderConfig().getSslSocketFactory() != null) {
      conn = new LDAPConnection(
        new LDAPJSSEStartTLSFactory(getProviderConfig().getSslSocketFactory()));
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
  protected JLdapTlsConnection createJLdapConnection(final LDAPConnection conn)
  {
    return new JLdapTlsConnection(conn);
  }
}
