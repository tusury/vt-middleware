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
package org.ldaptive;

import org.ldaptive.cache.Cache;
import org.ldaptive.handler.ExtendedLdapEntryHandler;
import org.ldaptive.handler.HandlerResult;
import org.ldaptive.handler.LdapEntryHandler;

/**
 * Provides common implementation for search operations.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractSearchOperation<Q extends SearchRequest>
  extends AbstractOperation<Q, LdapResult>
{

  /** Cache to use when performing searches. */
  private Cache<Q> cache;


  /**
   * Creates a new abstract search connection.
   *
   * @param  conn  to use for this operation
   * @param  c  cache
   */
  public AbstractSearchOperation(final Connection conn, final Cache<Q> c)
  {
    super(conn);
    cache = c;
  }


  /**
   * Returns the cache to check when performing search operations. When a cache
   * is provided it will be populated as new searches are performed and used
   * when a search request hits the cache.
   *
   * @return  cache
   */
  public Cache<Q> getCache()
  {
    return cache;
  }


  /**
   * Sets the cache.
   *
   * @param  c  cache to set
   */
  public void setCache(final Cache<Q> c)
  {
    cache = c;
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(final Q request, final ConnectionConfig cc)
  {
    initializeLdapEntryHandlers(request, getConnection());
  }


  /**
   * Initializes those ldap entry handlers that require access to the ldap
   * connection.
   *
   * @param  request  to read entry handlers from
   * @param  c  to provide to entry handlers
   */
  protected void initializeLdapEntryHandlers(
    final Q request,
    final Connection c)
  {
    final LdapEntryHandler[] handlers = request.getLdapEntryHandlers();
    if (handlers != null && handlers.length > 0) {
      for (LdapEntryHandler h : handlers) {
        if (ExtendedLdapEntryHandler.class.isInstance(h)) {
          ((ExtendedLdapEntryHandler) h).setResultConnection(c);
        }
      }
    }
  }


  /**
   * Performs the ldap search.
   *
   * @param  request  to invoke search with
   *
   * @return  ldap result
   *
   * @throws  LdapException  if an error occurs
   */
  protected abstract Response<LdapResult> executeSearch(final Q request)
    throws LdapException;


  /** {@inheritDoc} */
  @Override
  protected Response<LdapResult> invoke(final Q request)
    throws LdapException
  {
    logger.debug("invoke request={}", request);

    Response<LdapResult> response;
    if (cache != null) {
      final LdapResult lr = cache.get(request);
      if (lr == null) {
        response = executeSearch(request);
        cache.put(request, response.getResult());
        logger.debug("invoke stored result={} in cache", response.getResult());
      } else {
        logger.debug("invoke found result={} in cache", lr);
        response = new Response<LdapResult>(lr, null);
      }
    } else {
      response = executeSearch(request);
    }
    logger.debug("invoke response={} for request={}", response, request);
    return response;
  }


  /**
   * Processes each ldap entry handler after a search has been performed.
   * Returns a handler result containing an ldap entry processed by all
   * handlers. If any handler indicates that the search should be aborted, that
   * flag is returned to the search operation after all handlers have been
   * invoked.
   *
   * @param  request  the search was performed with
   * @param  entry  from a search
   *
   * @return  handler result
   *
   * @throws  LdapException  if an error occurs processing a handler
   */
  protected HandlerResult executeLdapEntryHandlers(
    final SearchRequest request,
    final LdapEntry entry)
    throws LdapException
  {
    LdapEntry processedEntry = entry;
    boolean abort = false;
    final LdapEntryHandler[] handlers = request.getLdapEntryHandlers();
    if (handlers != null && handlers.length > 0) {
      for (LdapEntryHandler handler : handlers) {
        if (handler != null) {
          final HandlerResult hr = handler.process(request, processedEntry);
          if (hr.getAbortSearch()) {
            abort = true;
          }
          processedEntry = hr.getLdapEntry();
        }
      }
    }
    return new HandlerResult(processedEntry, abort);
  }
}
