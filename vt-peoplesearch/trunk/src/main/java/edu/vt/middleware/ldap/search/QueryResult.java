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

import javax.naming.directory.SearchResult;

/**
 * <code>QueryResult</code> contains search results and related data.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class QueryResult
{

  /** LDAP search result. */
  private SearchResult searchResult;

  /** LDAP query used to get search result. */
  private String ldapQuery;

  /** Attributes returned with the ldap query. */
  private String[] queryAttributes;

  /** Number of terms in the ldap query. */
  private int termCount;

  /** Search iteration which found the search result. */
  private int searchIteration;

  /** Time in milliseconds it took the ldap query to return. */
  private long searchTime;


  /** Default constructor. */
  public QueryResult() {}


  /**
   * This creates a new <code>QueryResult</code> with the supplied search
   * result.
   *
   * @param  s  <code>SearchResult</code>
   */
  public QueryResult(final SearchResult s)
  {
    this.searchResult = s;
  }


  /**
   * This sets the search result for this <code>QueryResult</code>.
   *
   * @param  s  <code>SearchResult</code>
   */
  public void setSearchResult(final SearchResult s)
  {
    this.searchResult = s;
  }


  /**
   * This returns the search result for this <code>QueryResult</code>.
   *
   * @return  <code>SearchResult</code>
   */
  public SearchResult getSearchResult()
  {
    return this.searchResult;
  }


  /**
   * This sets the ldap query for this <code>QueryResult</code>.
   *
   * @param  s  <code>String</code>
   */
  public void setLdapQuery(final String s)
  {
    this.ldapQuery = s;
  }


  /**
   * This returns the ldap query for this <code>QueryResult</code>.
   *
   * @return  <code>String</code>
   */
  public String getLdapQuery()
  {
    return this.ldapQuery;
  }


  /**
   * This sets the query attributes for this <code>QueryResult</code>.
   *
   * @param  s  <code>String[]</code>
   */
  public void setQueryAttributes(final String[] s)
  {
    this.queryAttributes = s;
  }


  /**
   * This returns the query attributes for this <code>QueryResult</code>.
   *
   * @return  <code>String[]</code>
   */
  public String[] getQueryAttributes()
  {
    return this.queryAttributes;
  }


  /**
   * This sets the term count for this <code>QueryResult</code>.
   *
   * @param  i  <code>int</code>
   */
  public void setTermCount(final int i)
  {
    this.termCount = i;
  }


  /**
   * This returns the term count for this <code>QueryResult</code>.
   *
   * @return  <code>int</code>
   */
  public int getTermCount()
  {
    return this.termCount;
  }


  /**
   * This sets the search iteration for this <code>QueryResult</code>.
   *
   * @param  i  <code>int</code>
   */
  public void setSearchIteration(final int i)
  {
    this.searchIteration = i;
  }


  /**
   * This returns the search iteration for this <code>QueryResult</code>.
   *
   * @return  <code>int</code>
   */
  public int getSearchIteration()
  {
    return this.searchIteration;
  }


  /**
   * This sets the search time for this <code>QueryResult</code>.
   *
   * @param  l  <code>long</code>
   */
  public void setSearchTime(final long l)
  {
    this.searchTime = l;
  }


  /**
   * This returns the search time for this <code>QueryResult</code>.
   *
   * @return  <code>long</code>
   */
  public long getSearchTime()
  {
    return this.searchTime;
  }
}
