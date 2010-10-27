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
package edu.vt.middleware.dictionary;

import java.io.FileReader;
import edu.vt.middleware.dictionary.sort.ArraysSort;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link WordLists}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1509 $
 */
public class WordListsTest
{

  /** Case sensitive word list. */
  private ArrayWordList caseSensitiveWordList;

  /** Case insensitive word list. */
  private ArrayWordList caseInsensitiveWordList;


  /**
   * @param  file1  dictionary to load.
   * @param  file2  dictionary to load.
   *
   * @throws  Exception  On word list creation.
   */
  @Parameters({ "fbsdFile", "webFile" })
  @BeforeClass(groups = {"wltest"})
  public void createWordLists(final String file1, final String file2)
    throws Exception
  {
    this.caseSensitiveWordList = WordLists.createFromReader(
      new FileReader[] {new FileReader(file1)},
      true,
      new ArraysSort());

    this.caseInsensitiveWordList = WordLists.createFromReader(
      new FileReader[] {new FileReader(file2)},
      false,
      new ArraysSort());
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"wltest"})
  public void closeWordLists()
    throws Exception
  {
    this.caseSensitiveWordList = null;
    this.caseInsensitiveWordList = null;
  }


  /**
   * @return  Test data for creating key pair entries.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "searchData")
  public Object[][] createTestData()
    throws Exception
  {
    final ArrayWordList oneWord = new ArrayWordList(new String[] {"a"});
    final ArrayWordList twoWords = new ArrayWordList(new String[] {"a", "b"});
    final ArrayWordList threeWords = new ArrayWordList(
      new String[] {"a", "b", "c"});
    return
      new Object[][] {
        {oneWord, "a", 0},
        {oneWord, "b", WordLists.NOT_FOUND},
        {twoWords, "a", 0},
        {twoWords, "b", 1},
        {twoWords, "c", WordLists.NOT_FOUND},
        {threeWords, "a", 0},
        {threeWords, "b", 1},
        {threeWords, "c", 2},
        {threeWords, "d", WordLists.NOT_FOUND},
        {this.caseSensitiveWordList, "ISBN", 76},
        {this.caseSensitiveWordList, "guacamole", WordLists.NOT_FOUND},
        {this.caseInsensitiveWordList, "irresolute", 98323},
        {this.caseInsensitiveWordList, "brujo", WordLists.NOT_FOUND},
      };
  }


  /**
   * Test for {@link WordLists.binarySearch(WordList, int)}.
   *
   * @param  wl  Test word list.
   * @param  word  Word to search for.
   * @param  expectedResult  Expected result of test.
   */
  @Test(
    groups = {"wltest"},
    dataProvider = "searchData"
  )
  public void binarySearchTest(
    final WordList wl,
    final String word,
    final int expectedResult)
  {
    AssertJUnit.assertEquals(expectedResult, WordLists.binarySearch(wl, word));
  }
}
