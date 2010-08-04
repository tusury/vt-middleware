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

import java.io.RandomAccessFile;
import java.util.List;
import edu.vt.middleware.dictionary.FilePointerWordList;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Sorter} implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class SorterTest
{

  /** word list to use for comparison. */
  private FilePointerWordList sortedList;


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
    this.sortedList = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(dict, "r")});
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"sorttest"})
  public void destroy()
    throws Exception
  {
    this.sortedList.close();
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
    final FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(dict, "r")});
    AssertJUnit.assertNotSame(this.sortedList, fwl);
    this.doSort(new BubbleSort(), fwl);
    AssertJUnit.assertEquals(this.sortedList, fwl);
    fwl.close();
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
    final FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(dict, "r")});
    AssertJUnit.assertNotSame(this.sortedList, fwl);
    this.doSort(new CollectionsSort(), fwl);
    AssertJUnit.assertEquals(this.sortedList, fwl);
    fwl.close();
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
    final FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(dict, "r")});
    AssertJUnit.assertNotSame(this.sortedList, fwl);
    this.doSort(new InsertionSort(), fwl);
    AssertJUnit.assertEquals(this.sortedList, fwl);
    fwl.close();
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
    final FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(dict, "r")});
    AssertJUnit.assertNotSame(this.sortedList, fwl);
    this.doSort(new QuickSort(), fwl);
    AssertJUnit.assertEquals(this.sortedList, fwl);
    fwl.close();
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
    final FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(dict, "r")});
    AssertJUnit.assertNotSame(this.sortedList, fwl);
    this.doSort(new SelectionSort(), fwl);
    AssertJUnit.assertEquals(this.sortedList, fwl);
    fwl.close();
  }


  /**
   * Sorts the supplied list with the supplied sorter.
   *
   * @param  s  <code>Sorter</code> to sort with
   * @param  l  <code>List</code> to sort
   */
  public void doSort(final Sorter<List<String>> s, final List<String> l)
  {
    long t = System.currentTimeMillis();
    s.sort(l);
    t = System.currentTimeMillis() - t;
    System.out.println(
      s.getClass().getSimpleName() + " sort time (" + l.size() + "): " +
      t + "ms");
  }
}
