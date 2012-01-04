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

/**
 * Creates ldap connections using the JLDAP LDAPConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JLdapConnectionFactory
  extends AbstractJLdapConnectionFactory<JLdapConnection>
{


  /**
   * Creates a new jldap connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public JLdapConnectionFactory(final String url)
  {
    super(url);
  }


  /** {@inheritDoc} */
  @Override
  protected LDAPConnection createLDAPConnection()
    throws LDAPException
  {
    return new LDAPConnection();
  }


  /** {@inheritDoc} */
  @Override
  protected JLdapConnection createJLdapConnection(final LDAPConnection conn)
  {
    return new JLdapConnection(conn);
  }
}
