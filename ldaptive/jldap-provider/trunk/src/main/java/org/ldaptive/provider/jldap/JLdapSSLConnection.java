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

/**
 * JLDAP provider implementation of ldap operations over SSL.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JLdapSslConnection extends JLdapConnection
{


  /**
   * Creates a new jldap ssl connection.
   *
   * @param  conn  ldap connection
   * @param  pc  provider configuration
   */
  public JLdapSslConnection(
    final LDAPConnection conn, final JLdapProviderConfig pc)
  {
    super(conn, pc);
  }
}
