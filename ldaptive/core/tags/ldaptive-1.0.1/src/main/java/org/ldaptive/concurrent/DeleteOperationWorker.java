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
package org.ldaptive.concurrent;

import java.util.concurrent.ExecutorService;
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
   * @param  op  delete operation to execute
   */
  public DeleteOperationWorker(final DeleteOperation op)
  {
    super(op);
  }


  /**
   * Creates a new delete operation worker.
   *
   * @param  op  delete operation to execute
   * @param  es  executor service
   */
  public DeleteOperationWorker(
    final DeleteOperation op,
    final ExecutorService es)
  {
    super(op, es);
  }
}
