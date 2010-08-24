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

import java.util.Arrays;

/**
 * Provides a {@link WordList} backed by a string array.
 * Since the entire word list is stored in memory java heap settings may need
 * to be modified in order to store large word lists.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class ArrayWordList extends AbstractWordList
{

  /** file containing words. */
  protected String[] words;


  /**
   * Creates a new case-sensitive word list backed by the given array.
   *
   * @param  array  Array of words.
   *
   * @throws  IllegalArgumentException  If array is null or contains any null
   * entries.
   */
  public ArrayWordList(final String[] array)
  {
    this(array, true);
  }


  /**
   * Creates a new word list backed by the given array.
   *
   * @param  array  Array of words.
   * @param  caseSensitive  Set to true to create case-sensitive word list,
   * false otherwise.
   *
   * @throws  IllegalArgumentException  If array is null or contains any null
   * entries.
   */
  public ArrayWordList(final String[] array, final boolean caseSensitive)
  {
    this(array, caseSensitive, false);
  }


  /**
   * Creates a new word list backed by the given array with optional sorting of
   * the input string array.
   *
   * @param  array  Array of words.
   * @param  caseSensitive  Set to true to create case-sensitive word list,
   * false otherwise.
   * @param  sort  Flag indicating the input array should be sorted.  The sort
   * routine is consistent with {@link #getComparator()}, which respects the
   * case sensitivity of the word list.
   *
   * @throws  IllegalArgumentException  If array is null or contains any null
   * entries.
   */
  public ArrayWordList(
      final String[] array, final boolean caseSensitive, final boolean sort)
  {
    if (array == null) {
      throw new IllegalArgumentException("Array cannot be null.");
    }
    if (caseSensitive) {
      comparator = CASE_SENSITIVE_COMPARATOR;
    } else {
      comparator = CASE_INSENSITIVE_COMPARATOR;
    }
    for (int i = 0; i < array.length; i++) {
      if (array[i] == null) {
        throw new IllegalArgumentException(
            "Word list cannot contain null entries.");
      }
    }
    this.words = array;
    if (sort) {
      Arrays.sort(array, comparator);
    }
  }


  /** {@inheritDoc} */
  public String get(final int index)
  {
    this.checkRange(index);
    return this.words[index];
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.words.length;
  }

}
