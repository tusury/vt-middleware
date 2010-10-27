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
 * Provides an implementation of the selection sort algorithm.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class SelectionSort implements ArraySorter
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
    for (int i = 0; i < n - 1; i++) {
      int min = i;
      for (int j = i + 1; j < n; j++) {
        final String b = array[j];
        if (c.compare(b, array[min]) < 0) {
          min = j;
        }
      }

      final String s = array[min];
      array[min] = array[i];
      array[i] = s;
    }
  }
}
