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
 * Provides an implementation of the selection sort algorithm.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class SelectionSort implements Sorter<List<String>>
{


  /** {@inheritDoc} */
  public void sort(final List<String> l)
  {
    final int n = l.size();
    for (int i = 0; i < n - 1; i++) {
      int min = i;
      for (int j = i + 1; j < n; j++) {
        final String b = l.get(j);
        if (b.compareTo(l.get(min)) < 0) {
          min = j;
        }
      }
      l.set(i, l.set(min, l.get(i)));
    }
  }
}
