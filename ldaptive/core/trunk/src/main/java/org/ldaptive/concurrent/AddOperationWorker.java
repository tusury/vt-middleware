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
package org.ldaptive.concurrent;

import java.util.concurrent.ExecutorService;
import org.ldaptive.AddOperation;
import org.ldaptive.AddRequest;
import org.ldaptive.Connection;

/**
 * Executes an ldap add operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AddOperationWorker
  extends AbstractOperationWorker<AddRequest, Void>
{


  /**
   * Creates a new add operation worker.
   *
   * @param  conn  connection
   */
  public AddOperationWorker(final Connection conn)
  {
    this(conn, null);
  }


  /**
   * Creates a new add operation worker.
   *
   * @param  conn  connection
   * @param  es  executor service
   */
  public AddOperationWorker(final Connection conn, final ExecutorService es)
  {
    super(new AddOperation(conn), es);
  }
}
