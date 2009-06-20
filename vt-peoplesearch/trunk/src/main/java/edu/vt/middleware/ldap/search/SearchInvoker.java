/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.pool.LdapPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>SearchInvoker</code> queries a LDAP and attempts to find the best fit
 * results based on the query.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class SearchInvoker
{

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(SearchInvoker.class);

  /** Whether to proxy SASL authorization. */
  private boolean proxySaslAuthz;

  /** Search modules. */
  private Map<Integer, Search> searches = new HashMap<Integer, Search>();

  /** Retrieve ldap objects for searching. */
  private LdapPoolManager ldapPoolManager;


  /**
   * This returns whether to proxy sasl authorization.
   *
   * @return  <code>boolean</code>
   */
  public boolean getProxySaslAuthorization()
  {
    return this.proxySaslAuthz;
  }


  /**
   * Sets whether to proxy sasl authorization.
   *
   * @param  b  whether to proxy sasl authorization
   */
  public void setProxySaslAuthorization(final boolean b)
  {
    this.proxySaslAuthz = b;
  }


  /**
   * This returns the ldap pool manager.
   *
   * @return  <code>LdapPoolManager</code>
   */
  public LdapPoolManager getLdapPoolManager()
  {
    return this.ldapPoolManager;
  }


  /**
   * This sets the ldap pool manager.
   *
   * @param  lpm  <code>LdapPoolManager</code>
   */
  public void setLdapPoolManager(final LdapPoolManager lpm)
  {
    this.ldapPoolManager = lpm;
  }


  /**
   * This returns the search objects used to formulate queries.
   *
   * @return  <code>Map</code> of term count to search
   */
  public Map<Integer, Search> getSearches()
  {
    return this.searches;
  }


  /**
   * This returns the search objects used to formulate queries.
   *
   * @param  m  map of term count to search
   */
  public void setSearches(final Map<Integer, Search> m)
  {
    this.searches = m;
  }


  /**
   * Perform people finder fuzzy logic for the supplied query.
   *
   * @param  query  <code>Query</code> to search for
   *
   * @return  <code>Iterator</code>
   *
   * @throws  PeopleSearchException  if an error occurs
   */
  public Iterator<SearchResult> find(final Query query)
    throws PeopleSearchException
  {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Query: " + query);
    }

    // store the results of our ldap queries
    Iterator<SearchResult> queryResults = null;

    // get an ldap pool
    LdapPool<Ldap> pool = null;
    if (this.proxySaslAuthz) {
      final String saslAuthzId = query.getSaslAuthorizationId();
      if (saslAuthzId != null && !saslAuthzId.equals("")) {
        pool = this.ldapPoolManager.getLdapPool(saslAuthzId);
      } else {
        throw new PeopleSearchException("No SASL Authorization ID found.");
      }
    } else {
      pool = this.ldapPoolManager.getLdapPool();
    }

    // get a search object
    Search search = null;
    final QueryParser parser = new QueryParser(query.getLdapQuery());

    if (parser.isValidQuery()) {

      if (LOG.isDebugEnabled()) {
        LOG.debug("Processing valid query: " + parser.getQueryParams());
      }

      query.setQueryParameters(parser.getQueryParams().toArray(new String[0]));

      Integer termCount = new Integer(parser.queryCount());
      if (termCount.intValue() > this.searches.size()) {
        termCount = this.searches.size() - 1;
      }
      search = this.searches.get(termCount);
      if (LOG.isDebugEnabled()) {
        if (search != null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Found search module for query count of " + termCount);
          }
        } else {
          if (LOG.isDebugEnabled()) {
            LOG.debug("No search module found for query count of " + termCount);
          }
        }
      }
    }

    // perform search
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();
      if (search != null) {
        queryResults = search.search(ldap, query);
      }
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error occured while attempting query", e);
      }
      throw new PeopleSearchException(e.getMessage());
    } finally {
      pool.checkIn(ldap);
    }

    return queryResults;
  }
}
