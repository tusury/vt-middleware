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
import org.ldaptive.LdapException;
import org.ldaptive.Response;

/**
 * Executes an ldap who am i operation. See RFC 4532.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class WhoAmIOperation extends
  AbstractOperation<WhoAmIRequest, WhoAmIResponse>
{


  /**
   * Creates a new who am i operation.
   *
   * @param  conn  connection
   */
  public WhoAmIOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  protected Response<WhoAmIResponse> invoke(final WhoAmIRequest request)
    throws LdapException
  {
    return (Response<WhoAmIResponse>)
      getConnection().getProviderConnection().extendedOperation(request);
  }
}
