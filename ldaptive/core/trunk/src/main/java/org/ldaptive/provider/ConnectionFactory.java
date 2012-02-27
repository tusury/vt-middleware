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
package org.ldaptive.provider;

import org.ldaptive.LdapException;

/**
 * Provides an interface for creating provider connections.
 *
 * @param  <T>  type of provider config for this connection factory
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ConnectionFactory<T extends ProviderConfig>
{


  /**
   * Returns the provider configuration.
   *
   * @return  provider configuration
   */
  T getProviderConfig();


  /**
   * Sets the provider configuration.
   *
   * @param  pc  provider configuration
   */
  void setProviderConfig(T pc);


  /**
   * Create a connection to an LDAP.
   *
   * @return  provider connection
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  Connection create()
    throws LdapException;
}
