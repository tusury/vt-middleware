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
package org.ldaptive.provider.apache;

import java.io.IOException;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.shared.ldap.model.exception.LdapOperationException;
import org.ldaptive.LdapException;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.AbstractConnectionFactory;
import org.ldaptive.provider.ConnectionException;

/**
 * Creates ldap connections using the Apache LdapNetworkConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ApacheLdapConnectionFactory
  extends AbstractConnectionFactory<ApacheLdapProviderConfig>
{


  /**
   * Creates a new Apache LDAP connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public ApacheLdapConnectionFactory(final String url)
  {
    super(url);
  }


  /** {@inheritDoc} */
  @Override
  protected ApacheLdapConnection createInternal(final String url)
    throws LdapException
  {
    final org.apache.directory.ldap.client.api.LdapConnectionConfig lcc =
      new org.apache.directory.ldap.client.api.LdapConnectionConfig();
    final String[] hostAndPort = getHostnameAndPort(url);
    lcc.setLdapHost(hostAndPort[0]);
    if (hostAndPort[1] != null) {
      lcc.setLdapPort(Integer.valueOf(hostAndPort[1]));
    }
    if (getProviderConfig().getSsl()) {
      lcc.setUseSsl(true);
    }
    if (getProviderConfig().getKeyManagers() != null) {
      lcc.setKeyManagers(getProviderConfig().getKeyManagers());
    }
    if (getProviderConfig().getTrustManagers() != null) {
      lcc.setTrustManagers(getProviderConfig().getTrustManagers());
    }
    if (getProviderConfig().getEnabledCipherSuites() != null) {
      lcc.setEnabledCipherSuites(getProviderConfig().getEnabledCipherSuites());
    }
    if (getProviderConfig().getSslProtocol() != null) {
      lcc.setSslProtocol(getProviderConfig().getSslProtocol());
    }

    ApacheLdapConnection conn = null;
    boolean closeConn = false;
    try {
      final LdapNetworkConnection lc = new LdapNetworkConnection(lcc);
      conn = new ApacheLdapConnection(lc);
      if (getProviderConfig().getTimeOut() > 0) {
        lc.setTimeOut(getProviderConfig().getTimeOut());
      }
      lc.connect();
      if (getProviderConfig().getTls()) {
        lc.startTls();
      }

      conn.setOperationRetryResultCodes(
        getProviderConfig().getOperationRetryResultCodes());
      conn.setControlProcessor(getProviderConfig().getControlProcessor());
    } catch (LdapOperationException e) {
      closeConn = true;
      throw new ConnectionException(
        e,
        ResultCode.valueOf(e.getResultCode().getValue()));
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      closeConn = true;
      throw new ConnectionException(e);
    } catch (IOException e) {
      closeConn = true;
      throw new ConnectionException(e);
    } catch (RuntimeException e) {
      closeConn = true;
      throw e;
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

    // remove scheme, if it exists
    if (host.startsWith("ldap://")) {
      host = host.substring("ldap://".length());
    } else if (host.startsWith("ldaps://")) {
      host = host.substring("ldaps://".length());
    }

    // remove port, if it exist
    if (host.indexOf(":") != -1) {
      hostAndPort[0] = host.substring(0, host.indexOf(":"));
      hostAndPort[1] = host.substring(host.indexOf(":") + 1, host.length());
    } else {
      hostAndPort[0] = host;
    }
    return hostAndPort;
  }
}
