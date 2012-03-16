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
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;

/**
 * Executes an ldap modify operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ModifyOperationWorker
  extends AbstractOperationWorker<ModifyRequest, Void>
{


  /**
   * Creates a new modify operation worker.
   *
   * @param  conn  connection
   */
  public ModifyOperationWorker(final Connection conn)
  {
    this(conn, null);
  }


  /**
   * Creates a new modify operation worker.
   *
   * @param  conn  connection
   * @param  es  executor service
   */
  public ModifyOperationWorker(final Connection conn, final ExecutorService es)
  {
    super(new ModifyOperation(conn), es);
  }
}
