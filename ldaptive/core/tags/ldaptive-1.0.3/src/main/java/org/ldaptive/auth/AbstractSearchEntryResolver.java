/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.auth;

import org.ldaptive.Connection;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.handler.SearchEntryHandler;

/**
 * Base implementation for search entry resolvers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractSearchEntryResolver
  extends AbstractSearchOperationFactory implements EntryResolver
{

  /** @deprecated  User attributes to return. */
  @Deprecated
  private String[] retAttrs;

  /** Ldap entry handlers. */
  private SearchEntryHandler[] entryHandlers;


  /**
   * Returns the return attributes.
   *
   * @return  attributes to return
   *
   * @deprecated  return attributes retrieved from the authentication request
   */
  @Deprecated
  public String[] getReturnAttributes()
  {
    return retAttrs;
  }


  /**
   * Sets the return attributes.
   *
   * @param  attrs  to return
   *
   * @deprecated  return attributes retrieved from the authentication request
   */
  @Deprecated
  public void setReturnAttributes(final String... attrs)
  {
    retAttrs = attrs;
  }


  /**
   * Returns the search entry handlers.
   *
   * @return  search entry handlers
   */
  public SearchEntryHandler[] getSearchEntryHandlers()
  {
    return entryHandlers;
  }


  /**
   * Sets the search entry handlers.
   *
   * @param  handlers  search entry handlers
   */
  public void setSearchEntryHandlers(final SearchEntryHandler... handlers)
  {
    entryHandlers = handlers;
  }


  /**
   * Executes an ldap search with the supplied authentication criteria.
   *
   * @param  conn  that the user attempted to bind on
   * @param  ac  authentication criteria associated with the user
   *
   * @return  search result
   *
   * @throws  LdapException  if an error occurs attempting the search
   */
  protected abstract SearchResult performLdapSearch(
    final Connection conn,
    final AuthenticationCriteria ac)
    throws LdapException;


  /**
   * Returns a search request for an object level search on the DN found in the
   * authentication criteria.
   *
   * @param  ac  authentication criteria containing a DN
   * @param  returnAttributes  to request
   *
   * @return  search request
   *
   * @deprecated  use {@link #createSearchRequest(AuthenticationCriteria)}
   */
  @Deprecated
  protected SearchRequest createSearchRequest(
    final AuthenticationCriteria ac,
    final String[] returnAttributes)
  {
    final SearchRequest sr = SearchRequest.newObjectScopeSearchRequest(
      ac.getDn(),
      returnAttributes);
    sr.setSearchEntryHandlers(entryHandlers);
    return sr;
  }


  /**
   * Returns a search request for an object level search on the DN found in the
   * authentication criteria.
   *
   * @param  ac  authentication criteria containing a DN
   *
   * @return  search request
   */
  protected SearchRequest createSearchRequest(final AuthenticationCriteria ac)
  {
    final SearchRequest sr = SearchRequest.newObjectScopeSearchRequest(
      ac.getDn(),
      ac.getAuthenticationRequest().getReturnAttributes());
    sr.setSearchEntryHandlers(entryHandlers);
    return sr;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry resolve(
    final Connection conn,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    logger.debug("resolve criteria={}", ac);

    final SearchResult result = performLdapSearch(conn, ac);
    logger.debug("resolved result={} for criteria={}", result, ac);
    return resolveEntry(result);
  }


  /**
   * Returns the entry for the supplied search result.
   *
   * @param  result  to retrieve the entry from
   *
   * @return  ldap entry
   */
  protected LdapEntry resolveEntry(final SearchResult result)
  {
    return result.getEntry();
  }
}
