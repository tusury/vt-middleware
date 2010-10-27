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

/**
 * Interface for array sort implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public interface ArraySorter
{


  /**
   * This will sort the supplied string array.
   *
   * @param  array  To sort
   */
  void sort(String[] array);


  /**
   * This will sort the supplied string array.
   *
   * @param  array  To sort
   * @param  c  Comparator to sort with
   */
  void sort(String[] array, Comparator<String> c);
}
