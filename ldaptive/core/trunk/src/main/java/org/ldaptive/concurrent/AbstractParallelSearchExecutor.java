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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.LdapResult;
import org.ldaptive.Response;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.handler.LdapEntryHandler;

/**
 * Base class for parallel search executors. If no {@link ExecutorService} is
 * provided a cached thread pool is used by default.
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

  /** to submit operations to. */
  private final ExecutorService service;


  /**
   * Creates a new abstract search executor.
   *
   * @param  es  executor service
   */
  public AbstractParallelSearchExecutor(final ExecutorService es)
  {
    if (es != null) {
      service = es;
    } else {
      service = Executors.newCachedThreadPool();
    }
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
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filters  to search with
   *
   * @return  search results
   *
   * @throws  LdapException  if the search fails
   */
  public Collection<Response<LdapResult>> search(
    final T factory, final String... filters)
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
  public Collection<Response<LdapResult>> search(
    final T factory, final SearchFilter[] filters)
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
  public Collection<Response<LdapResult>> search(
    final T factory, final String[] filters, final String... attrs)
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
  public Collection<Response<LdapResult>> search(
    final T factory, final SearchFilter[] filters, final String... attrs)
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
  public abstract Collection<Response<LdapResult>> search(
    final T factory,
    final SearchFilter[] filters,
    final String[] attrs,
    final LdapEntryHandler... handlers)
    throws LdapException;
}
