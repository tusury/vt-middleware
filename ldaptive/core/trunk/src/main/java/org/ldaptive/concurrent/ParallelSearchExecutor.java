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

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.LdapResult;
import org.ldaptive.Response;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.handler.LdapEntryHandler;

/**
 * Executes a list of search filters in parallel. This implementation executes
 * each search on the same connection in separate threads. If you need parallel
 * searches over a pool of connections see {@link ParallelPooledSearchExecutor}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ParallelSearchExecutor
  extends AbstractParallelSearchExecutor<ConnectionFactory>
{

  /**
   * Default constructor.
   */
  public ParallelSearchExecutor()
  {
    this(null);
  }


  /**
   * Creates a new parallel search executor.
   *
   * @param  es  executor service
   */
  public ParallelSearchExecutor(final ExecutorService es)
  {
    super(es);
  }


  /** {@inheritDoc} */
  @Override
  public Collection<Response<LdapResult>> search(
    final ConnectionFactory factory,
    final SearchFilter[] filters,
    final String[] attrs,
    final LdapEntryHandler... handlers)
    throws LdapException
  {
    Collection<Response<LdapResult>> response = null;
    final Connection conn = factory.getConnection();
    try {
      conn.open();
      final SearchOperationWorker op = new SearchOperationWorker(
        conn, getExecutorService());
      final SearchRequest[] sr = new SearchRequest[filters.length];
      for (int i = 0; i < filters.length; i++) {
        sr[i] = newSearchRequest(this);
        if (filters[i] != null) {
          sr[i].setSearchFilter(filters[i]);
        }
        if (attrs != null) {
          sr[i].setReturnAttributes(attrs);
        }
        if (handlers != null) {
          sr[i].setLdapEntryHandlers(handlers);
        }
      }
      try {
        response = op.executeToCompletion(sr);
      } catch (ExecutionException e) {
        throw new LdapException(e);
      } catch (InterruptedException e) {
        throw new LdapException(e);
      }
    } finally {
      conn.close();
    }
    return response;
  }
}
