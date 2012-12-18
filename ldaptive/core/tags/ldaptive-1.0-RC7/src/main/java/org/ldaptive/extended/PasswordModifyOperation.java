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
package org.ldaptive.extended;

import org.ldaptive.AbstractOperation;
import org.ldaptive.Connection;
import org.ldaptive.Credential;
import org.ldaptive.LdapException;
import org.ldaptive.Response;

/**
 * Executes an ldap password modify operation. See RFC 3062.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PasswordModifyOperation
  extends AbstractOperation<PasswordModifyRequest, Credential>
{


  /**
   * Creates a new password modify operation.
   *
   * @param  conn  connection
   */
  public PasswordModifyOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  protected Response<Credential> invoke(final PasswordModifyRequest request)
    throws LdapException
  {
    return (Response<Credential>)
      getConnection().getProviderConnection().extendedOperation(request);
  }
}
