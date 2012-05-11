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
package org.ldaptive;

/**
 * Interface for ldap connection implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Connection
{


  /**
   * Returns the connection config for this connection. The config may be
   * read-only.
   *
   * @return  connection config
   */
  ConnectionConfig getConnectionConfig();


  /**
   * This will establish a connection if one does not already exist to the LDAP.
   *
   * @return  response associated with the bind operation
   *
   * @throws  LdapException  if the LDAP cannot be reached
   */
  Response<Void> open()
    throws LdapException;


  /**
   * This will establish a connection if one does not already exist by binding
   * to the LDAP using the supplied bind request. This connection should be
   * closed using {@link #close()}.
   *
   * @param  request  containing bind information
   *
   * @return  response associated with the bind operation
   *
   * @throws  IllegalStateException  if the connection is already open
   * @throws  LdapException  if the LDAP cannot be reached
   */
  Response<Void> open(BindRequest request)
    throws LdapException;


  /**
   * Returns whether {@link #open(BindRequest)} was successfully invoked on this
   * connection and {@link #close()} and not been invoked. This method does not
   * indicate the viability of this connection for use.
   *
   * @return  whether this connection is open
   */
  boolean isOpen();


  /**
   * Returns the provider connection to invoke the provider specific
   * implementation. Must be called after a successful call to {@link #open()}.
   *
   * @return  provider connection
   */
  org.ldaptive.provider.Connection getProviderConnection();


  /** This will close the connection to the LDAP. */
  void close();
}
