/*
  $Id: Dictionary.java 1252 2010-04-16 21:24:23Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1252 $
  Updated: $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
*/
package edu.vt.middleware.dictionary;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Provides common operations implementations for word lists.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public abstract class AbstractWordList implements WordList
{

  /** Word comparator */
  protected Comparator<String> comparator;


  /** {@inheritDoc} */
  public Comparator<String> getComparator()
  {
    return comparator;
  }


  /** {@inheritDoc} */
  public Iterator<String> iterator()
  {
    return new SequentialIterator();
  }


  /** {@inheritDoc} */
  public Iterator<String> medianIterator()
  {
    return new MedianIterator();
  }


  /**
   * Throws a <code>IndexOutOfBoundsException</code> if the supplied index is
   * less than 0 or greater than or equal to the size of this word list.
   *
   * @param  index  <code>int</code>
   */
  protected void checkRange(final int index)
  {
    if (index < 0 || index >= this.size()) {
      throw new IndexOutOfBoundsException(
        "Supplied index (" + index + ") does not exist");
    }
  }


  /**
   * Throws a <code>ClassCastException</code> if the supplied object is not an
   * instance of <code>String</code>.
   *
   * @param  o  <code>Object</code>
   */
  protected void checkIsString(final Object o)
  {
    if (!String.class.isInstance(o)) {
      throw new ClassCastException("Parameter must be of type String");
    }
  }


  /**
   * Abstract base class for all internal word list iterators.
   *
   * @author Middleware
   * @version $Revision: 1465 $
   *
   */
  private abstract class AbstractWordListIterator implements Iterator<String>
  {

    /** Index of next word in list. */
    protected int index;


    /** {@inheritDoc} */
    public void remove()
    {
      throw new UnsupportedOperationException("Remove not supported.");
    }
  }


  /**
   * Iterator implementation that iterates over a {@link WordList} by
   * incrementing an index from 0 to {@link WordList#size()} - 1.
   *
   * @author Middleware
   * @version $Revision: 1465 $
   *
   */
  private class SequentialIterator extends AbstractWordListIterator
  {


    /** {@inheritDoc} */
    public boolean hasNext()
    {
      return index < size();
    }


    /** {@inheritDoc} */
    public String next()
    {
      return get(index++);
    }
  }


  /**
   * Iterator that iterates over a word list from the median outward to either
   * end.  In particular, for a word list of N elements whose median index is
   * M, and for each i such that M-i >= 0 and M+i < N, the M-i element is
   * visited  before the M+i element.
   *
   * @author Middleware
   * @version $Revision: 1465 $
   *
   */
  private class MedianIterator extends AbstractWordListIterator
  {
    /** Index of median element in given list */
    private final int median = size() / 2;

    /** Indicates direction of next item */
    private int sign;


    /** {@inheritDoc} */
    public boolean hasNext()
    {
      final int n = size();
      final boolean result;
      if (sign > 0) {
        result = median + index < n;
      } else if (sign < 0) {
        result = median - index >= 0;
      } else {
        result = n > 0;
      }
      return result;
    }


    /** {@inheritDoc} */
    public String next()
    {
      final String next;
      if (sign > 0) {
        next = get(median + index);
        sign = -1;
        index++;
      } else if (sign < 0) {
        next = get(median - index);
        sign = 1;
      } else {
        next = get(median);
        sign = -1;
        index = 1;
      }
      return next;
    }
  }
}
