/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

/**
 * Executes an ldap compare operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CompareOperation extends AbstractOperation<CompareRequest, Boolean>
{


  /**
   * Creates a new compare operation.
   *
   * @param  conn  connection
   */
  public CompareOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Boolean> invoke(final CompareRequest request)
    throws LdapException
  {
    return getConnection().getProviderConnection().compare(request);
  }
}
