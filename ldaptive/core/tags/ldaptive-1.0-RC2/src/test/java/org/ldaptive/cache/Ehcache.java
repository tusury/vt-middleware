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
package org.ldaptive.cache;

import net.sf.ehcache.Element;
import org.ldaptive.LdapResult;
import org.ldaptive.SearchRequest;

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
  protected net.sf.ehcache.Cache cache;


  /**
   * Creates a new ehcache.
   *
   * @param  c  backing ehcache
   */
  public Ehcache(final net.sf.ehcache.Cache c)
  {
    cache = c;
  }


  /**
   * Removes all data from this cache.
   */
  public void clear()
  {
    cache.removeAll();
  }


  /** {@inheritDoc} */
  @Override
  public LdapResult get(final Q request)
  {
    final Element e = cache.get(request);
    if (e == null) {
      return null;
    }
    return (LdapResult) e.getObjectValue();
  }


  /** {@inheritDoc} */
  @Override
  public void put(final Q request, final LdapResult lr)
  {
    cache.put(new Element(request, lr));
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
