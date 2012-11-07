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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.Response;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.handler.OperationResponseHandler;
import org.ldaptive.handler.SearchEntryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for aggregate search executors.
 *
 * @param  <T>  type of connection factory
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class
AbstractAggregateSearchExecutor<T extends ConnectionFactory>
  extends SearchRequest
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** to submit operations to. */
  private final ExecutorService service;

  /** Handlers to process search responses. */
  private OperationResponseHandler<SearchRequest, SearchResult>[]
  searchResponseHandlers;


  /**
   * Creates a new abstract aggregate search executor.
   *
   * @param  es  executor service
   */
  public AbstractAggregateSearchExecutor(final ExecutorService es)
  {
    if (es == null) {
      throw new NullPointerException("ExecutorService cannot be null");
    }
    service = es;
  }


  /**
   * Returns the executor service for this search executor.
   *
   * @return  executor service
   */
  protected ExecutorService getExecutorService()
  {
    return service;
  }


  /**
   * Returns the search response handlers.
   *
   * @return  search response handlers
   */
  public OperationResponseHandler<SearchRequest, SearchResult>[]
  getSearchResponseHandlers()
  {
    return searchResponseHandlers;
  }


  /**
   * Sets the search response handlers.
   *
   * @param  handlers  search response handlers
   */
  public void setSearchResponseHandlers(
    final OperationResponseHandler<SearchRequest, SearchResult>... handlers)
  {
    searchResponseHandlers = handlers;
  }


  /**
   * Shuts down the executor service. See {@link ExecutorService#shutdown()}.
   */
  public void shutdown()
  {
    service.shutdown();
  }


  /**
   * Immediately shuts down the executor service. See {@link
   * ExecutorService#shutdownNow()}.
   *
   * @return  list of tasks that never executed
   */
  public List<Runnable> shutdownNow()
  {
    return service.shutdownNow();
  }


  /** {@inheritDoc} */
  @Override
  protected void finalize()
    throws Throwable
  {
    try {
      shutdown();
    } finally {
      super.finalize();
    }
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::service=%s, searchResponseHandlers=%s]",
        getClass().getName(),
        hashCode(),
        service,
        Arrays.toString(searchResponseHandlers));
  }


  /**
   * Performs a search operation with the supplied connection factories.
   *
   * @param  factories  to get a connection from
   * @param  filters  to search with
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final ConnectionFactory[] factories,
    final String... filters)
    throws LdapException
  {
    final SearchFilter[] sf = new SearchFilter[filters.length];
    for (int i = 0; i < filters.length; i++) {
      sf[i] = new SearchFilter(filters[i]);
    }
    return search(factories, sf, (String[]) null, (SearchEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factories.
   *
   * @param  factories  to get a connection from
   * @param  filters  to search with
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final ConnectionFactory[] factories,
    final SearchFilter[] filters)
    throws LdapException
  {
    return
      search(factories, filters, (String[]) null, (SearchEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factories.
   *
   * @param  factories  to get a connection from
   * @param  filters  to search with
   * @param  attrs  to return
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final ConnectionFactory[] factories,
    final String[] filters,
    final String... attrs)
    throws LdapException
  {
    final SearchFilter[] sf = new SearchFilter[filters.length];
    for (int i = 0; i < filters.length; i++) {
      sf[i] = new SearchFilter(filters[i]);
    }
    return search(factories, sf, attrs, (SearchEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factories.
   *
   * @param  factories  to get a connection from
   * @param  filters  to search with
   * @param  attrs  to return
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final ConnectionFactory[] factories,
    final SearchFilter[] filters,
    final String... attrs)
    throws LdapException
  {
    return search(factories, filters, attrs, (SearchEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factories.
   *
   * @param  factories  to get a connection from
   * @param  filters  to search with
   * @param  attrs  to return
   * @param  handlers  entry handlers
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public abstract Collection<Response<SearchResult>> search(
    final ConnectionFactory[] factories,
    final SearchFilter[] filters,
    final String[] attrs,
    final SearchEntryHandler... handlers)
    throws LdapException;
}
