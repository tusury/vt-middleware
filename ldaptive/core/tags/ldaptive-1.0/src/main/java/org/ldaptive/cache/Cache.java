/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.cache;

import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;

/**
 * Interface for cache implementations.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Cache<Q extends SearchRequest>
{


  /**
   * Returns the ldap result for the supplied request.
   *
   * @param  request  to find ldap result with
   *
   * @return  ldap result
   */
  SearchResult get(Q request);


  /**
   * Stores the ldap result for the supplied request.
   *
   * @param  request  used to find ldap result
   * @param  result  found with request
   */
  void put(Q request, SearchResult result);
}
