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
package org.ldaptive.jaas;

import java.util.Set;
import org.ldaptive.LdapException;
import org.ldaptive.SearchRequest;

/**
 * Looks up a user's roles using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface RoleResolver
{


  /**
   * Executes a search request and converts any attributes to ldap roles.
   *
   * @param  request  to execute
   *
   * @return  ldap roles
   *
   * @throws  LdapException  if the ldap operation fails
   */
  Set<LdapRole> search(final SearchRequest request)
    throws LdapException;
}
