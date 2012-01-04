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

import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import org.ldaptive.LdapException;
import org.ldaptive.provider.AbstractConnectionFactory;
import org.ldaptive.provider.ConnectionException;
import org.ldaptive.ssl.TLSSocketFactory;

/**
 * Creates ldap connections using the UnboundId LDAPConnection class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class UnboundIdConnectionFactory
  extends AbstractConnectionFactory<UnboundIdProviderConfig>
{


  /**
   * Creates a new Unbound ID connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public UnboundIdConnectionFactory(final String url)
  {
    super(url);
  }


  /** {@inheritDoc} */
  @Override
  protected UnboundIdConnection createInternal(final String url)
    throws LdapException
  {
    final LDAPConnectionOptions options = new LDAPConnectionOptions();
    final String[] hostAndPort = getHostnameAndPort(url);
    UnboundIdConnection conn = null;
    boolean closeConn = false;
    try {
      LDAPConnection lc = null;
      if (getProviderConfig().getTls()) {
        lc = new LDAPConnection(options);
      } else {
        lc = new LDAPConnection(
          getProviderConfig().getSocketFactory(),
          options);
      }
      conn = new UnboundIdConnection(lc);
      lc.connect(
        hostAndPort[0],
        Integer.parseInt(hostAndPort[1]),
        getProviderConfig().getConnectTimeout());

      conn.setResponseTimeout(getProviderConfig().getResponseTimeout());
      conn.setOperationRetryResultCodes(
        getProviderConfig().getOperationRetryResultCodes());
      conn.setControlProcessor(getProviderConfig().getControlProcessor());

      if (getProviderConfig().getTls()) {
        SSLContext sslCtx = null;
        if (getProviderConfig().getSocketFactory() != null) {
          if (
            TLSSocketFactory.class.isAssignableFrom(
                getProviderConfig().getSocketFactory().getClass())) {
            try {
              final TLSSocketFactory factory = (TLSSocketFactory)
                getProviderConfig().getSocketFactory();
              sslCtx = factory.getSSLContextInitializer().initSSLContext("TLS");
            } catch (GeneralSecurityException e) {
              throw new IllegalStateException(
                "Error initializing key and trust managers",
                e);
            }
          } else {
            throw new IllegalArgumentException(
              "SocketFactory must be of type " +
              "org.ldaptive.ssl.TLSSocketFactory");
          }
        }

        final ExtendedResult result = lc.processExtendedOperation(
          new StartTLSExtendedRequest(sslCtx));
        if (result.getResultCode() != ResultCode.SUCCESS) {
          closeConn = true;
          throw new ConnectionException(
            "StartTLS failed",
            org.ldaptive.ResultCode.valueOf(
              result.getResultCode().intValue()));
        }
      }
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
