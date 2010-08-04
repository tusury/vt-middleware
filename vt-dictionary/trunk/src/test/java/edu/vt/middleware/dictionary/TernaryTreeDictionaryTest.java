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

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import edu.vt.middleware.dictionary.sort.BubbleSort;
import edu.vt.middleware.dictionary.sort.InsertionSort;
import edu.vt.middleware.dictionary.sort.QuickSort;
import edu.vt.middleware.dictionary.sort.SelectionSort;
import edu.vt.middleware.dictionary.sort.Sorter;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link TernaryTreeDictionary}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TernaryTreeDictionaryTest extends AbstractDictionaryTest
{

  /** Test dictionary. */
  private TernaryTreeDictionary caseSensitive;

  /** Test dictionary. */
  private TernaryTreeDictionary caseInsensitive;


  /**
   * @throws  Exception  On test failure.
   */
  @BeforeClass(groups = {"ttdicttest"})
  public void createDictionary()
    throws Exception
  {
    final FilePointerWordList fwl1 = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(this.webFile, "r")});
    this.caseSensitive = new TernaryTreeDictionary();
    this.caseSensitive.setUseMedian(true);
    this.caseSensitive.setWordList(fwl1);
    this.caseSensitive.initialize();

    final FilePointerWordList fwl2 = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(this.webFile, "r")}, true);
    this.caseInsensitive = new TernaryTreeDictionary();
    this.caseInsensitive.setUseMedian(true);
    this.caseInsensitive.setWordList(fwl2);
    this.caseInsensitive.initialize();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"ttdicttest"})
  public void closeDictionary()
    throws Exception
  {
    this.caseSensitive.close();
    this.caseInsensitive.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ttdicttest"})
  public void getWords()
    throws Exception
  {
    FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(this.webFileSorted, "r")});
    List<String> l = new ArrayList<String>(
      this.caseSensitive.getTernaryTree().getWords());
    Collections.sort(l);
    AssertJUnit.assertEquals(fwl, l);
    l.clear();
    fwl.close();

    fwl = new FilePointerWordList(
      new RandomAccessFile[] {
        new RandomAccessFile(this.webFileLowerCaseSorted, "r"), });
    l = new ArrayList<String>(this.caseInsensitive.getTernaryTree().getWords());
    Collections.sort(l);
    AssertJUnit.assertEquals(fwl, l);
    l.clear();
    fwl.close();
  }


  /**
   * @param  word  to search for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "webSearchWord" })
  @Test(groups = {"ttdicttest"})
  public void search(final String word)
    throws Exception
  {
    AssertJUnit.assertTrue(this.caseSensitive.search(word));
    AssertJUnit.assertFalse(this.caseSensitive.search(FALSE_SEARCH));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word));
    AssertJUnit.assertFalse(this.caseInsensitive.search(FALSE_SEARCH));
  }


  /**
   * @param  word  to search for.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ttdicttest"}, dataProvider = "all-web-words")
  public void searchAll(final String word)
    throws Exception
  {
    AssertJUnit.assertTrue(this.caseSensitive.search(word));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word));
  }


  /**
   * @param  word  to search for.
   * @param  resultsCS  case sensitive results
   * @param  resultsCI  case insensitive results
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "partialSearchWord", "partialSearchResultsCS", "partialSearchResultsCI"
    }
  )
  @Test(groups = {"ttdicttest"})
  public void partialSearch(
    final String word,
    final String resultsCS,
    final String resultsCI)
    throws Exception
  {
    AssertJUnit.assertTrue(
      Arrays.equals(
        resultsCS.split("\\|"),
        this.caseSensitive.partialSearch(word)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        resultsCS.split("\\|"),
        this.caseSensitive.partialSearch(FALSE_SEARCH)));

    AssertJUnit.assertTrue(
      Arrays.equals(
        resultsCI.split("\\|"),
        this.caseInsensitive.partialSearch(word)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        resultsCI.split("\\|"),
        this.caseInsensitive.partialSearch(FALSE_SEARCH)));
  }


  /**
   * @param  word  to search for.
   * @param  distance  for near search
   * @param  resultsCS  case sensitive results
   * @param  resultsCI  case insensitive results
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "nearSearchWord",
      "nearSearchDistance",
      "nearSearchResultsCS",
      "nearSearchResultsCI"
    }
  )
  @Test(groups = {"ttdicttest"})
  public void nearSearch(
    final String word,
    final int distance,
    final String resultsCS,
    final String resultsCI)
    throws Exception
  {
    AssertJUnit.assertTrue(
      Arrays.equals(
        resultsCS.split("\\|"),
        this.caseSensitive.nearSearch(word, distance)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        resultsCS.split("\\|"),
        this.caseSensitive.nearSearch(FALSE_SEARCH, distance)));

    AssertJUnit.assertTrue(
      Arrays.equals(
        resultsCI.split("\\|"),
        this.caseInsensitive.nearSearch(word, distance)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        resultsCI.split("\\|"),
        this.caseInsensitive.nearSearch(FALSE_SEARCH, distance)));
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ttdicttest"})
  public void bubbleSort()
    throws Exception
  {
    this.testSort(new BubbleSort());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ttdicttest"})
  public void selectionSort()
    throws Exception
  {
    this.testSort(new SelectionSort());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ttdicttest"})
  public void insertionSort()
    throws Exception
  {
    this.testSort(new InsertionSort());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ttdicttest"})
  public void quickSort()
    throws Exception
  {
    this.testSort(new QuickSort());
  }


  /**
   * @param  sorter  <code>Sorter</code> to sort with
   * @throws  Exception  On test failure.
   */
  public void testSort(final Sorter<List<String>> sorter)
    throws Exception
  {
    ArrayWordList awl = new ArrayWordList(Arrays.asList(ANIMALS));
    sorter.sort(awl);
    final TernaryTreeDictionary sortCS = new TernaryTreeDictionary();
    sortCS.setUseMedian(true);
    sortCS.setWordList(awl);
    sortCS.initialize();
    AssertJUnit.assertTrue(sortCS.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertFalse(sortCS.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        sortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        sortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));

    awl = new ArrayWordList(Arrays.asList(ANIMALS), true);
    sorter.sort(awl);
    final TernaryTreeDictionary sortCI = new TernaryTreeDictionary();
    sortCI.setUseMedian(true);
    sortCI.setWordList(awl);
    sortCI.initialize();
    AssertJUnit.assertTrue(sortCI.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertTrue(sortCI.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        sortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        sortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
  }
}
