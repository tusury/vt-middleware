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

import org.ldaptive.LdapException;

/**
 * Provides an interface for LDAP authentication implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface AuthenticationHandler
{


  /**
   * Perform an ldap authentication.
   *
   * @param  criteria  to perform the authentication with
   *
   * @return  authentication handler response
   *
   * @throws  LdapException  if ldap operation fails
   */
  AuthenticationHandlerResponse authenticate(AuthenticationCriteria criteria)
    throws LdapException;
}
