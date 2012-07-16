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
import org.ldaptive.SearchReference;
import org.ldaptive.SearchRequest;

/**
 * Provides post search processing of a search reference.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface SearchReferenceHandler
  extends Handler<SearchRequest, SearchReference>
{


  /**
   * Process a search reference from an ldap search.
   *
   * @param  conn  the search was performed on
   * @param  request  used to perform the search
   * @param  reference  from a search result
   *
   * @return  handler result
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  HandlerResult<SearchReference> process(
    Connection conn, SearchRequest request, SearchReference reference)
    throws LdapException;
}
