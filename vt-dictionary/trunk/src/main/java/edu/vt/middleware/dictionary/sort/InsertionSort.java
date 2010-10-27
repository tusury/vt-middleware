/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.dictionary.sort;

import java.util.Comparator;
import edu.vt.middleware.dictionary.WordLists;

/**
 * Provides an implementation of the insertion sort algorithm.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class InsertionSort implements ArraySorter
{


  /** {@inheritDoc} */
  public void sort(final String[] array)
  {
    this.sort(array, WordLists.CASE_SENSITIVE_COMPARATOR);
  }


  /** {@inheritDoc} */
  public void sort(final String[] array, final Comparator<String> c)
  {
    final int n = array.length;
    for (int i = 1; i < n; i++) {
      int j = i - 1;
      final String a = array[i];
      String b = array[j];
      while (j >= 0 && (c.compare(a, b = array[j]) < 0)) {
        array[j + 1] = b;
        j--;
      }
      array[j + 1] = a;
    }
  }
}
