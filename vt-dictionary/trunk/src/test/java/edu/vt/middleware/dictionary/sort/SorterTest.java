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
package edu.vt.middleware.dictionary.sort;

import java.util.Arrays;
import edu.vt.middleware.dictionary.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ArraySorter} implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class SorterTest
{

  /** word list to use for comparison. */
  private String[] sortedArray;


  /**
   * @param  dict  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "fbsdFileSorted" })
  @BeforeClass(groups = {"sorttest"})
  public void create(final String dict)
    throws Exception
  {
    this.sortedArray = TestUtil.fileToArray(dict);
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"sorttest"})
  public void destroy()
    throws Exception
  {
    this.sortedArray = null;
  }


  /**
   * @param  dict  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({"fbsdFile" })
  @Test(groups = {"sorttest"})
  public void bubbleSort(final String dict)
    throws Exception
  {
    final String[] array = TestUtil.fileToArray(dict);
    AssertJUnit.assertFalse(Arrays.equals(this.sortedArray, array));
    this.doSort(new BubbleSort(), array);
    AssertJUnit.assertTrue(Arrays.equals(this.sortedArray, array));
  }


  /**
   * @param  dict  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({"fbsdFile" })
  @Test(groups = {"sorttest"})
  public void collectionsSort(final String dict)
    throws Exception
  {
    final String[] array = TestUtil.fileToArray(dict);
    AssertJUnit.assertFalse(Arrays.equals(this.sortedArray, array));
    this.doSort(new ArraysSort(), array);
    AssertJUnit.assertTrue(Arrays.equals(this.sortedArray, array));
  }


  /**
   * @param  dict  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({"fbsdFile" })
  @Test(groups = {"sorttest"})
  public void insertionSort(final String dict)
    throws Exception
  {
    final String[] array = TestUtil.fileToArray(dict);
    AssertJUnit.assertFalse(Arrays.equals(this.sortedArray, array));
    this.doSort(new InsertionSort(), array);
    AssertJUnit.assertTrue(Arrays.equals(this.sortedArray, array));
  }


  /**
   * @param  dict  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({"fbsdFile" })
  @Test(groups = {"sorttest"})
  public void quickSort(final String dict)
    throws Exception
  {
    final String[] array = TestUtil.fileToArray(dict);
    AssertJUnit.assertFalse(Arrays.equals(this.sortedArray, array));
    this.doSort(new QuickSort(), array);
    AssertJUnit.assertTrue(Arrays.equals(this.sortedArray, array));
  }


  /**
   * @param  dict  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({"fbsdFile" })
  @Test(groups = {"sorttest"})
  public void selectionSort(final String dict)
    throws Exception
  {
    final String[] array = TestUtil.fileToArray(dict);
    AssertJUnit.assertFalse(Arrays.equals(this.sortedArray, array));
    this.doSort(new SelectionSort(), array);
    AssertJUnit.assertTrue(Arrays.equals(this.sortedArray, array));
  }


  /**
   * Sorts the supplied list with the supplied sorter.
   *
   * @param  s  <code>Sorter</code> to sort with
   * @param  array  <code>String[]</code> to sort
   */
  public void doSort(final ArraySorter s, final String[] array)
  {
    long t = System.nanoTime();
    s.sort(array);
    t = System.nanoTime() - t;
    System.out.println(
      s.getClass().getSimpleName() + " sort time (" + array.length + "): " +
      t + "ns");
  }
}
