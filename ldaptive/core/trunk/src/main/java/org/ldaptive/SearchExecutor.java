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

import org.ldaptive.handler.LdapEntryHandler;
import org.ldaptive.handler.OperationResponseHandler;

/**
 * Helper class which encapsulates the try, finally idiom used to execute a
 * {@link SearchOperation}. This is a convenience class for searching if you
 * don't need to manage individual connections. In addition, this class provides
 * a way to hold common search request properties constant while changing
 * properties that tend to be more dynamic.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchExecutor extends SearchRequest
{

  /** Handlers to process search responses. */
  private OperationResponseHandler<LdapResult>[] searchResponseHandlers;


  /**
   * Returns the search response handlers.
   *
   * @return  search response handlers
   */
  public OperationResponseHandler<LdapResult>[] getSearchResponseHandlers()
  {
    return searchResponseHandlers;
  }


  /**
   * Sets the search response handlers.
   *
   * @param  handlers  search response handlers
   */
  public void setSearchResponseHandlers(
    final OperationResponseHandler<LdapResult>... handlers)
  {
    searchResponseHandlers = handlers;
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   *
   * @return  search result
   *
   * @throws  LdapException  if the search fails
   */
  public Response<LdapResult> search(final ConnectionFactory factory)
    throws LdapException
  {
    return search(factory, null, (String[]) null, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filter  to search with
   *
   * @return  search result
   *
   * @throws  LdapException  if the search fails
   */
  public Response<LdapResult> search(
    final ConnectionFactory factory, final String filter)
    throws LdapException
  {
    return search(
      factory,
      new SearchFilter(filter),
      (String[]) null,
      (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filter  to search with
   *
   * @return  search result
   *
   * @throws  LdapException  if the search fails
   */
  public Response<LdapResult> search(
    final ConnectionFactory factory, final SearchFilter filter)
    throws LdapException
  {
    return search(factory, filter, (String[]) null, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filter  to search with
   * @param  attrs  to return
   *
   * @return  search result
   *
   * @throws  LdapException  if the search fails
   */
  public Response<LdapResult> search(
    final ConnectionFactory factory, final String filter, final String... attrs)
    throws LdapException
  {
    return search(
      factory, new SearchFilter(filter), attrs, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filter  to search with
   * @param  attrs  to return
   *
   * @return  search result
   *
   * @throws  LdapException  if the search fails
   */
  public Response<LdapResult> search(
    final ConnectionFactory factory,
    final SearchFilter filter,
    final String... attrs)
    throws LdapException
  {
    return search(factory, filter, attrs, (LdapEntryHandler[]) null);
  }


  /**
   * Performs a search operation with the supplied connection factory.
   *
   * @param  factory  to get a connection from
   * @param  filter  to search with
   * @param  attrs  to return
   * @param  handlers  entry handlers
   *
   * @return  search result
   *
   * @throws  LdapException  if the search fails
   */
  public Response<LdapResult> search(
    final ConnectionFactory factory,
    final SearchFilter filter,
    final String[] attrs,
    final LdapEntryHandler... handlers)
    throws LdapException
  {
    Response<LdapResult> response = null;
    final Connection conn = factory.getConnection();
    try {
      conn.open();
      final SearchOperation op = new SearchOperation(conn);
      op.setOperationResponseHandlers(searchResponseHandlers);
      final SearchRequest sr = newSearchRequest(this);
      if (filter != null) {
        sr.setSearchFilter(filter);
      }
      if (attrs != null) {
        sr.setReturnAttributes(attrs);
      }
      if (handlers != null) {
        sr.setLdapEntryHandlers(handlers);
      }
      response = op.execute(sr);
    } finally {
      conn.close();
    }
    return response;
  }
}
