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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Dictionary}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DictionaryTest
{

  /** Missing search. */
  private static final String FALSE_SEARCH = "not-found-in-the-dictionary";

  /** Animal names for sorting. */
  private static final String[] ANIMALS;

  /** Case sensitive animal search. */
  private static final String ANIMAL_SEARCH_CS = "Kangaroo";

  /** Case insensitive animal search. */
  private static final String ANIMAL_SEARCH_CI = "kangaroo";

  /** Partial animal search. */
  private static final String ANIMAL_PARTIAL_SEARCH = ".a..us";

  /** Partial animal search results. */
  private static final String[] ANIMAL_PARTIAL_SEARCH_RESULTS_CS =
    new String[] {"Walrus", "Xantus"};

  /** Partial animal search results. */
  private static final String[] ANIMAL_PARTIAL_SEARCH_RESULTS_CI =
    new String[] {"walrus", "xantus"};

  /**
   * Load animal names.
   */
  static {
    final List<String> animals = new ArrayList<String>();
    animals.add("Aardvark");
    animals.add("Baboon");
    animals.add("Chinchilla");
    animals.add("Donkey");
    animals.add("Emu");
    animals.add("Flamingo");
    animals.add("Gorilla");
    animals.add("Hippopotamus");
    animals.add("Iguana");
    animals.add("Jackal");
    animals.add("Kangaroo");
    animals.add("Lemming");
    animals.add("Marmot");
    animals.add("Narwhal");
    animals.add("Ox");
    animals.add("Platypus");
    animals.add("Quail");
    animals.add("Rhinoceros");
    animals.add("Skunk");
    animals.add("Tortoise");
    animals.add("Uakari ");
    animals.add("Vulture");
    animals.add("Walrus");
    animals.add("Xantus");
    animals.add("Yak");
    animals.add("Zebra");
    Collections.shuffle(animals);
    ANIMALS = animals.toArray(new String[0]);
  }

  /** Test dictionary. */
  private Dictionary caseSensitive;

  /** Test dictionary. */
  private Dictionary caseInsensitive;


  /**
   * @param  dictFile  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "dictionaryFile" })
  @BeforeClass(groups = {"dicttest"})
  public void createDictionary(final String dictFile)
    throws Exception
  {
    this.caseSensitive = new Dictionary();
    this.caseSensitive.useMedian();
    this.caseSensitive.insert(new File(dictFile));
    this.caseSensitive.build();

    this.caseInsensitive = new Dictionary();
    this.caseInsensitive.ignoreCase();
    this.caseInsensitive.insert(new File(dictFile));
    this.caseInsensitive.build();
  }


  /**
   * @param  count  size of the dictionary.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "dictionaryCount" })
  @Test(groups = {"dicttest"})
  public void getCount(final int count)
    throws Exception
  {
    AssertJUnit.assertEquals(count, this.caseSensitive.getCount());
    AssertJUnit.assertEquals(count, this.caseInsensitive.getCount());
  }


  /**
   * @param  word  to search for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "searchWord" })
  @Test(groups = {"dicttest"})
  public void search(final String word)
    throws Exception
  {
    AssertJUnit.assertTrue(this.caseSensitive.search(word));
    AssertJUnit.assertFalse(this.caseSensitive.search(FALSE_SEARCH));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word.toLowerCase()));
    AssertJUnit.assertFalse(this.caseInsensitive.search(FALSE_SEARCH));
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
  @Test(groups = {"dicttest"})
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
  @Test(groups = {"dicttest"})
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
  @Test(groups = {"dicttest"})
  public void bubbleSort()
    throws Exception
  {
    final Dictionary bubbleSortCS = new Dictionary();
    bubbleSortCS.useMedian();
    bubbleSortCS.insert(ANIMALS);
    bubbleSortCS.bubbleSort();
    bubbleSortCS.build();
    AssertJUnit.assertTrue(bubbleSortCS.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertFalse(bubbleSortCS.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        bubbleSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        bubbleSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));

    final Dictionary bubbleSortCI = new Dictionary();
    bubbleSortCI.ignoreCase();
    bubbleSortCI.insert(ANIMALS);
    bubbleSortCI.bubbleSort();
    bubbleSortCI.build();
    AssertJUnit.assertTrue(bubbleSortCI.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertTrue(bubbleSortCI.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        bubbleSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        bubbleSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"dicttest"})
  public void selectionSort()
    throws Exception
  {
    final Dictionary selectionSortCS = new Dictionary();
    selectionSortCS.useMedian();
    selectionSortCS.insert(ANIMALS);
    selectionSortCS.selectionSort();
    selectionSortCS.build();
    AssertJUnit.assertTrue(selectionSortCS.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertFalse(selectionSortCS.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        selectionSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        selectionSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));

    final Dictionary selectionSortCI = new Dictionary();
    selectionSortCI.ignoreCase();
    selectionSortCI.insert(ANIMALS);
    selectionSortCI.selectionSort();
    selectionSortCI.build();
    AssertJUnit.assertTrue(selectionSortCI.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertTrue(selectionSortCI.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        selectionSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        selectionSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"dicttest"})
  public void insertionSort()
    throws Exception
  {
    final Dictionary insertionSortCS = new Dictionary();
    insertionSortCS.useMedian();
    insertionSortCS.insert(ANIMALS);
    insertionSortCS.insertionSort();
    insertionSortCS.build();
    AssertJUnit.assertTrue(insertionSortCS.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertFalse(insertionSortCS.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        insertionSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        insertionSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));

    final Dictionary insertionSortCI = new Dictionary();
    insertionSortCI.ignoreCase();
    insertionSortCI.insert(ANIMALS);
    insertionSortCI.insertionSort();
    insertionSortCI.build();
    AssertJUnit.assertTrue(insertionSortCI.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertTrue(insertionSortCI.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        insertionSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        insertionSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"dicttest"})
  public void quickSort()
    throws Exception
  {
    final Dictionary quickSortCS = new Dictionary();
    quickSortCS.useMedian();
    quickSortCS.insert(ANIMALS);
    quickSortCS.quickSort();
    quickSortCS.build();
    AssertJUnit.assertTrue(quickSortCS.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertFalse(quickSortCS.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        quickSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        quickSortCS.partialSearch(ANIMAL_PARTIAL_SEARCH)));

    final Dictionary quickSortCI = new Dictionary();
    quickSortCI.ignoreCase();
    quickSortCI.insert(ANIMALS);
    quickSortCI.quickSort();
    quickSortCI.build();
    AssertJUnit.assertTrue(quickSortCI.search(ANIMAL_SEARCH_CS));
    AssertJUnit.assertTrue(quickSortCI.search(ANIMAL_SEARCH_CI));
    AssertJUnit.assertTrue(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CI,
        quickSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
    AssertJUnit.assertFalse(
      Arrays.equals(
        ANIMAL_PARTIAL_SEARCH_RESULTS_CS,
        quickSortCI.partialSearch(ANIMAL_PARTIAL_SEARCH)));
  }
}
