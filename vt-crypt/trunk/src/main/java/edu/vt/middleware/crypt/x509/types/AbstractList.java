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
package edu.vt.middleware.crypt.x509.types;

import java.util.Arrays;

/**
 * Base class for all types that simply contain collections of other types.
 *
 * @param  <T>  Type of object contained in collection.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractList<T> implements List<T>
{

  /** Hash code scale factor. */
  private static final int HASH_FACTOR = 31;

  /** Items in collection. */
  protected T[] items;


  /** {@inheritDoc} */
  public T[] getItems()
  {
    return items;
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      result = Arrays.equals(items, ((List<?>) obj).getItems());
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return getClass().hashCode() + HASH_FACTOR * Arrays.hashCode(items);
  }


  /**
   * Provides a string representation of all the items in the collection.
   *
   * @return  String of the format [item1, item2, item3, ... itemN] where item1
   * ... itemN are the string representations if items 1 through N.
   */
  @Override
  public String toString()
  {
    return Arrays.toString(items);
  }
}
