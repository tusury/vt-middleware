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
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>QueryParser</code> contains methods for determining the properties of a
 * query.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class QueryParser
{

  /** List of query parameters. */
  private List<String> queryParams = new ArrayList<String>();


  /**
   * This creates a new <code>QuerySearch</code>.
   *
   * @param  query  <code>String</code> to parse
   */
  public QueryParser(final String query)
  {
    if (query != null) {
      final StringTokenizer queryTokens = new StringTokenizer(
        query.toLowerCase().trim());
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

        this.queryParams.add(token);
      }
    }
  }


  /**
   * This returns whether the <code>QueryParser</code> contains a valid query.
   *
   * @return  <code>boolean</code>
   */
  public boolean isValidQuery()
  {
    boolean result = false;
    if (!this.queryParams.isEmpty()) {
      result = true;
    }
    return result;
  }


  /**
   * This returns the number of query params for this <code>QueryParser</code>.
   *
   * @return  <code>int</code>
   */
  public int queryCount()
  {
    return this.queryParams.size();
  }


  /**
   * This returns the query parameters.
   *
   * @return  <code>List</code> - of query parameters
   */
  public List<String> getQueryParams()
  {
    return this.queryParams;
  }
}
