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
 * Provides post processing of authentication responses.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface AuthenticationResponseHandler
{


  /**
   * Process the response from an ldap authentication.
   *
   * @param  response  produced from an authentication
   *
   * @throws  LdapException  if an error occurs processing an authentication
   * response
   */
  void process(AuthenticationResponse response)
    throws LdapException;
}
