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
import org.ldaptive.BindOperation;
import org.ldaptive.BindRequest;

/**
 * Executes an ldap bind operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BindOperationWorker
  extends AbstractOperationWorker<BindRequest, Void>
{


  /**
   * Creates a new bind operation worker.
   *
   * @param  op  bind operation to execute
   */
  public BindOperationWorker(final BindOperation op)
  {
    super(op);
  }


  /**
   * Creates a new bind operation worker.
   *
   * @param  op  bind operation to execute
   * @param  es  executor service
   */
  public BindOperationWorker(final BindOperation op, final ExecutorService es)
  {
    super(op, es);
  }
}
