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
import org.ldaptive.handler.HandlerResult;
import org.ldaptive.intermediate.IntermediateResponse;
import org.ldaptive.provider.SearchItem;
import org.ldaptive.provider.SearchIterator;

/**
 * Provides common implementation for search operations.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractSearchOperation<Q extends SearchRequest>
  extends AbstractOperation<Q, SearchResult>
{

  /** Cache to use when performing searches. */
  private Cache<Q> cache;


  /**
   * Creates a new abstract search operation.
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


  /**
   * Performs the ldap search.
   *
   * @param  request  to invoke search with
   *
   * @return  ldap response
   *
   * @throws  LdapException  if an error occurs
   */
  protected abstract Response<SearchResult> executeSearch(final Q request)
    throws LdapException;


  /** {@inheritDoc} */
  @Override
  protected Response<SearchResult> invoke(final Q request)
    throws LdapException
  {
    logger.debug("invoke request={}", request);

    Response<SearchResult> response;
    if (cache != null) {
      final SearchResult sr = cache.get(request);
      if (sr == null) {
        response = executeSearch(request);
        cache.put(request, response.getResult());
        logger.debug("invoke stored result={} in cache", response.getResult());
      } else {
        logger.debug("invoke found result={} in cache", sr);
        response = new Response<SearchResult>(sr, null);
      }
    } else {
      response = executeSearch(request);
    }
    logger.debug("invoke response={} for request={}", response, request);
    return response;
  }


  /**
   * Invokes the provider search operation and iterates over the results.
   * Invokes handlers as necessary for each result type.
   *
   * @param  request  used to create the search iterator
   * @param  si  search iterator
   *
   * @return  search result
   *
   * @throws  LdapException  if an error occurs
   */
  protected SearchResult readResult(final Q request, final SearchIterator si)
    throws LdapException
  {
    final SearchResult result = new SearchResult(request.getSortBehavior());
    try {
      while (si.hasNext()) {
        final SearchItem item = si.next();
        if (item.isSearchEntry()) {
          final SearchEntry se = item.getSearchEntry();
          if (se != null) {
            final HandlerResult<SearchEntry> hr = executeHandlers(
              request.getSearchEntryHandlers(), request, se);
            if (hr.getResult() != null) {
              result.addEntry(hr.getResult());
            }
            if (hr.getAbort()) {
              logger.debug("Aborting search on entry=%s", se);
              break;
            }
          }
        } else if (item.isSearchReference()) {
          final SearchReference sr = item.getSearchReference();
          if (sr != null) {
            final HandlerResult<SearchReference> hr = executeHandlers(
              request.getSearchReferenceHandlers(), request, sr);
            if (hr.getResult() != null) {
              result.addReference(hr.getResult());
            }
            if (hr.getAbort()) {
              logger.debug("Aborting search on reference=%s", sr);
              break;
            }
          }
        } else if (item.isIntermediateResponse()) {
          final IntermediateResponse ir = item.getIntermediateResponse();
          if (ir != null) {
            final HandlerResult<IntermediateResponse> hr = executeHandlers(
              request.getIntermediateResponseHandlers(), request, ir);
            if (hr.getAbort()) {
              logger.debug("Aborting search on intermediate response=%s", ir);
              break;
            }
          }
        }
      }
    } finally {
      si.close();
    }
    return result;
  }
}
