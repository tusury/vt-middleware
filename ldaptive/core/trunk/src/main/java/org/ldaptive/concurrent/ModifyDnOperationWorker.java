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
import org.ldaptive.ModifyDnOperation;
import org.ldaptive.ModifyDnRequest;

/**
 * Executes an ldap modify dn operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ModifyDnOperationWorker
  extends AbstractOperationWorker<ModifyDnRequest, Void>
{


  /**
   * Creates a new modify dn operation worker.
   *
   * @param  conn  connection
   */
  public ModifyDnOperationWorker(final Connection conn)
  {
    this(conn, null);
  }


  /**
   * Creates a new modify dn operation worker.
   *
   * @param  conn  connection
   * @param  es  executor service
   */
  public ModifyDnOperationWorker(
    final Connection conn, final ExecutorService es)
  {
    super(new ModifyDnOperation(conn), es);
  }
}
