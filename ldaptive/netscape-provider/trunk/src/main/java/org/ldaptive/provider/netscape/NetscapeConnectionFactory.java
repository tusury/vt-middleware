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
package org.ldaptive.provider.netscape;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPConstraints;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSocketFactory;
import org.ldaptive.LdapException;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.AbstractProviderConnectionFactory;
import org.ldaptive.provider.ConnectionException;

/**
 * Creates ldap connections using the Netscape LDAPConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeConnectionFactory
  extends AbstractProviderConnectionFactory<NetscapeProviderConfig>
{

  /** LDAP protocol version. */
  public static final int LDAP_VERSION = 3;

  /** Netscape connection constraints. */
  private final LDAPConstraints ldapConstraints;

  /** Socket factory to use for LDAP and LDAPS. */
  private final LDAPSocketFactory socketFactory;

  /** Amount of time in milliseconds that connect operations will block. */
  private final int connectTimeout;

  /** Amount of time in milliseconds that operations will wait. */
  private final int timeLimit;


  /**
   * Creates a new Netscape connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  config  provider configuration
   * @param  constraints  connection constraints
   * @param  factory  ldap socket factory
   * @param  cTimeout  connection timeout
   * @param  rTimeout  response timeout
   */
  public NetscapeConnectionFactory(
    final String url,
    final NetscapeProviderConfig config,
    final LDAPConstraints constraints,
    final LDAPSocketFactory factory,
    final int cTimeout,
    final int rTimeout)
  {
    super(url, config);
    ldapConstraints = constraints;
    socketFactory = factory;
    connectTimeout = cTimeout;
    timeLimit = rTimeout;
  }


  /** {@inheritDoc} */
  @Override
  protected NetscapeConnection createInternal(final String url)
    throws LdapException
  {
    final LdapURL ldapUrl = new LdapURL(url);
    NetscapeConnection conn = null;
    boolean closeConn = false;
    try {
      LDAPConnection lc;
      if (socketFactory != null) {
        lc = new LDAPConnection(socketFactory);
      } else {
        lc = new LDAPConnection();
      }
      if (connectTimeout > 0) {
        lc.setConnectTimeout(connectTimeout);
      }
      conn = new NetscapeConnection(lc, getProviderConfig());
      if (timeLimit > 0) {
        conn.setTimeLimit(timeLimit);
      }
      lc.connect(
        LDAP_VERSION,
        ldapUrl.getLastEntry().getHostname(),
        ldapUrl.getLastEntry().getPort(),
        null,
        null,
        ldapConstraints);
    } catch (LDAPException e) {
      closeConn = true;
      throw new ConnectionException(
        e,
        org.ldaptive.ResultCode.valueOf(e.getLDAPResultCode()));
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
