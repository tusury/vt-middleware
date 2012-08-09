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

import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import org.ldaptive.ConnectionConfig;

/**
 * UnboundID provider implementation that uses synchronous options. Attempting
 * to use connections by this provider in an asynchronous manner with throw
 * exceptions.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIDSyncProvider extends UnboundIDProvider
{


  /** {@inheritDoc} */
  @Override
  protected LDAPConnectionOptions getDefaultLDAPConnectionOptions(
    final ConnectionConfig cc)
  {
    final LDAPConnectionOptions options = super.getDefaultLDAPConnectionOptions(
      cc);
    options.setUseSynchronousMode(true);
    return options;
  }
}
