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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Delegates sorting to {@link java.util.Collections#sort(List)}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class CollectionsSort implements Sorter<List<String>>
{


  /** {@inheritDoc} */
  public void sort(final List<String> l)
  {
    Collections.sort(l);
  }


  /**
   * Sort the supplied list with
   * {@link java.util.Collections#sort(List, Comparator)}.
   *
   * @param  l  <code>List</code> to sort
   * @param  c  <code>Comparator</code> to use when sorting
   */
  public void sort(final List<String> l, final Comparator<String> c)
  {
    Collections.sort(l, c);
  }
}
