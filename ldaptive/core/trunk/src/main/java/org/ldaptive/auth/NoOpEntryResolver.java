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
package org.ldaptive.auth;

import org.ldaptive.Connection;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;

/**
 * Returns an LDAP entry that contains only the DN that was supplied to it.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoOpEntryResolver implements EntryResolver
{


  /** Default constructor. */
  public NoOpEntryResolver() {}


  /** {@inheritDoc} */
  @Override
  public LdapEntry resolve(
    final Connection conn,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    return new LdapEntry(ac.getDn());
  }
}
