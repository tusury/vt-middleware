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
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;

/**
 * Executes an ldap search operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchOperationWorker
  extends AbstractOperationWorker<SearchRequest, SearchResult>
{


  /**
   * Creates a new search operation worker.
   *
   * @param  op  search operation to execute
   */
  public SearchOperationWorker(final SearchOperation op)
  {
    super(op);
  }


  /**
   * Creates a new search operation worker.
   *
   * @param  op  search operation to execute
   * @param  es  executor service
   */
  public SearchOperationWorker(
    final SearchOperation op,
    final ExecutorService es)
  {
    super(op, es);
  }
}
