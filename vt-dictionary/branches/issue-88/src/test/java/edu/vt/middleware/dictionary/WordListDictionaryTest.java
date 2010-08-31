/*
  $Id: DictionaryTest.java 166 2009-05-03 03:52:31Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 166 $
  Updated: $Date: 2009-05-02 23:52:31 -0400 (Sat, 02 May 2009) $
*/
package edu.vt.middleware.dictionary;

import java.io.FileReader;
import java.io.RandomAccessFile;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link WordListDictionary}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class WordListDictionaryTest
{
  /**
   * Creates dictionary search test data.
   *
   * @return  Search test data.
   *
   * @throws  Exception  On data creation.
   */
  @DataProvider(name = "searchData")
  public Object[][] createSearchData() throws Exception
  {
    final Dictionary csDict1 = new WordListDictionary(
        WordListUtils.createFromFile(
            new FileReader("src/test/resources/web2.sort"), true, true));
    final Dictionary ciDict2 = new WordListDictionary(
        WordListUtils.createFromFile(
            new FileReader("src/test/resources/web2"), false, true));
    final Dictionary csDict3 = new WordListDictionary(
        new FileWordList(
            new RandomAccessFile("src/test/resources/web2.sort", "r"), true));
    final Dictionary ciDict4 = new WordListDictionary(
        new FileWordList(
            new RandomAccessFile("src/test/resources/web2", "r"), false));

    return new Object[][] {
      {csDict1, "ornithopter", true },
      {csDict1, "Pawpaw", false },
      {ciDict2, "brujo", true},
      {ciDict2, "Jocular", true },
      {csDict3, "Xaverian", true },
      {csDict3, "wycliffian", false },
      {ciDict4, "Pantopelagian", true},
      {ciDict4, "trigeminal", true },
    };
  }


  /**
   * @param  dictionary  Dictionary searched for target word.
   * @param  word  Target word.
   * @param  expected  Expected search result.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wldicttest"}, dataProvider = "searchData")
  public void search(
    final Dictionary dictionary, final String word, final boolean expected)
    throws Exception
  {
    AssertJUnit.assertEquals(expected, dictionary.search(word));
  }
}
