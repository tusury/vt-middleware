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


  /** {@inheritDoc} */
  @Override
  HandlerResult<SearchReference> process(
    Connection conn,
    SearchRequest request,
    SearchReference reference)
    throws LdapException;
}
