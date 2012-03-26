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
package org.ldaptive.provider.opends;

import org.ldaptive.LdapException;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.AbstractConnectionFactory;
import org.ldaptive.provider.ConnectionException;
import org.opends.sdk.Connection;
import org.opends.sdk.ErrorResultException;
import org.opends.sdk.LDAPConnectionFactory;
import org.opends.sdk.LDAPOptions;

/**
 * Creates ldap connections using the OpenDS LDAPConnectionFactory class.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDSConnectionFactory
  extends AbstractConnectionFactory<OpenDSProviderConfig>
{

  /** Ldap connection options. */
  private final LDAPOptions ldapOptions;


  /**
   * Creates a new OpenDS connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  config  provider configuration
   * @param  options  connection options
   */
  public OpenDSConnectionFactory(
    final String url,
    final OpenDSProviderConfig config,
    final LDAPOptions options)
  {
    super(url, config);
    ldapOptions = options;
  }


  /** {@inheritDoc} */
  @Override
  protected OpenDSConnection createInternal(final String url)
    throws LdapException
  {
    final LdapURL ldapUrl = new LdapURL(url);
    OpenDSConnection conn = null;
    boolean closeConn = false;
    try {
      final LDAPConnectionFactory cf = new LDAPConnectionFactory(
        ldapUrl.getLastEntry().getHostname(),
        ldapUrl.getLastEntry().getPort(),
        ldapOptions);
      final Connection c = cf.getConnection();
      conn = new OpenDSConnection(c, getProviderConfig());
    } catch (ErrorResultException e) {
      closeConn = true;
      throw new ConnectionException(
        e,
        org.ldaptive.ResultCode.valueOf(
          e.getResult().getResultCode().intValue()));
    } catch (InterruptedException e) {
      throw new LdapException(e);
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
