/*
  $Id: TestUtil.java 1330 2010-05-23 22:10:53Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1330 $
  Updated: $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
*/
package edu.vt.middleware.dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Common methods for dictionary tests.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $
 */
public final class TestUtil
{


  /** Private constructor of utility class. */
  private TestUtil() {}


  /**
   * Returns an array of every line in the supplied file.
   *
   * @param  file  To read
   * @return  Array of lines
   * @throws  IOException  if an error occurs reading the file
   */
  public static String[] fileToArray(final String file)
    throws IOException
  {
    final List<String> words = new ArrayList<String>();
    final BufferedReader br = new BufferedReader(new FileReader(file));
    try {
      String word = null;
      while ((word = br.readLine()) != null) {
        words.add(word);
      }
    } finally {
      br.close();
    }
    return words.toArray(new String[words.size()]);
  }
}
