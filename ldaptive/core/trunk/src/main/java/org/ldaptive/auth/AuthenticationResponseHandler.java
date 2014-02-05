/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
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
 * Provides post authentication handling of authentication responses.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface AuthenticationResponseHandler
{


  /**
   * Handle the response from an ldap authentication.
   *
   * @param  response  produced from an authentication
   *
   * @throws  LdapException  if an error occurs handling an authentication
   * response
   */
  void handle(AuthenticationResponse response)
    throws LdapException;
}
