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
import java.util.Arrays;
import edu.vt.middleware.dictionary.sort.ArraySorter;
import edu.vt.middleware.dictionary.sort.ArraysSort;
import edu.vt.middleware.dictionary.sort.BubbleSort;
import edu.vt.middleware.dictionary.sort.InsertionSort;
import edu.vt.middleware.dictionary.sort.QuickSort;
import edu.vt.middleware.dictionary.sort.SelectionSort;
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


  /** @throws  Exception  On test failure. */
  @BeforeClass(groups = {"ttdicttest"})
  public void createDictionary()
    throws Exception
  {
    final ArrayWordList awl1 = WordLists.createFromReader(
      new FileReader[] {new FileReader(this.webFile)},
      true,
      new ArraysSort());
    this.caseSensitive = new TernaryTreeDictionary(awl1);

    final ArrayWordList awl2 = WordLists.createFromReader(
      new FileReader[] {new FileReader(this.webFile)},
      false,
      new ArraysSort());
    this.caseInsensitive = new TernaryTreeDictionary(awl2);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"ttdicttest"})
  public void closeDictionary()
    throws Exception
  {
    this.caseSensitive = null;
    this.caseInsensitive = null;
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
  @Test(
    groups = {"ttdicttest"},
    dataProvider = "all-web-words"
  )
  public void searchAll(final String word)
    throws Exception
  {
    AssertJUnit.assertTrue(this.caseSensitive.search(word));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word.toLowerCase()));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word.toUpperCase()));
  }


  /**
   * @param  word  to search for.
   * @param  results  case sensitive results
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "partialSearchWord", "partialSearchResults" })
  @Test(groups = {"ttdicttest"})
  public void partialSearch(final String word, final String results)
    throws Exception
  {
    AssertJUnit.assertTrue(
      Arrays.equals(
        results.split("\\|"),
        this.caseSensitive.partialSearch(word)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        results.split("\\|"),
        this.caseSensitive.partialSearch(FALSE_SEARCH)));

    try {
      this.caseInsensitive.partialSearch(word);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(),
        UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * @param  word  to search for.
   * @param  distance  for near search
   * @param  results  case sensitive results
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "nearSearchWord", "nearSearchDistance", "nearSearchResults" })
  @Test(groups = {"ttdicttest"})
  public void nearSearch(
    final String word,
    final int distance,
    final String results)
    throws Exception
  {
    AssertJUnit.assertTrue(
      Arrays.equals(
        results.split("\\|"),
        this.caseSensitive.nearSearch(word, distance)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        results.split("\\|"),
        this.caseSensitive.nearSearch(FALSE_SEARCH, distance)));

    try {
      this.caseInsensitive.nearSearch(word, distance);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(),
        UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
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
   *
   * @throws  Exception  On test failure.
   */
  public void testSort(final ArraySorter sorter)
    throws Exception
  {
    ArrayWordList awl = new ArrayWordList(ANIMALS, true, sorter);
    final TernaryTreeDictionary sortCS = new TernaryTreeDictionary(awl);
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

    awl = new ArrayWordList(ANIMALS, false, sorter);

    final TernaryTreeDictionary sortCI = new TernaryTreeDictionary(awl);
    AssertJUnit.assertTrue(sortCI.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertTrue(sortCI.search(ANIMAL_SEARCH_CI));
  }
}
