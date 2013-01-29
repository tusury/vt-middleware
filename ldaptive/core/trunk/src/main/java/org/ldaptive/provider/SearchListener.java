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
package org.ldaptive.provider;

/**
 * Search results listener.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface SearchListener extends ResponseListener
{


  /**
   * Invoked when a search item is received from a provider.
   *
   * @param  item  containing a search result entry, reference, or intermediate
   * response
   */
  void searchItemReceived(SearchItem item);
}
