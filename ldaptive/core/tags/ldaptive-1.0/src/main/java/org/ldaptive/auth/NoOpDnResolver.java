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
package org.ldaptive.auth;

import org.ldaptive.LdapException;

/**
 * Returns a DN that is the user identifier.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoOpDnResolver implements DnResolver
{


  /**
   * Returns the user as the DN.
   *
   * @param  user  to set as DN
   *
   * @return  user as DN
   *
   * @throws  LdapException  never
   */
  @Override
  public String resolve(final String user)
    throws LdapException
  {
    return user;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return String.format("[%s@%d]", getClass().getName(), hashCode());
  }
}
