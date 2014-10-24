/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.concurrent;

import java.util.concurrent.ExecutorService;
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
   * @param  op  modify dn operation to execute
   */
  public ModifyDnOperationWorker(final ModifyDnOperation op)
  {
    super(op);
  }


  /**
   * Creates a new modify dn operation worker.
   *
   * @param  op  modify dn operation to execute
   * @param  es  executor service
   */
  public ModifyDnOperationWorker(
    final ModifyDnOperation op,
    final ExecutorService es)
  {
    super(op, es);
  }
}
