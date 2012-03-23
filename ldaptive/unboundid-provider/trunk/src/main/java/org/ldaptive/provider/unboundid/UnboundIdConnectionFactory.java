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
package org.ldaptive.provider.unboundid;

import javax.net.SocketFactory;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import org.ldaptive.LdapException;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.AbstractConnectionFactory;
import org.ldaptive.provider.ConnectionException;

/**
 * Creates ldap connections using the UnboundId LDAPConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class UnboundIdConnectionFactory
  extends AbstractConnectionFactory<UnboundIdProviderConfig>
{

  /** Socket factory to use for LDAP and LDAPS connections. */
  private final SocketFactory socketFactory;

  /** Unboundid connection options. */
  private final LDAPConnectionOptions ldapOptions;


  /**
   * Creates a new Unbound ID connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  factory  SSL socket factory to use for LDAP and LDAPS
   * @param  options  connection options
   */
  public UnboundIdConnectionFactory(
    final String url,
    final SocketFactory factory,
    final LDAPConnectionOptions options)
  {
    super(url);
    socketFactory = factory;
    ldapOptions = options;
  }


  /** {@inheritDoc} */
  @Override
  protected UnboundIdConnection createInternal(final String url)
    throws LdapException
  {
    final LdapURL ldapUrl = new LdapURL(url);
    UnboundIdConnection conn = null;
    boolean closeConn = false;
    try {
      final LDAPConnection lc = new LDAPConnection(socketFactory, ldapOptions);
      conn = new UnboundIdConnection(lc);
      lc.connect(
        ldapUrl.getLastEntry().getHostname(), ldapUrl.getLastEntry().getPort());

      conn.setOperationRetryResultCodes(
        getProviderConfig().getOperationRetryResultCodes());
      conn.setSearchIgnoreResultCodes(
        getProviderConfig().getSearchIgnoreResultCodes());
      conn.setControlProcessor(getProviderConfig().getControlProcessor());
    } catch (LDAPException e) {
      closeConn = true;
      throw new ConnectionException(
        e,
        org.ldaptive.ResultCode.valueOf(
          e.getResultCode().intValue()));
    } finally {
      if (closeConn) {
        try {
          if (conn != null) {
            conn.close();
          }
        } catch (LdapException e) {
          logger.debug("Problem tearing down connection", e);
        }
      }
    }
    return conn;
  }
}
