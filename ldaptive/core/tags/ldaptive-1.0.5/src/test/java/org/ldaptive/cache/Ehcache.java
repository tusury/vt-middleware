/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.cache;

import net.sf.ehcache.Element;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;

/**
 * Ehcache implementation.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Ehcache<Q extends SearchRequest> implements Cache<Q>
{

  /** Underlying ehcache. */
  protected final net.sf.ehcache.Cache cache;


  /**
   * Creates a new ehcache.
   *
   * @param  c  backing ehcache
   */
  public Ehcache(final net.sf.ehcache.Cache c)
  {
    cache = c;
  }


  /** Removes all data from this cache. */
  public void clear()
  {
    cache.removeAll();
  }


  /** {@inheritDoc} */
  @Override
  public SearchResult get(final Q request)
  {
    final Element e = cache.get(request);
    if (e == null) {
      return null;
    }
    return (SearchResult) e.getObjectValue();
  }


  /** {@inheritDoc} */
  @Override
  public void put(final Q request, final SearchResult result)
  {
    cache.put(new Element(request, result));
  }


  /**
   * Returns the number of items in this cache.
   *
   * @return  size of this cache
   */
  public int size()
  {
    return cache.getKeysWithExpiryCheck().size();
  }
}
