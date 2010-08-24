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

import java.io.FileReader;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link WordListUtils} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class WordListUtilsTest
{
  /** Case sensitive word list */
  private WordList caseSensitiveWordList;

  /** Case insensitive word list */
  private WordList caseInsensitiveWordList;


  /**
   * Creates word lists from word files.
   *
   * @throws Exception  On word list creation.
   */
  @BeforeClass(groups = {"wltest"})
  public void createWordLists() throws Exception
  {
    this.caseSensitiveWordList = WordListUtils.createFromFile(
        new FileReader("src/test/resources/freebsd"), true, true);
    this.caseInsensitiveWordList = WordListUtils.createFromFile(
        new FileReader("src/test/resources/web2"), false, true);
  }


  /**
   * @return  Test data for creating key pair entries.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "searchData")
  public Object[][] createTestData() throws Exception
  {
    final ArrayWordList oneWord = new ArrayWordList(new String[] {"a"});
    final ArrayWordList twoWords = new ArrayWordList(new String[] {"a", "b"});
    final ArrayWordList threeWords = new ArrayWordList(
        new String[] {"a", "b", "c"});
    return new Object[][] {
      {oneWord, "a", 0 },
      {oneWord, "b", WordListUtils.NOT_FOUND },
      {twoWords, "a", 0 },
      {twoWords, "b", 1 },
      {twoWords, "c", WordListUtils.NOT_FOUND },
      {threeWords, "a", 0 },
      {threeWords, "b", 1 },
      {threeWords, "c", 2 },
      {threeWords, "d", WordListUtils.NOT_FOUND },
      {caseSensitiveWordList, "ISBN", 76},
      {caseSensitiveWordList, "guacamole", WordListUtils.NOT_FOUND},
      {caseInsensitiveWordList, "irresolute", 98299},
      {caseInsensitiveWordList, "brujo", WordListUtils.NOT_FOUND},
    };
  }


  /**
   * Tests the {@link WordListUtils.binarySearch(WordList, int) method.
   *
   * @param wl Test word list.
   * @param word Word to search for.
   * @param expectedResult Expected result of test.
   */
  @Test(
      groups = {"wltest"},
      dataProvider = "searchData")
  public void binarySearchTest(
      final WordList wl, final String word, final int expectedResult)
  {
    AssertJUnit.assertEquals(
        expectedResult,
        WordListUtils.binarySearch(wl, word));
  }
}
