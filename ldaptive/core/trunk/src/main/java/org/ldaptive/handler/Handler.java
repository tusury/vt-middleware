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
package org.ldaptive.handler;

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;

/**
 * Interface for ldap handlers.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Handler<Q extends Request, S>
{


  /**
   * Process the supplied result for this handler.
   *
   * @param  conn  connection the operation was executed on
   * @param  request  executed by the operation
   * @param  result  produced from the operation
   *
   * @return  handler result
   *
   * @throws  LdapException  if processing fails
   */
  HandlerResult<S> process(Connection conn, Q request, S result)
    throws LdapException;
}
