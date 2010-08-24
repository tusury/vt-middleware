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
 * Represents a random-access list of words.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public interface WordList
{
  /**
   * Gets the comparator that should be used to compare a search term with
   * candidate words in the list.  The comparator naturally respects ordering
   * and case sentitivy of the word list.
   *
   * @return  Comparator for words in the list.
   */
  Comparator<String> getComparator();

  /**
   * Gets the word at the given 0-based index.
   *
   * @param  index  0-based index.
   *
   * @return  Word at given index.
   */
  String get(int index);

  /**
   * Gets the number of words in the list.
   *
   * @return  Total number of words in list.
   */
  int size();
}
