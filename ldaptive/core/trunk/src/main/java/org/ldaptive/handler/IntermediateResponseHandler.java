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
package org.ldaptive.handler;

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;
import org.ldaptive.intermediate.IntermediateResponse;

/**
 * Provides post processing of an ldap intermediate response.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface IntermediateResponseHandler
  extends Handler<Request, IntermediateResponse>
{


  /**
   * Process an intermediate response from an ldap operation.
   *
   * @param  conn  the operation was performed on
   * @param  request  used to perform the search
   * @param  response  operation response
   *
   * @return  handler result
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  HandlerResult<IntermediateResponse> process(
    Connection conn, Request request, IntermediateResponse response)
    throws LdapException;
}
