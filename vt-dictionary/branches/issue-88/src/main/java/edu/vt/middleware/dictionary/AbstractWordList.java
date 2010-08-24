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

/**
 * Provides common operations implementations for word lists. The
 * lower case property allows data to be read from the list in lower case
 * format, however the underlying data is not changed. This allows lists to be
 * sorted as if they are lower case without modifying the data.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public abstract class AbstractWordList implements WordList
{
  /** Case sensitive comparator */
  protected static final Comparator<String> CASE_SENSITIVE_COMPARATOR =
    new CaseSensitiveComparator();

  /** Case insensitive comparator */
  protected static final Comparator<String> CASE_INSENSITIVE_COMPARATOR =
    new CaseInsensitiveComparator();

  /** Word comparator */
  protected Comparator<String> comparator;



  /** {@inheritDoc} */
  public Comparator<String> getComparator()
  {
    return comparator;
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
   * Performs a case sensitive comparison between two strings.
   *
   * @author Middleware
   * @version $Revision: $
   *
   */
  public static class CaseSensitiveComparator implements Comparator<String>
  {
    /** {@inheritDoc} */
    public int compare(final String a, final String b)
    {
      return a.compareTo(b);
    }
  }


  /**
   * Performs a case sensitive comparison between two strings.
   *
   * @author Middleware
   * @version $Revision: $
   *
   */
  public static class CaseInsensitiveComparator implements Comparator<String>
  {
    /** {@inheritDoc} */
    public int compare(final String a, final String b)
    {
      return a.compareToIgnoreCase(b);
    }
  }
}
