/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.dictionary;

import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link TernaryTreeDictionary}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TernaryTreeDictionaryTest
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
    final FilePointerWordList wordListCS = new FilePointerWordList(
      new RandomAccessFile[] {
        new RandomAccessFile("src/test/resources/web2.sort", "r"),
      },
      true);
    final FilePointerWordList wordListCI = new FilePointerWordList(
      new RandomAccessFile[] {
        new RandomAccessFile("src/test/resources/web2", "r"),
      },
      false);
    final Dictionary caseSensitiveDict =
      new TernaryTreeDictionary(wordListCS, true);
    final Dictionary caseInsensitiveDict =
      new TernaryTreeDictionary(wordListCI, true);
    wordListCS.close();
    wordListCI.close();

    return new Object[][] {
      {caseSensitiveDict, "ornithopter", true },
      {caseSensitiveDict, "Pawpaw", false },
      {caseInsensitiveDict, "Jocular", true },
      {caseSensitiveDict, "brujo", false },
    };
  }


  /**
   * Creates partial search test data.
   *
   * @return  Partial search test data.
   *
   * @throws  Exception  On data creation.
   */
  @DataProvider(name = "partialSearchData")
  public Object[][] createPartialSearchData() throws Exception
  {
    final FileWordList wordListCS = new FileWordList(
      new RandomAccessFile("src/test/resources/web2.sort", "r"), true);
    final FileWordList wordListCI = new FileWordList(
      new RandomAccessFile("src/test/resources/web2", "r"), false);
    final Dictionary caseSensitiveDict =
      new TernaryTreeDictionary(wordListCS, true);
    final Dictionary caseInsensitiveDict =
      new TernaryTreeDictionary(wordListCI, true);

    return new Object[][] {
      {
        caseSensitiveDict,
        ".e.e.e.e",
        new String[] {"Genevese", "reserene", "teleseme", "terebene"},
      },
      {
        caseInsensitiveDict,
        ".e.e.e.e",
        new String[] {"Genevese", "reserene", "teleseme", "terebene"},
      },
      {
        caseInsensitiveDict,
        ".ix",
        new String[] {"Aix", "fix", "mix", "nix", "pix", "rix", "six"},
      },
    };
  }


  /**
   * Creates near search test data.
   *
   * @return  Near search test data.
   *
   * @throws  Exception  On data creation.
   */
  @DataProvider(name = "nearSearchData")
  public Object[][] createNearSearchData() throws Exception
  {
    final WordList wordListCS = WordListUtils.createFromFile(
        new FileReader("src/test/resources/web2"), true, true);
    final WordList wordListCI = WordListUtils.createFromFile(
        new FileReader("src/test/resources/web2"), false, true);
    final Dictionary caseSensitiveDict =
      new TernaryTreeDictionary(wordListCS, false);
    final Dictionary caseInsensitiveDict =
      new TernaryTreeDictionary(wordListCI, false);

    return new Object[][] {
      {
        caseSensitiveDict,
        "Jicaque",
        2,
        new String[] {"Jicaque", "Jicaquean", "Xicaque", "macaque"},
      },
      {
        caseInsensitiveDict,
        "Jicaque",
        2,
        new String[] {"Jicaque", "Jicaquean", "jocoque", "macaque", "Xicaque"},
      },
    };
  }


  /**
   * @param  dictionary  Dictionary searched for target word.
   * @param  word  Target word.
   * @param  expected  Expected search result.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ttdicttest"}, dataProvider = "searchData")
  public void search(
    final Dictionary dictionary, final String word, final boolean expected)
    throws Exception
  {
    AssertJUnit.assertEquals(expected, dictionary.search(word));
  }


  /**
   * @param  dictionary  Dictionary searched for target word.
   * @param  searchTerm  Partial search term.
   * @param  expected  Expected partial search results.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ttdicttest"}, dataProvider = "partialSearchData")
  public void partialSearch(
    final TernaryTreeDictionary dictionary,
    final String searchTerm,
    final String[] expected)
    throws Exception
  {
    final String[] actual = dictionary.partialSearch(searchTerm);
    System.out.println("Partial search results: " + Arrays.toString(actual));
    System.out.println("Partial search expected: " + Arrays.toString(expected));
    Assert.assertEquals(actual, expected);
  }


  /**
   * @param  dictionary  Dictionary searched for target word.
   * @param  word  to search for.
   * @param  distance  for near search
   * @param  expected  Expected partial search results.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ttdicttest"}, dataProvider = "nearSearchData")
  public void nearSearch(
    final TernaryTreeDictionary dictionary,
    final String word,
    final int distance,
    final String[] expected)
    throws Exception
  {
    final String[] actual = dictionary.nearSearch(word, distance);
    System.out.println("Near search results: " + Arrays.toString(actual));
    System.out.println("Near search expected: " + Arrays.toString(expected));
    Assert.assertEquals(actual, expected);
  }
}
