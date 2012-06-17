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
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.Response;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.handler.LdapEntryHandler;
import org.ldaptive.handler.OperationResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for parallel search executors. A cached thread pool is used by
 * default.
 *
 * @param  <T>  type of connection factory
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class
AbstractParallelSearchExecutor<T extends ConnectionFactory>
  extends SearchRequest
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** to submit operations to. */
  private final ExecutorService service;

  /** Handlers to process search responses. */
  private OperationResponseHandler<SearchResult>[] searchResponseHandlers;


  /**
   * Creates a new abstract search executor.
   *
   * @param  es  executor service
   */
  public AbstractParallelSearchExecutor(final ExecutorService es)
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
  public OperationResponseHandler<SearchResult>[] getSearchResponseHandlers()
  {
    return searchResponseHandlers;
  }


  /**
   * Sets the search response handlers.
   *
   * @param  handlers  search response handlers
   */
  public void setSearchResponseHandlers(
    final OperationResponseHandler<SearchResult>... handlers)
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


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filters  to search with
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final T factory,
    final String... filters)
    throws LdapException
  {
    final SearchFilter[] sf = new SearchFilter[filters.length];
    for (int i = 0; i < filters.length; i++) {
      sf[i] = new SearchFilter(filters[i]);
    }
    return search(factory, sf, (String[]) null, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filters  to search with
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final T factory,
    final SearchFilter[] filters)
    throws LdapException
  {
    return search(factory, filters, (String[]) null, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filters  to search with
   * @param  attrs  to return
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final T factory,
    final String[] filters,
    final String... attrs)
    throws LdapException
  {
    final SearchFilter[] sf = new SearchFilter[filters.length];
    for (int i = 0; i < filters.length; i++) {
      sf[i] = new SearchFilter(filters[i]);
    }
    return search(factory, sf, attrs, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filters  to search with
   * @param  attrs  to return
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<SearchResult>> search(
    final T factory,
    final SearchFilter[] filters,
    final String... attrs)
    throws LdapException
  {
    return search(factory, filters, attrs, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filters  to search with
   * @param  attrs  to return
   * @param  handlers  entry handlers
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public abstract Collection<Response<SearchResult>> search(
    final T factory,
    final SearchFilter[] filters,
    final String[] attrs,
    final LdapEntryHandler... handlers)
    throws LdapException;
}
