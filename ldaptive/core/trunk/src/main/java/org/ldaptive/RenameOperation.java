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
package org.ldaptive;

/**
 * Executes an ldap rename operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RenameOperation extends AbstractOperation<RenameRequest, Void>
{


  /**
   * Creates a new rename operation.
   *
   * @param  conn  connection
   */
  public RenameOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final RenameRequest request)
    throws LdapException
  {
    return getConnection().getProviderConnection().rename(request);
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final RenameRequest request,
    final ConnectionConfig cc) {}
}
