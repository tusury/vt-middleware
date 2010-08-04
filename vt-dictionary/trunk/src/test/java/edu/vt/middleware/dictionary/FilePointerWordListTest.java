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

import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link FilePointerWordList}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class FilePointerWordListTest extends AbstractWordListTest
{


  /**
   * @param  file1  dictionary to load.
   * @param  file2  dictionary to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "fbsdFile", "eignFile" })
  @BeforeClass(groups = {"wltest"})
  public void createFileWordList(final String file1, final String file2)
    throws Exception
  {
    this.fileWordList = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(file1, "r")});
    this.equalFileWordList = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(file1, "r")});
    this.unequalFileWordList = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(file2, "r")});
  }


  /**
   * Test for {@link FilePointerFilePointerWordList#close()}.
   *
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"wltest"})
  public void close()
    throws Exception
  {
    AssertJUnit.assertTrue(this.fileWordList.size() > 0);
    AssertJUnit.assertTrue(!this.fileWordList.isEmpty());
    this.fileWordList.clear();
    AssertJUnit.assertTrue(this.fileWordList.size() == 0);
    AssertJUnit.assertTrue(this.fileWordList.isEmpty());

    for (RandomAccessFile raf :
         ((FilePointerWordList) this.fileWordList).getFiles()) {
      AssertJUnit.assertTrue(raf.getFD().valid());
    }
    this.fileWordList.close();
    for (RandomAccessFile raf :
         ((FilePointerWordList) this.fileWordList).getFiles()) {
      AssertJUnit.assertFalse(raf.getFD().valid());
    }

    for (RandomAccessFile raf :
         ((FilePointerWordList) this.equalFileWordList).getFiles()) {
      AssertJUnit.assertTrue(raf.getFD().valid());
    }
    this.equalFileWordList.close();
    for (RandomAccessFile raf :
         ((FilePointerWordList) this.equalFileWordList).getFiles()) {
      AssertJUnit.assertFalse(raf.getFD().valid());
    }

    for (RandomAccessFile raf :
         ((FilePointerWordList) this.unequalFileWordList).getFiles()) {
      AssertJUnit.assertTrue(raf.getFD().valid());
    }
    this.unequalFileWordList.close();
    for (RandomAccessFile raf :
         ((FilePointerWordList) this.unequalFileWordList).getFiles()) {
      AssertJUnit.assertFalse(raf.getFD().valid());
    }
  }


  /**
   * Test for {@link FilePointerWordList#add(String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void add()
    throws Exception
  {
    super.add();
    try {
      this.fileWordList.add(FALSE_CONTAINS);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }

    try {
      this.fileWordList.add(0, FALSE_CONTAINS);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }
  }


  /**
   * Test for {@link FilePointerWordList#addAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void addAll()
    throws Exception
  {
    super.addAll();
    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    try {
      fwl.addAll(Arrays.asList(FALSE_CONTAINS_ALL));
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    try {
      fwl.addAll(10, Arrays.asList(FALSE_CONTAINS_ALL));
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }
  }


  /**
   * Test for {@link FilePointerWordList#listIterator()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void listIterator()
    throws Exception
  {
    super.listIterator();
    final List<String> fwl =
      this.fileWordList.subList(0, this.fileWordList.size());
    final ListIterator<String> i = fwl.listIterator();
    try {
      i.add("AAA");
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " +
        e.getMessage());
    }
    try {
      i.set("BBB");
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FilePointerWordList#set(int, String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void set()
    throws Exception
  {
    super.set();
    try {
      this.fileWordList.set(11, "ZZZZ");
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " +
        e.getMessage());
    }
  }
}
