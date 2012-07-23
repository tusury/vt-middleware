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
import org.ldaptive.provider.SearchIterator;

/**
 * Executes an ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchOperation extends AbstractSearchOperation<SearchRequest>
{

  /** Cache to use when performing searches. */
  private Cache<SearchRequest> cache;


  /**
   * Creates a new search operation.
   *
   * @param  conn  connection
   */
  public SearchOperation(final Connection conn)
  {
    super(conn);
  }


  /**
   * Creates a new search operation.
   *
   * @param  conn  connection
   * @param  c  cache
   */
  public SearchOperation(final Connection conn, final Cache<SearchRequest> c)
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
  public Cache<SearchRequest> getCache()
  {
    return cache;
  }


  /**
   * Sets the cache.
   *
   * @param  c  cache to set
   */
  public void setCache(final Cache<SearchRequest> c)
  {
    cache = c;
  }


  /** {@inheritDoc} */
  @Override
  protected Response<SearchResult> invoke(final SearchRequest request)
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
   * Performs the ldap search.
   *
   * @param  request  to invoke search with
   *
   * @return  ldap response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<SearchResult> executeSearch(final SearchRequest request)
    throws LdapException
  {
    final SearchIterator si = getConnection().getProviderConnection().search(
      request);
    final SearchResult result = readResult(request, si);
    final Response<Void> response = si.getResponse();
    return
      new Response<SearchResult>(
        result,
        response.getResultCode(),
        response.getMessage(),
        response.getMatchedDn(),
        response.getControls(),
        response.getReferralURLs(),
        response.getMessageId());
  }
}
