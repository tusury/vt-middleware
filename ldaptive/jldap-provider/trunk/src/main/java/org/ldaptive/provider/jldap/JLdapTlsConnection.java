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

/**
 * JLDAP provider implementation of ldap operations over TLS.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JLdapTlsConnection extends JLdapConnection
{

  /**
   * Whether to call {@link LDAPConnection#stopTLS()} when {@link #close()} is
   * called.
   */
  private boolean stopTlsOnClose;


  /**
   * Creates a new jldap tls connection.
   *
   * @param  conn  ldap connection
   */
  public JLdapTlsConnection(final LDAPConnection conn)
  {
    super(conn);
  }


  /**
   * Returns whether to call {@link LDAPConnection#stopTLS()} when {@link
   * #close()} is called.
   *
   * @return  stop TLS on close
   */
  public boolean getStopTlsOnClose()
  {
    return stopTlsOnClose;
  }


  /**
   * Sets whether to call {@link LDAPConnection#stopTLS()} when {@link #close()}
   * is called.
   *
   * @param  b  stop TLS on close
   */
  public void setStopTlsOnClose(final boolean b)
  {
    logger.trace("setting stopTlsOnClose: " + b);
    stopTlsOnClose = b;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    try {
      if (stopTlsOnClose) {
        getLDAPConnection().stopTLS();
      }
    } catch (LDAPException e) {
      logger.error("Error stopping TLS", e);
    } finally {
      super.close();
    }
  }
}
