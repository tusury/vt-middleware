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

import java.util.Arrays;

/**
 * <code>Query</code> contains query related data.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Query
{

  /** LDAP query to execute. */
  private String ldapQuery;

  /** Attributes to return with the ldap query. */
  private String[] queryAttributes;

  /** LDAP query separated into parameters. */
  private String[] queryParameters;

  /** Additional restrictions to place on every query. */
  private String searchRestrictions;

  /** Start index of search results to return. */
  private Integer fromResult;

  /** End index of search results to return. */
  private Integer toResult;

  /** ID to use for SASL authorization. */
  private String saslAuthorizationId;


  /** Default constructor. */
  public Query() {}


  /**
   * This sets the ldap query for this <code>Query</code>.
   *
   * @param  s  <code>String</code>
   */
  public void setLdapQuery(final String s)
  {
    this.ldapQuery = s;
  }


  /**
   * This returns the ldap query for this <code>Query</code>.
   *
   * @return  <code>String</code>
   */
  public String getLdapQuery()
  {
    return this.ldapQuery;
  }


  /**
   * This sets the query attributes for this <code>Query</code>.
   *
   * @param  s  <code>String[]</code>
   */
  public void setQueryAttributes(final String[] s)
  {
    this.queryAttributes = s;
  }


  /**
   * This returns the query attributes for this <code>Query</code>.
   *
   * @return  <code>String[]</code>
   */
  public String[] getQueryAttributes()
  {
    return this.queryAttributes;
  }


  /**
   * This sets the query parameters for this <code>Query</code>.
   *
   * @param  s  <code>String[]</code>
   */
  public void setQueryParameters(final String[] s)
  {
    this.queryParameters = s;
  }


  /**
   * This returns the query parameters for this <code>Query</code>.
   *
   * @return  <code>String[]</code>
   */
  public String[] getQueryParameters()
  {
    return this.queryParameters;
  }


  /**
   * This sets the search restrictions for this <code>Query</code>.
   *
   * @param  s  <code>String</code>
   */
  public void setSearchRestrictions(final String s)
  {
    this.searchRestrictions = s;
  }


  /**
   * This returns the search restrictions for this <code>Query</code>.
   *
   * @return  <code>String</code>
   */
  public String getSearchRestrictions()
  {
    return this.searchRestrictions;
  }


  /**
   * This sets the from result for this <code>Query</code>.
   *
   * @param  i  <code>Integer</code>
   */
  public void setFromResult(final Integer i)
  {
    this.fromResult = i;
  }


  /**
   * This returns the from result for this <code>Query</code>.
   *
   * @return  <code>Integer</code>
   */
  public Integer getFromResult()
  {
    return this.fromResult;
  }


  /**
   * This sets the to result for this <code>Query</code>.
   *
   * @param  i  <code>Integer</code>
   */
  public void setToResult(final Integer i)
  {
    this.toResult = i;
  }


  /**
   * This returns the to result for this <code>Query</code>.
   *
   * @return  <code>Integer</code>
   */
  public Integer getToResult()
  {
    return this.toResult;
  }


  /**
   * This sets the SASL authorization id for this <code>Query</code>.
   *
   * @param  id  <code>String</code>
   */
  public void setSaslAuthorizationId(final String id)
  {
    this.saslAuthorizationId = id;
  }


  /**
   * This returns the SASL authorization id for this <code>Query</code>.
   *
   * @return  <code>String</code>
   */
  public String getSaslAuthorizationId()
  {
    return this.saslAuthorizationId;
  }


  /**
   * This returns a <code>String</code> representation of this <code>
   * Query</code>.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "{query=%s,attributes=%s,parameters=%s,searchRestrictions=%s," +
        "fromResult=%d,toResult=%d,saslAuthorizationId=%s}",
        this.ldapQuery,
        this.queryAttributes == null ? null
                                     : Arrays.asList(this.queryAttributes),
        this.queryParameters == null ? null
                                     : Arrays.asList(this.queryParameters),
        this.searchRestrictions,
        this.fromResult,
        this.toResult,
        this.saslAuthorizationId);
  }
}
