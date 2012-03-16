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
import org.ldaptive.Connection;
import org.ldaptive.DeleteOperation;
import org.ldaptive.DeleteRequest;

/**
 * Executes an ldap delete operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DeleteOperationWorker
  extends AbstractOperationWorker<DeleteRequest, Void>
{


  /**
   * Creates a new delete operation worker.
   *
   * @param  conn  connection
   */
  public DeleteOperationWorker(final Connection conn)
  {
    this(conn, null);
  }


  /**
   * Creates a new delete operation worker.
   *
   * @param  conn  connection
   * @param  es  executor service
   */
  public DeleteOperationWorker(final Connection conn, final ExecutorService es)
  {
    super(new DeleteOperation(conn), es);
  }
}
