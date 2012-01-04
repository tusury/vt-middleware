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
package org.ldaptive.provider.netscape;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPConstraints;
import netscape.ldap.LDAPException;
import org.ldaptive.LdapException;
import org.ldaptive.provider.AbstractConnectionFactory;
import org.ldaptive.provider.ConnectionException;

/**
 * Creates ldap connections using the Netscape LDAPConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class NetscapeConnectionFactory
  extends AbstractConnectionFactory<NetscapeProviderConfig>
{

  /** LDAP protocol version. */
  public static final int LDAP_VERSION = 3;


  /**
   * Creates a new Netscape connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public NetscapeConnectionFactory(final String url)
  {
    super(url);
  }


  /** {@inheritDoc} */
  @Override
  protected NetscapeConnection createInternal(final String url)
    throws LdapException
  {
    final LDAPConstraints constraints = new LDAPConstraints();
    final String[] hostAndPort = getHostnameAndPort(url);
    NetscapeConnection conn = null;
    boolean closeConn = false;
    try {
      LDAPConnection lc = null;
      if (getProviderConfig().getLDAPSocketFactory() != null) {
        lc = new LDAPConnection(getProviderConfig().getLDAPSocketFactory());
      } else {
        lc = new LDAPConnection();
      }
      if (getProviderConfig().getConnectTimeout() > 0) {
        lc.setConnectTimeout(getProviderConfig().getConnectTimeout());
      }
      conn = new NetscapeConnection(lc);
      if (getProviderConfig().getOperationTimeLimit() > 0) {
        conn.setTimeLimit(getProviderConfig().getOperationTimeLimit());
      }
      lc.connect(
        LDAP_VERSION,
        hostAndPort[0],
        Integer.parseInt(hostAndPort[1]),
        null,
        null,
        constraints);

      conn.setOperationRetryResultCodes(
        getProviderConfig().getOperationRetryResultCodes());
      conn.setSearchIgnoreResultCodes(
        getProviderConfig().getSearchIgnoreResultCodes());
      conn.setControlProcessor(getProviderConfig().getControlProcessor());
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


  /**
   * Extracts the hostname and port from the supplied url. If the url is a space
   * delimited string, only the last hostname is used.
   *
   * @param  url  to parse
   *
   * @return  string array with {hostname, port}
   */
  protected static String[] getHostnameAndPort(final String url)
  {
    final String[] hostAndPort = new String[2];
    // if url is a space delimited string, use the last value
    final String[] hosts = url.split(" ");
    String host = hosts[hosts.length - 1];

    String port = "389";
    // remove scheme, if it exists
    if (host.startsWith("ldap://")) {
      host = host.substring("ldap://".length());
    } else if (host.startsWith("ldaps://")) {
      host = host.substring("ldaps://".length());
      port = "636";
    }

    // remove port, if it exist
    if (host.indexOf(":") != -1) {
      hostAndPort[0] = host.substring(0, host.indexOf(":"));
      hostAndPort[1] = host.substring(host.indexOf(":") + 1, host.length());
    } else {
      hostAndPort[0] = host;
      hostAndPort[1] = port;
    }
    return hostAndPort;
  }
}
