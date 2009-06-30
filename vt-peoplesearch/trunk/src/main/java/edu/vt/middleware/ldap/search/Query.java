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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>Query</code> contains query related data.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Query
{

  /** LDAP query to execute. */
  private String rawQuery;

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
   * This sets the raw query for this <code>Query</code>.
   *
   * @param  s  <code>String</code>
   */
  public void setRawQuery(final String s)
  {
    this.rawQuery = s;
  }


  /**
   * This returns the raw query for this <code>Query</code>.
   *
   * @return  <code>String</code>
   */
  public String getRawQuery()
  {
    return this.rawQuery;
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
   * This parses the raw query from a string into an array of separate
   * parameters.
   *
   * @return  <code>String[]</code>
   */
  public String[] getQueryParameters()
  {
    if (this.queryParameters == null) {
      if (this.rawQuery != null) {
        final List<String> params = new ArrayList<String>();
        final StringTokenizer queryTokens = new StringTokenizer(
          this.rawQuery.toLowerCase().trim());
        while (queryTokens.hasMoreTokens()) {
          String token = queryTokens.nextToken();

          // don't allow an odd number of trailing backslashes, it breaks regex
          int i = token.length() - 1;
          int slashCount = 0;
          while (i >= 0 && token.charAt(i--) == '\\') {
            slashCount++;
          }
          if (slashCount % 2 == 1) {
            token = token.concat("\\");
          }

          params.add(token);
        }
        this.queryParameters = params.toArray(new String[0]);
      } else {
        this.queryParameters = new String[0];
      }
    }

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
        this.rawQuery,
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
