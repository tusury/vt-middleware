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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link FileWordList}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class FileWordListTest extends AbstractWordListTest
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
    this.fileWordList = new FileWordList(new RandomAccessFile(file1, "r"));
    this.equalFileWordList = new FileWordList(new RandomAccessFile(file1, "r"));
    this.unequalFileWordList = new FileWordList(
      new RandomAccessFile(file2, "r"));
  }


  /**
   * Test for {@link FilePointerWordList#close()}.
   *
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"wltest"})
  public void close()
    throws Exception
  {
    AssertJUnit.assertTrue(
      ((FileWordList) this.fileWordList).getFile().getFD().valid());
    this.fileWordList.close();
    AssertJUnit.assertFalse(
      ((FileWordList) this.fileWordList).getFile().getFD().valid());

    AssertJUnit.assertTrue(
      ((FileWordList) this.equalFileWordList).getFile().getFD().valid());
    this.equalFileWordList.close();
    AssertJUnit.assertFalse(
      ((FileWordList) this.equalFileWordList).getFile().getFD().valid());

    AssertJUnit.assertTrue(
      ((FileWordList) this.unequalFileWordList).getFile().getFD().valid());
    this.unequalFileWordList.close();
    AssertJUnit.assertFalse(
      ((FileWordList) this.unequalFileWordList).getFile().getFD().valid());
  }


  /**
   * @param  file  dictionary to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "fbsdFile", "eignFile" })
  @Test(groups = {"wltest"})
  public void construt(final String file)
    throws Exception
  {
    try {
      new FileWordList(new RandomAccessFile(file, "r"), true, -1);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }

    try {
      new FileWordList(new RandomAccessFile(file, "r"), true, 1.1);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }

    final FileWordList fwl = new FileWordList(
      new RandomAccessFile(file, "r"), true, 0);
    AssertJUnit.assertEquals(
      TRUE_CONTAINS.toLowerCase(), fwl.get(TRUE_CONTAINS_INDEX));
    fwl.close();
  }


  /**
   * Test for {@link FileWordList#add(String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void add()
    throws Exception
  {
    try {
      this.fileWordList.add(null);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.add(FALSE_CONTAINS);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.add(TRUE_CONTAINS);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.add(0, null);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.add(0, FALSE_CONTAINS);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.add(TRUE_CONTAINS_INDEX - 5, TRUE_CONTAINS);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FileWordList#addAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void addAll()
    throws Exception
  {
    try {
      this.fileWordList.addAll(null);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    final List<String> l = new ArrayList<String>();
    l.add(null);
    try {
      this.fileWordList.addAll(l);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.addAll(Arrays.asList(FALSE_CONTAINS_ALL));
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.addAll(Arrays.asList(TRUE_CONTAINS_ALL));
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.addAll(0, null);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.addAll(0, l);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.addAll(10, Arrays.asList(FALSE_CONTAINS_ALL));
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.addAll(10, Arrays.asList(TRUE_CONTAINS_ALL));
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FileWordList#clear()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void clear()
    throws Exception
  {
    try {
      this.fileWordList.clear();
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FileWordList#iterator()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void iterator()
    throws Exception
  {
    final Iterator<String> i = this.fileWordList.iterator();
    int index = 0;
    while (i.hasNext()) {
      final String s = i.next();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      try {
        i.remove();
        AssertJUnit.fail("Should have thrown UnsupportedOperationException");
      } catch (UnsupportedOperationException e) {
        AssertJUnit.assertEquals(
          e.getClass(), UnsupportedOperationException.class);
      } catch (Exception e) {
        AssertJUnit.fail(
          "Should have thrown UnsupportedOperationException, threw " +
          e.getMessage());
      }
      index++;
    }
  }


  /**
   * Test for {@link FileWordList#listIterator()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void listIterator()
    throws Exception
  {
    ListIterator<String> i = this.fileWordList.listIterator();

    try {
      i.add("AAA");
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      i.set("AAA");
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    int index = 0;
    while (i.hasNext()) {
      final String s = i.next();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      index++;
    }

    i = this.fileWordList.listIterator();
    index = 0;
    while (i.hasNext()) {
      final String s = i.next();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      try {
        i.remove();
        AssertJUnit.fail("Should have thrown UnsupportedOperationException");
      } catch (UnsupportedOperationException e) {
        AssertJUnit.assertEquals(
          e.getClass(), UnsupportedOperationException.class);
      } catch (Exception e) {
        AssertJUnit.fail(
          "Should have thrown UnsupportedOperationException, threw " +
          e.getMessage());
      }
      index++;
    }

    i = this.fileWordList.listIterator();
    index = 0;
    AssertJUnit.assertEquals(0, i.nextIndex());
    AssertJUnit.assertEquals(-1, i.previousIndex());
    while (i.hasNext()) {
      final String s = i.next();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      AssertJUnit.assertEquals(index + 1, i.nextIndex());
      AssertJUnit.assertEquals(index, i.previousIndex());
      index++;
    }
    AssertJUnit.assertEquals(this.fileWordList.size(), i.nextIndex());
    AssertJUnit.assertEquals(this.fileWordList.size() - 1, i.previousIndex());

    index--;
    while (i.hasPrevious()) {
      final String s = i.previous();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      AssertJUnit.assertEquals(index, i.nextIndex());
      AssertJUnit.assertEquals(index - 1, i.previousIndex());
      index--;
    }

    index = 200;
    i = this.fileWordList.listIterator(index);
    while (i.hasNext()) {
      i.next();
      index++;
    }
    AssertJUnit.assertEquals(this.fileWordList.size(), index);
  }


  /**
   * Test for {@link FileWordList#remove(int)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void remove()
    throws Exception
  {
    try {
      this.fileWordList.remove(-1);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.remove(282);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.remove(106);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.remove(null);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.remove(new Integer(0));
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.remove("MIPS");
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FileWordList#removeAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void removeAll()
    throws Exception
  {
    try {
      this.fileWordList.removeAll(null);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    List<String> l = new ArrayList<String>();
    l.add(null);
    try {
      this.fileWordList.removeAll(l);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    l = Arrays.asList(TRUE_CONTAINS_ALL);
    final List<Object> cceL = new ArrayList<Object>(l);
    cceL.add(new Integer(0));
    try {
      this.fileWordList.removeAll(cceL);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.removeAll(l);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FileWordList#retainAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void retainAll()
    throws Exception
  {
    try {
      this.fileWordList.retainAll(null);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    final List<String> l = new ArrayList<String>();
    l.add(FALSE_CONTAINS);
    l.add(null);
    try {
      this.fileWordList.retainAll(l);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.retainAll(Arrays.asList(TRUE_CONTAINS_ALL));
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FileWordList#set(int, String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void set()
    throws Exception
  {
    try {
      this.fileWordList.set(-1, "MIPS");
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.set(this.fileWordList.size() + 11, "MIPS");
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.set(11, "ZZZZ");
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.set(53, "MIPS");
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }


  /**
   * Test for {@link FileWordList#subList(int, int)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void subList()
    throws Exception
  {
    try {
      this.fileWordList.subList(25, 50);
      AssertJUnit.fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      AssertJUnit.assertEquals(
        e.getClass(), UnsupportedOperationException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown UnsupportedOperationException, threw " +
        e.getMessage());
    }
  }
}
