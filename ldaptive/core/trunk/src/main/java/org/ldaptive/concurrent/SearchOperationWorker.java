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
import org.ldaptive.LdapResult;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.cache.Cache;

/**
 * Executes an ldap search operation on a separate thread.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchOperationWorker
  extends AbstractOperationWorker<SearchRequest, LdapResult>
{


  /**
   * Creates a new search operation worker.
   *
   * @param  conn  connection
   */
  public SearchOperationWorker(final Connection conn)
  {
    this(conn, null, null);
  }


  /**
   * Creates a new search operation worker.
   *
   * @param  conn  connection
   * @param  es  executor service
   */
  public SearchOperationWorker(final Connection conn, final ExecutorService es)
  {
    this(conn, null, es);
  }


  /**
   * Creates a new search operation worker.
   *
   * @param  conn  connection
   * @param  c  cache
   */
  public SearchOperationWorker(
    final Connection conn, final Cache<SearchRequest> c)
  {
    this(conn, c, null);
  }


  /**
   * Creates a new search operation worker.
   *
   * @param  conn  connection
   * @param  c  cache
   * @param  es  executor service
   */
  public SearchOperationWorker(
    final Connection conn,
    final Cache<SearchRequest> c,
    final ExecutorService es)
  {
    super(new SearchOperation(conn, c), es);
  }
}
