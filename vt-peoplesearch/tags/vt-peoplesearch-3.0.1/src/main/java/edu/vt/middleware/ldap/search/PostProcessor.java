/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.search;

import javax.naming.NamingException;

/**
 * <code>PostProcesser</code> provides methods for processing query results
 * before they are sent back to clients.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface PostProcessor
{


  /**
   * This performs post processing of ldap results.
   *
   * @param  queryResult  <code>QueryResult</code>
   *
   * @throws  NamingException  if an error occurs using the search result
   */
  void processResult(QueryResult queryResult)
    throws NamingException;
}
