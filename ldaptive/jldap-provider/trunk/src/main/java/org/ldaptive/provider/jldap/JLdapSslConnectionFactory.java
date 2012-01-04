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
import com.novell.ldap.LDAPJSSESecureSocketFactory;

/**
 * Creates ldap connections using the JLDAP LDAPConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JLdapSslConnectionFactory
  extends AbstractJLdapConnectionFactory<JLdapSslConnection>
{


  /**
   * Creates a new jldap ssl connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public JLdapSslConnectionFactory(final String url)
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
        new LDAPJSSESecureSocketFactory(
          getProviderConfig().getSslSocketFactory()));
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
