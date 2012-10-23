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
 * Executes an ldap delete operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DeleteOperation extends AbstractOperation<DeleteRequest, Void>
{


  /**
   * Creates a new delete operation.
   *
   * @param  conn  connection
   */
  public DeleteOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final DeleteRequest request)
    throws LdapException
  {
    return getConnection().getProviderConnection().delete(request);
  }
}
