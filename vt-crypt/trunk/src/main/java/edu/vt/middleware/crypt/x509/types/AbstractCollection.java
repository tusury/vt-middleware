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

/**
 * Base class for all types that simply contain collections of other types.
 *
 * @author Middleware
 * @version $Revision$
 * @param <T> Type of object contained in collection.
 *
 */
public abstract class AbstractCollection<T> implements Collection<T>
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
      final Collection other = (Collection) obj;
      if (other.getItems() != null && items != null &&
          other.getItems().length == items.length)
      {
        for (int i = 0; i < items.length; i++) {
          if (!items[i].equals(other.getItems()[i])) {
            return false;
          }
        }
        result = true;
      }
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getHashSeed();
    if (items != null) {
      for (T item : items) {
        hash = HASH_FACTOR * hash + item.hashCode();
      }
    }
    return hash;
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
    final StringBuilder sb = new StringBuilder();
    sb.append('[');
    int i = 0;
    for (T item : items) {
      if (i++ > 0) {
        sb.append(", ");
      }
      sb.append(item);
    }
    sb.append(']');
    return sb.toString();
  }


  /**
   * Gets the hash code seed value for this class.
   *
   * @return  Hash code seed value.
   */
  protected abstract int getHashSeed();
}
