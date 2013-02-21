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
import org.ldaptive.AddOperation;
import org.ldaptive.AddRequest;

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
   * @param  op  add operation to execute
   */
  public AddOperationWorker(final AddOperation op)
  {
    super(op);
  }


  /**
   * Creates a new add operation worker.
   *
   * @param  op  add operation to execute
   * @param  es  executor service
   */
  public AddOperationWorker(final AddOperation op, final ExecutorService es)
  {
    super(op, es);
  }
}
