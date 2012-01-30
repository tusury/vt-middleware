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
import javax.net.ssl.SSLContext;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
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

  /** Socket factory to use for LDAP and LDAPS, not used for startTLS. */
  private SocketFactory sslSocketFactory;

  /** SSL context to use for startTLS. */
  private SSLContext sslContext;

  /** Whether to startTLS on connections. */
  private boolean useStartTLS;

  /** Amount of time in milliseconds that connect operations will block. */
  private int connectTimeout;

  /** Amount of time in milliseconds that operations will wait. */
  private long responseTimeout;


  /**
   * Creates a new Unbound ID connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  factory  SSL socket factory to use for LDAP and LDAPS
   * @param  sslCtx  SSL context to use for startTLS
   * @param  tls  whether to startTLS on connections
   * @param  cTimeout  connection timeout
   * @param  rTimeout  response timeout
   */
  public UnboundIdConnectionFactory(
    final String url,
    final SocketFactory factory,
    final SSLContext sslCtx,
    final boolean tls,
    final int cTimeout,
    final long rTimeout)
  {
    super(url);
    sslSocketFactory = factory;
    sslContext = sslCtx;
    useStartTLS = tls;
    connectTimeout = cTimeout;
    responseTimeout = rTimeout;
  }


  /** {@inheritDoc} */
  @Override
  protected UnboundIdConnection createInternal(final String url)
    throws LdapException
  {
    final LDAPConnectionOptions options = new LDAPConnectionOptions();
    final LdapURL ldapUrl = new LdapURL(url);
    UnboundIdConnection conn = null;
    boolean closeConn = false;
    try {
      LDAPConnection lc = null;
      if (useStartTLS) {
        lc = new LDAPConnection(options);
      } else {
        lc = new LDAPConnection(
          sslSocketFactory,
          options);
      }
      conn = new UnboundIdConnection(lc);
      lc.connect(
        ldapUrl.getLastEntry().getHostname(),
        ldapUrl.getLastEntry().getPort(),
        connectTimeout);

      conn.setResponseTimeout(responseTimeout);
      conn.setOperationRetryResultCodes(
        getProviderConfig().getOperationRetryResultCodes());
      conn.setControlProcessor(getProviderConfig().getControlProcessor());

      if (useStartTLS) {
        final ExtendedResult result = lc.processExtendedOperation(
          new StartTLSExtendedRequest(sslContext));
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
}
