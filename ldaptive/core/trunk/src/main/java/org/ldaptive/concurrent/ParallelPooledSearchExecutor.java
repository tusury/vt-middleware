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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.LdapResult;
import org.ldaptive.Response;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.handler.LdapEntryHandler;
import org.ldaptive.pool.PooledConnectionFactory;

/**
 * Executes a list of search filters in parallel, each search is performed on a
 * separate connection in the pool. If you need to execute all searches on the
 * same connection see {@link ParallelSearchExecutor}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ParallelPooledSearchExecutor
  extends AbstractParallelSearchExecutor<PooledConnectionFactory>
{


  /**
   * Default constructor.
   */
  public ParallelPooledSearchExecutor()
  {
    this(null);
  }


  /**
   * Creates a new parallel pooled search executor.
   *
   * @param  es  executor service
   */
  public ParallelPooledSearchExecutor(final ExecutorService es)
  {
    super(es);
  }


  /** {@inheritDoc} */
  @Override
  public Collection<Response<LdapResult>> search(
    final PooledConnectionFactory factory,
    final SearchFilter[] filters,
    final String[] attrs,
    final LdapEntryHandler... handlers)
    throws LdapException
  {
    final List<Response<LdapResult>> response =
      new LinkedList<Response<LdapResult>>();
    final Map<Future<Response<LdapResult>>, Connection> conns =
      new HashMap<Future<Response<LdapResult>>, Connection>(filters.length);
    final CompletionService<Response<LdapResult>> cs =
      new ExecutorCompletionService<Response<LdapResult>>(getExecutorService());
    try {
      for (int i = 0; i < filters.length; i++) {
        final Connection conn = factory.getConnection();
        conn.open();
        final SearchRequest sr = newSearchRequest(this);
        if (filters[i] != null) {
          sr.setSearchFilter(filters[i]);
        }
        if (attrs != null) {
          sr.setReturnAttributes(attrs);
        }
        if (handlers != null) {
          sr.setLdapEntryHandlers(handlers);
        }
        conns.put(
          cs.submit(
            SearchOperationWorker.createCallable(
              new SearchOperation(conn), sr)),
          conn);
      }
      for (int i = 0; i < filters.length; i++) {
        try {
          final Future<Response<LdapResult>> future = cs.take();
          response.add(future.get());
          conns.remove(future).close();
        } catch (ExecutionException e) {
          throw new LdapException(e);
        } catch (InterruptedException e) {
          throw new LdapException(e);
        }
      }
    } finally {
      if (!conns.isEmpty()) {
        for (Map.Entry<Future<Response<LdapResult>>, Connection> entry :
          conns.entrySet()) {
          entry.getValue().close();
        }
      }
    }
    return response;
  }
}
