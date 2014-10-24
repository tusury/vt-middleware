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
package org.ldaptive.provider;

/**
 * Interface to describe the state of the connection factory. Used by {@link
 * ConnectionStrategy} to produce LDAP URLs.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ConnectionFactoryMetadata
{


  /**
   * Returns the LDAP URL the provider connection factory is using. May be space
   * delimited for multiple URLs.
   *
   * @return  ldap url
   */
  String getLdapUrl();


  /**
   * Returns the number of times the provider connection factory has created a
   * connection.
   *
   * @return  connection count
   */
  int getConnectionCount();
}
