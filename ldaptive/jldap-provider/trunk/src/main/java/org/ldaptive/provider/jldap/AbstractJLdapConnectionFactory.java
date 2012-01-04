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
import com.novell.ldap.LDAPException;
import org.ldaptive.LdapException;
import org.ldaptive.provider.AbstractConnectionFactory;
import org.ldaptive.provider.ConnectionException;

/**
 * Base class for JLDAP connection factory implementations.
 *
 * @param  <T>  type of jldap connection
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractJLdapConnectionFactory<T extends JLdapConnection>
  extends AbstractConnectionFactory<JLdapProviderConfig>
{


  /**
   * Creates a new abstract jldap connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public AbstractJLdapConnectionFactory(final String url)
  {
    super(url);
  }


  /** {@inheritDoc} */
  @Override
  protected T createInternal(final String url)
    throws LdapException
  {
    final String modUrl = getHostname(url);
    LDAPConnection conn = null;
    T jldapConn = null;
    boolean closeConn = false;
    try {
      conn = createLDAPConnection();
      if (getProviderConfig().getSocketTimeOut() > 0) {
        conn.setSocketTimeOut(getProviderConfig().getSocketTimeOut());
      }
      conn.connect(modUrl, LDAPConnection.DEFAULT_PORT);
      initializeConnection(conn);
      jldapConn = createJLdapConnection(conn);
      jldapConn.setOperationRetryResultCodes(
        getProviderConfig().getOperationRetryResultCodes());
      jldapConn.setSearchIgnoreResultCodes(
        getProviderConfig().getSearchIgnoreResultCodes());
      jldapConn.setControlProcessor(getProviderConfig().getControlProcessor());
    } catch (LDAPException e) {
      closeConn = true;
      throw new ConnectionException(e);
    } catch (RuntimeException e) {
      closeConn = true;
      throw e;
    } finally {
      if (closeConn) {
        try {
          if (conn != null) {
            conn.disconnect();
          }
        } catch (LDAPException e) {
          logger.debug("Problem tearing down connection", e);
        }
      }
    }
    return jldapConn;
  }


  /**
   * Extracts the hostname from the supplied url. If the url is a space
   * delimited string, only the last hostname is used.
   *
   * @param  url  to strip scheme from
   *
   * @return  url without scheme
   */
  protected static String getHostname(final String url)
  {
    // if url is a space delimited string, use the last value
    final String[] hosts = url.split(" ");
    String host = hosts[hosts.length - 1];

    // remove scheme, if it exists
    if (host.startsWith("ldap://")) {
      host = host.substring("ldap://".length());
    } else if (host.startsWith("ldaps://")) {
      host = host.substring("ldaps://".length());
    }

    return host;
  }


  /**
   * Creates an ldap connection for use with this connection factory.
   *
   * @return  ldap connection
   *
   * @throws  LDAPException  if an error occurs creating the connection
   */
  protected abstract LDAPConnection createLDAPConnection()
    throws LDAPException;


  /**
   * Initialize the supplied connection after a connection has been established.
   *
   * @param  conn  to initialize
   *
   * @throws  LDAPException  if an error occurs initializing the connection
   */
  protected void initializeConnection(final LDAPConnection conn)
    throws LDAPException {}


  /**
   * Creates a jldap connection of the appropriate type for this connection
   * factory.
   *
   * @param  conn  to create jldap connection with
   *
   * @return  jldap connection
   */
  protected abstract T createJLdapConnection(final LDAPConnection conn);
}
