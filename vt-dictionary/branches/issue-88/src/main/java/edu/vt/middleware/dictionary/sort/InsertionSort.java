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
 * Provides an implementation of the insertion sort algorithm.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class InsertionSort implements Sorter<List<String>>
{


  /** {@inheritDoc} */
  public void sort(final List<String> l)
  {
    final int n = l.size();
    for (int i = 1; i < n; i++) {
      int j = i - 1;
      final String a = l.get(i);
      String b = l.get(j);
      while (j >= 0 && (a.compareTo(b = l.get(j)) < 0)) {
        l.set(j + 1, b);
        j--;
      }
      l.set(j + 1, a);
    }
  }
}
