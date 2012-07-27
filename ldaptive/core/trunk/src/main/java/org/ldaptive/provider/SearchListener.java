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
package org.ldaptive.provider;

import org.ldaptive.Response;

/**
 * Search results listener.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface SearchListener
{


  /**
   * Invoked when a search item is received from a provider.
   *
   * @param  item  containing a search result entry, reference, or intermediate
   * response
   */
  void searchItemReceived(final SearchItem item);


  /**
   * Invoked when a search result is received from a provider indicating the
   * search operation has completed.
   *
   * @param  response  containing the result
   */
  void searchResponseReceived(final Response<Void> response);
}
