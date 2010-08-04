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
package edu.vt.middleware.dictionary.sort;

import java.util.List;

/**
 * Provides an implementation of the quick sort algorithm.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class QuickSort implements Sorter<List<String>>
{


  /** {@inheritDoc} */
  public void sort(final List<String> l)
  {
    if (l.size() > 0) {
      sort(l, 0, l.size() - 1);
    }
  }


  /**
   * This will sort the supplied <code>List</code> beginning at the lo index and
   * ending at the hi index, using the quick sort algorithm.
   *
   * @param  l  <code>List</code> to sort
   * @param  lo  <code>int</code> index to beginning sorting at
   * @param  hi  <code>int</code> index to stop sorting at
   */
  public static void sort(final List<String> l, final int lo, final int hi)
  {
    final int m = (int) Math.floor((lo + hi) / 2);
    final String x = l.get(m);

    int i = lo;
    int j = hi;
    do {
      while (x.compareTo(l.get(i)) > 0) {
        i++;
      }

      while (x.compareTo(l.get(j)) < 0) {
        j--;
      }

      if (i <= j) {
        l.set(j, l.set(i, l.get(j)));
        i++;
        j--;
      }
    } while (i <= j);

    if (lo < j) {
      sort(l, lo, j);
    }
    if (i < hi) {
      sort(l, i, hi);
    }
  }
}
