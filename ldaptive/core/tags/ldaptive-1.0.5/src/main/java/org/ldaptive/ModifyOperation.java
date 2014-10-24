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
 * Executes an ldap modify operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ModifyOperation extends AbstractOperation<ModifyRequest, Void>
{


  /**
   * Creates a new modify operation.
   *
   * @param  conn  connection
   */
  public ModifyOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final ModifyRequest request)
    throws LdapException
  {
    return getConnection().getProviderConnection().modify(request);
  }
}
