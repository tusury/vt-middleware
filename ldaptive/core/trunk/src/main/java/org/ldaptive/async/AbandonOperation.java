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
package org.ldaptive.async;

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes an ldap abandon operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AbandonOperation
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Connection to perform operation. */
  private final Connection connection;


  /**
   * Creates a new abandon operation.
   *
   * @param  conn  connection
   */
  public AbandonOperation(final Connection conn)
  {
    connection = conn;
  }


  /**
   * Execute this ldap operation.
   *
   * @param  request  containing the data required by this operation
   *
   * @throws  LdapException  if the operation fails
   */
  public void execute(final AbandonRequest request)
    throws LdapException
  {
    logger.debug("execute request={} with connection={}", request, connection);
    connection.getProviderConnection().abandon(request);
  }
}
