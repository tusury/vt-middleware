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
package org.ldaptive.handler;

import java.util.Arrays;
import java.util.Map;
import org.ldaptive.SearchRequest;

/**
 * Contains the attributes used to perform ldap searches.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchCriteria
{

  /** dn. */
  private String searchDn;

  /** filter. */
  private String searchFilter;

  /** filter parameters. */
  private Map<String, Object> filterParameters;

  /** return attributes. */
  private String[] returnAttrs;


  /** Default constructor. */
  public SearchCriteria() {}


  /**
   * Creates a new search criteria.
   *
   * @param  dn  to set
   */
  public SearchCriteria(final String dn)
  {
    searchDn = dn;
  }


  /**
   * Creates a new search criteria.
   *
   * @param  request  to set properties with
   */
  public SearchCriteria(final SearchRequest request)
  {
    setDn(request.getBaseDn());
    if (request.getSearchFilter() != null) {
      setFilter(request.getSearchFilter().getFilter());
      setFilterParameters(request.getSearchFilter().getParameters());
    }
    setReturnAttrs(request.getReturnAttributes());
  }


  /**
   * Gets the dn.
   *
   * @return  dn
   */
  public String getDn()
  {
    return searchDn;
  }


  /**
   * Sets the dn.
   *
   * @param  dn  to set
   */
  public void setDn(final String dn)
  {
    searchDn = dn;
  }


  /**
   * Gets the filter.
   *
   * @return  filter
   */
  public String getFilter()
  {
    return searchFilter;
  }


  /**
   * Sets the filter.
   *
   * @param  filter  to set
   */
  public void setFilter(final String filter)
  {
    searchFilter = filter;
  }


  /**
   * Gets the filter parameters.
   *
   * @return  filter parameters
   */
  public Map<String, Object> getFilterParameters()
  {
    return filterParameters;
  }


  /**
   * Sets the filter parameters.
   *
   * @param  params  to set filter parameters
   */
  public void setFilterParameters(final Map<String, Object> params)
  {
    filterParameters = params;
  }


  /**
   * Gets the return attributes.
   *
   * @return  return attributes
   */
  public String[] getReturnAttrs()
  {
    return returnAttrs;
  }


  /**
   * Sets the return attributes.
   *
   * @param  attrs  to set return attributes
   */
  public void setReturnAttrs(final String[] attrs)
  {
    returnAttrs = attrs;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::dn=%s, filter=%s, filterParameters=%s, returnAttrs=%s]",
        getClass().getName(),
        hashCode(),
        searchDn,
        searchFilter,
        filterParameters,
        Arrays.toString(returnAttrs));
  }
}
