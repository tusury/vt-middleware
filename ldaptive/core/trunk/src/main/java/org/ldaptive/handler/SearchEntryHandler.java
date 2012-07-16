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

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.SearchEntry;
import org.ldaptive.SearchRequest;

/**
 * Provides post search processing of a search entry.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface SearchEntryHandler extends Handler<SearchRequest, SearchEntry>
{


  /**
   * Process an entry from an ldap search.
   *
   * @param  conn  the search was performed on
   * @param  request  used to perform the search
   * @param  entry  from a search result
   *
   * @return  handler result
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  HandlerResult<SearchEntry> process(
    Connection conn, SearchRequest request, SearchEntry entry)
    throws LdapException;
}
