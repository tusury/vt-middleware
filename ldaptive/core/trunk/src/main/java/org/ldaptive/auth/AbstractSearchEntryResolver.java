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
package org.ldaptive.auth;

import java.util.Arrays;
import org.ldaptive.Connection;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for search entry resolvers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractSearchEntryResolver implements EntryResolver
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** User attributes to return. */
  private String[] retAttrs;


  /**
   * Returns the return attributes.
   *
   * @return  attributes to return
   */
  public String[] getReturnAttributes()
  {
    return retAttrs;
  }


  /**
   * Sets the return attributes.
   *
   * @param  attrs  to return
   */
  public void setReturnAttributes(final String... attrs)
  {
    retAttrs = attrs;
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
   */
  protected SearchRequest createSearchRequest(
    final AuthenticationCriteria ac,
    final String[] returnAttributes)
  {
    return
      SearchRequest.newObjectScopeSearchRequest(ac.getDn(), returnAttributes);
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry resolve(
    final Connection conn,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    logger.debug(
      "resolve criteria={} with attributes={}",
      ac,
      retAttrs == null ? "<all attributes>" : Arrays.toString(retAttrs));

    final SearchResult result = performLdapSearch(conn, ac);
    logger.debug(
      "resolved result={} for criteria={} with attributes={}",
      new Object[] {
        result,
        ac,
        retAttrs == null ? "<all attributes>" : Arrays.toString(retAttrs),
      });
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
