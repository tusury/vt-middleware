/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

/**
 * Executes an ldap add operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AddOperation extends AbstractOperation<AddRequest, Void>
{


  /**
   * Creates a new add operation.
   *
   * @param  conn  connection
   */
  public AddOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final AddRequest request)
    throws LdapException
  {
    return getConnection().getProviderConnection().add(request);
  }
}
