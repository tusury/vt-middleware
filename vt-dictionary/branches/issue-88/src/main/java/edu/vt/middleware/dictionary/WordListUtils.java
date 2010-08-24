/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for common operations on word lists.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public final class WordListUtils
{
  /** Index returned when word not found by binary search */
  public static final int NOT_FOUND = -1;


  /** Private constructor of utility class. */
  private WordListUtils() {}


  /**
   * Performs a binary search of the given word list for the given word.
   *
   * @param  wordList  Word list to search
   * @param  word  Word to search for.
   *
   * @return  Index of given word in list or a negative number if not found.
   */
  public static int binarySearch(final WordList wordList, final String word)
  {
    final Comparator<String> comparator = wordList.getComparator();
    int l = 0;
    int r = wordList.size() - 1;
    int delta;
    int m;
    int cmp;
    do {
      delta = r - l;
      m = l + delta / 2;
      cmp = comparator.compare(word, wordList.get(m));
      if (cmp < 0) {
        r = m;
      } else if (cmp > 0) {
        l = m;
      } else {
        return m;
      }
    } while (delta > 1);
    int result = NOT_FOUND;
    if (m <= r && comparator.compare(word, wordList.get(m)) == 0) {
      result = m;
    } else if (m + 1 <= r && comparator.compare(word, wordList.get(++m)) == 0) {
      result = m;
    }
    return result;
  }


  /**
   * Creates a case-sensitive {@link ArrayWordList} by reading the contents of
   * the given file.
   *
   * @param  reader  File reader.
   *
   * @return  Word list read from given file.
   */
  public static ArrayWordList createFromFile(final FileReader reader)
  {
    return createFromFile(reader, true);
  }


  /**
   * Creates an {@link ArrayWordList} by reading the contents of the given file.
   *
   * @param  reader  File reader.
   * @param  caseSensitive  Set to true to create case-sensitive word list
   * (default), false otherwise.
   *
   * @return  Word list read from given file.
   */
  public static ArrayWordList createFromFile(
      final FileReader reader, final boolean caseSensitive)
  {
    return createFromFile(reader, caseSensitive, false);
  }


  /**
   * Creates an {@link ArrayWordList} by reading the contents of the given file
   * with support for sorting file contents.
   *
   * @param  reader  File reader.
   * @param  caseSensitive  Set to true to create case-sensitive word list
   * (default), false otherwise.
   * @param  sort  True to sort file contents, false otherwise.
   *
   * @return  Word list read from given file.
   */
  public static ArrayWordList createFromFile(
      final FileReader reader, final boolean caseSensitive, final boolean sort)
  {
    final BufferedReader br = new BufferedReader(reader);
    final List<String> words = new ArrayList<String>();
    try {
      String word = null;
      while ((word = br.readLine()) != null) {
        word = word.trim();
        if (!"".equals(word)) {
          words.add(word);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error reading from file.", e);
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return new ArrayWordList(
        words.toArray(new String[] {}), caseSensitive, sort);
  }

}
