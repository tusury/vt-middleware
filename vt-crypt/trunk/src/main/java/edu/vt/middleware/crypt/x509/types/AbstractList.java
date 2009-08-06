/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.Arrays;

/**
 * Base class for all types that simply contain collections of other types.
 *
 * @author Middleware
 * @version $Revision$
 * @param <T> Type of object contained in collection.
 *
 */
public abstract class AbstractList<T> implements List<T>
{
  /** Hash code scale factor */
  private static final int HASH_FACTOR = 31;

  /** Items in collection */
  protected T[] items;


  /** {@inheritDoc} */
  public T[] getItems()
  {
    return items;
  }


  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(final Object obj)
  {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      result = Arrays.equals(items, ((List) obj).getItems());
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return getHashSeed() + HASH_FACTOR * Arrays.hashCode(items);
  }


  /**
   * Provides a string representation of all the items in the collection.
   *
   * @return  String of the format [item1, item2, item3, ... itemN] where
   * item1 ... itemN are the string representations if items 1 through N.
   */
  @Override
  public String toString()
  {
    return Arrays.toString(items);
  }


  /**
   * Gets the hash code seed value for this class.
   *
   * @return  Hash code seed value.
   */
  protected abstract int getHashSeed();
}
