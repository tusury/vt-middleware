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
 * Interface for ldap operations.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Operation<Q extends Request, S>
{


  /**
   * Execute this ldap operation.
   *
   * @param  request  containing the data required by this operation
   *
   * @return  response for this operation
   *
   * @throws  LdapException  if the operation fails
   */
  Response<S> execute(Q request)
    throws LdapException;
}
