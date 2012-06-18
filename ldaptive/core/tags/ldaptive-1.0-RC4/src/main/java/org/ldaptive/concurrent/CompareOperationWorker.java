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
import org.ldaptive.CompareOperation;
import org.ldaptive.CompareRequest;

/**
 * Executes an ldap compare operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CompareOperationWorker
  extends AbstractOperationWorker<CompareRequest, Boolean>
{


  /**
   * Creates a new compare operation worker.
   *
   * @param  op  compare operation to execute
   */
  public CompareOperationWorker(final CompareOperation op)
  {
    super(op);
  }


  /**
   * Creates a new compare operation worker.
   *
   * @param  op  compare operation to execute
   * @param  es  executor service
   */
  public CompareOperationWorker(
    final CompareOperation op,
    final ExecutorService es)
  {
    super(op, es);
  }
}
