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
import org.ldaptive.BindOperation;
import org.ldaptive.BindRequest;
import org.ldaptive.Connection;

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
   * @param  conn  connection
   */
  public BindOperationWorker(final Connection conn)
  {
    this(conn, null);
  }


  /**
   * Creates a new bind operation worker.
   *
   * @param  conn  connection
   * @param  es  executor service
   */
  public BindOperationWorker(final Connection conn, final ExecutorService es)
  {
    super(new BindOperation(conn), es);
  }
}
