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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Common unit tests for {@link WordList} implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public abstract class AbstractWordListTest
{

  /** False contains. */
  public static final String FALSE_CONTAINS = "not-found-in-the-list";

  /** True contains. */
  public static final String TRUE_CONTAINS = "LinuxDoc";

  /** True contains index. */
  public static final int TRUE_CONTAINS_INDEX = 103;

  /** True contains all. */
  public static final String[] FALSE_CONTAINS_ALL = new String[] {
    "TrueType",
    "LDAP",
    "Estonia",
    "NDIS",
    "Vidrine",
    "http",
    "ISDN",
    "shareware",
    "libc",
    "NetBIOS",
    "downtime",
  };

  /** True contains all. */
  public static final String[] TRUE_CONTAINS_ALL = new String[] {
    "TrueType",
    "CDROMs",
    "Estonia",
    "Vidrine",
    "ISDN",
    "shareware",
    "MPEG",
    "NetBIOS",
    "OpenSSL",
    "downtime",
  };

  /** Test list. */
  protected WordList fileWordList;

  /** Test list. */
  protected WordList equalFileWordList;

  /** Test list. */
  protected WordList unequalFileWordList;


  /**
   * Test for {@link WordList#add(String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void add()
    throws Exception
  {
    try {
      this.fileWordList.add(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertTrue(fwl.add(TRUE_CONTAINS));
    AssertJUnit.assertEquals(TRUE_CONTAINS_INDEX, fwl.indexOf(TRUE_CONTAINS));
    AssertJUnit.assertEquals(fwl.size() - 1, fwl.lastIndexOf(TRUE_CONTAINS));
    AssertJUnit.assertEquals(TRUE_CONTAINS, fwl.get(TRUE_CONTAINS_INDEX));
    AssertJUnit.assertEquals(TRUE_CONTAINS, fwl.get(fwl.size() - 1));

    try {
      this.fileWordList.add(-1, TRUE_CONTAINS);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.add(282, TRUE_CONTAINS);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }
    try {
      this.fileWordList.add(0, null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    fwl.add(TRUE_CONTAINS_INDEX - 5, TRUE_CONTAINS);
    AssertJUnit.assertEquals(
      TRUE_CONTAINS_INDEX - 5, fwl.indexOf(TRUE_CONTAINS));
    AssertJUnit.assertEquals(
      TRUE_CONTAINS_INDEX + 1, fwl.lastIndexOf(TRUE_CONTAINS));
    AssertJUnit.assertEquals(TRUE_CONTAINS, fwl.get(TRUE_CONTAINS_INDEX - 5));
    AssertJUnit.assertEquals(TRUE_CONTAINS, fwl.get(TRUE_CONTAINS_INDEX + 1));
  }


  /**
   * Test for {@link WordList#addAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void addAll()
    throws Exception
  {
    try {
      this.fileWordList.addAll(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    final List<String> l = new ArrayList<String>();
    l.add(null);
    try {
      this.fileWordList.addAll(l);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.toString());
    }

    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertTrue(fwl.addAll(Arrays.asList(TRUE_CONTAINS_ALL)));
    AssertJUnit.assertEquals(
      fwl.size() - this.fileWordList.size(), TRUE_CONTAINS_ALL.length);
    int index = 0;
    for (int i = this.fileWordList.size(); i < fwl.size(); i++) {
      AssertJUnit.assertEquals(fwl.get(i), TRUE_CONTAINS_ALL[index++]);
    }

    try {
      this.fileWordList.addAll(0, null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    try {
      this.fileWordList.addAll(0, l);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.toString());
    }

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertTrue(fwl.addAll(10, Arrays.asList(TRUE_CONTAINS_ALL)));
    AssertJUnit.assertEquals(
      fwl.size() - this.fileWordList.size(), TRUE_CONTAINS_ALL.length);
    index = 0;
    for (int i = 10; i < TRUE_CONTAINS_ALL.length; i++) {
      AssertJUnit.assertEquals(fwl.get(i), TRUE_CONTAINS_ALL[index++]);
    }
  }


  /**
   * Test for {@link WordList#contains(Object)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void contains()
    throws Exception
  {
    try {
      this.fileWordList.contains(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    try {
      this.fileWordList.contains(new Integer(0));
      AssertJUnit.fail("Should have thrown ClassCastException");
    } catch (ClassCastException e) {
      AssertJUnit.assertEquals(e.getClass(), ClassCastException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown ClassCastException, threw " + e.getMessage());
    }

    AssertJUnit.assertTrue(this.fileWordList.contains(TRUE_CONTAINS));
    AssertJUnit.assertFalse(this.fileWordList.contains(FALSE_CONTAINS));
  }


  /**
   * Test for {@link WordList#containsAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void containsAll()
    throws Exception
  {
    try {
      this.fileWordList.containsAll(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    List<String> l = new ArrayList<String>();
    l.add(null);
    try {
      this.fileWordList.containsAll(l);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.toString());
    }

    l = Arrays.asList(TRUE_CONTAINS_ALL);
    final List<Object> cceL = new ArrayList<Object>(l);
    cceL.add(new Integer(0));
    try {
      this.fileWordList.containsAll(cceL);
      AssertJUnit.fail("Should have thrown ClassCastException");
    } catch (ClassCastException e) {
      AssertJUnit.assertEquals(e.getClass(), ClassCastException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown ClassCastException, threw " + e.toString());
    }

    AssertJUnit.assertTrue(this.fileWordList.containsAll(l));
    AssertJUnit.assertFalse(
      this.fileWordList.containsAll(Arrays.asList(FALSE_CONTAINS_ALL)));
  }


  /**
   * Test for {@link WordList#equals(Object)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void equals()
    throws Exception
  {
    AssertJUnit.assertFalse(this.fileWordList.equals(null));
    AssertJUnit.assertFalse(this.fileWordList.equals(new Integer(0)));
    AssertJUnit.assertFalse(this.fileWordList.equals(this.unequalFileWordList));
    AssertJUnit.assertTrue(this.fileWordList.equals(this.fileWordList));
    AssertJUnit.assertTrue(this.fileWordList.equals(this.equalFileWordList));
  }


  /**
   * Test for {@link WordList#get(int)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void get()
    throws Exception
  {
    try {
      this.fileWordList.get(-1);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.get(282);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    AssertJUnit.assertEquals("ABI", this.fileWordList.get(0));
    AssertJUnit.assertEquals("MIPS", this.fileWordList.get(107));
    AssertJUnit.assertEquals("Wemm", this.fileWordList.get(281));
  }


  /**
   * Test for {@link WordList#indexOf(Object)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void indexOf()
    throws Exception
  {
    try {
      this.fileWordList.indexOf(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    try {
      this.fileWordList.indexOf(new Integer(0));
      AssertJUnit.fail("Should have thrown ClassCastException");
    } catch (ClassCastException e) {
      AssertJUnit.assertEquals(e.getClass(), ClassCastException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown ClassCastException, threw " + e.getMessage());
    }

    AssertJUnit.assertEquals(-1, this.fileWordList.indexOf("ZZZZ"));
    AssertJUnit.assertEquals(0, this.fileWordList.indexOf("ABI"));
    AssertJUnit.assertEquals(107, this.fileWordList.indexOf("MIPS"));
    AssertJUnit.assertEquals(281, this.fileWordList.indexOf("Wemm"));
  }


  /**
   * Test for {@link WordList#iterator()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void iterator()
    throws Exception
  {
    final List<String> fwl =
      this.fileWordList.subList(0, this.fileWordList.size());
    final Iterator<String> i = fwl.iterator();
    int index = 0;
    while (i.hasNext()) {
      final String s = i.next();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      i.remove();
      index++;
    }
    AssertJUnit.assertEquals(0, fwl.size());
  }


  /**
   * Test for {@link WordList#lastIndexOf(Object)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void lastIndexOf()
    throws Exception
  {
    try {
      this.fileWordList.lastIndexOf(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    try {
      this.fileWordList.lastIndexOf(new Integer(0));
      AssertJUnit.fail("Should have thrown ClassCastException");
    } catch (ClassCastException e) {
      AssertJUnit.assertEquals(e.getClass(), ClassCastException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown ClassCastException, threw " + e.getMessage());
    }

    AssertJUnit.assertEquals(-1, this.fileWordList.lastIndexOf("ZZZZ"));
    AssertJUnit.assertEquals(0, this.fileWordList.lastIndexOf("ABI"));
    AssertJUnit.assertEquals(107, this.fileWordList.lastIndexOf("MIPS"));
    AssertJUnit.assertEquals(281, this.fileWordList.lastIndexOf("Wemm"));
  }


  /**
   * Test for {@link WordList#listIterator()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void listIterator()
    throws Exception
  {
    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    ListIterator<String> i = fwl.listIterator();

    int index = 0;
    while (i.hasNext()) {
      if (index == 0) {
        i.add(TRUE_CONTAINS);
        AssertJUnit.assertEquals(TRUE_CONTAINS, i.previous());
        AssertJUnit.assertEquals(this.fileWordList.size() + 1, fwl.size());
        i.next();
      } else if (index == this.fileWordList.size() / 2) {
        i.add(TRUE_CONTAINS);
        AssertJUnit.assertEquals(TRUE_CONTAINS, i.previous());
        AssertJUnit.assertEquals(this.fileWordList.size() + 2, fwl.size());
        i.next();
      }
      final String s = i.next();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      index++;
    }
    i.add(TRUE_CONTAINS);
    AssertJUnit.assertEquals(TRUE_CONTAINS, i.previous());
    AssertJUnit.assertEquals(this.fileWordList.size() + 3, fwl.size());

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    i = fwl.listIterator();
    index = 0;
    while (i.hasNext()) {
      final String s = i.next();
      AssertJUnit.assertEquals(this.fileWordList.get(index), s);
      i.remove();
      index++;
    }
    AssertJUnit.assertEquals(0, fwl.size());

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    i = fwl.listIterator();
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

    int size = fwl.size();
    while (i.hasNext()) {
      i.next();
      i.remove();
      size--;
      AssertJUnit.assertEquals(size, fwl.size());
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
   * Test for {@link WordList#remove(int)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void remove()
    throws Exception
  {
    try {
      this.fileWordList.remove(-1);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.remove(282);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertEquals("ABI", fwl.remove(0));
    AssertJUnit.assertEquals("MIPS", fwl.remove(106));
    AssertJUnit.assertEquals("Wemm", fwl.remove(279));

    try {
      this.fileWordList.remove(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    try {
      this.fileWordList.remove(new Integer(0));
      AssertJUnit.fail("Should have thrown ClassCastException");
    } catch (ClassCastException e) {
      AssertJUnit.assertEquals(e.getClass(), ClassCastException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown ClassCastException, threw " + e.getMessage());
    }

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertFalse(fwl.remove("ZZZZ"));
    AssertJUnit.assertTrue(fwl.remove("ABI"));
    AssertJUnit.assertTrue(fwl.remove("MIPS"));
    AssertJUnit.assertTrue(fwl.remove("Wemm"));
    AssertJUnit.assertEquals(this.fileWordList.size() - 3, fwl.size());
  }


  /**
   * Test for {@link WordList#removeAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void removeAll()
    throws Exception
  {
    try {
      this.fileWordList.removeAll(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    List<String> l = new ArrayList<String>();
    l.add(null);
    try {
      this.fileWordList.removeAll(l);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.toString());
    }

    l = Arrays.asList(TRUE_CONTAINS_ALL);
    final List<Object> cceL = new ArrayList<Object>(l);
    cceL.add(new Integer(0));
    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    try {
      fwl.removeAll(cceL);
      AssertJUnit.fail("Should have thrown ClassCastException");
    } catch (ClassCastException e) {
      AssertJUnit.assertEquals(e.getClass(), ClassCastException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown ClassCastException, threw " + e.getMessage());
    }

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertTrue(fwl.removeAll(l));
    AssertJUnit.assertFalse(
      fwl.removeAll(Arrays.asList(new String[] {"XXXX", "YYYY", "ZZZZ"})));
  }


  /**
   * Test for {@link WordList#retainAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void retainAll()
    throws Exception
  {
    try {
      this.fileWordList.retainAll(null);
      AssertJUnit.fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
      AssertJUnit.assertEquals(e.getClass(), NullPointerException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown NullPointerException, threw " + e.getMessage());
    }

    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    final List<String> l = new ArrayList<String>();
    l.add(FALSE_CONTAINS);
    l.add(null);
    AssertJUnit.assertTrue(fwl.retainAll(l));
    AssertJUnit.assertEquals(0, fwl.size());

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertFalse(fwl.retainAll(this.fileWordList));

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    AssertJUnit.assertTrue(fwl.retainAll(Arrays.asList(TRUE_CONTAINS_ALL)));
    AssertJUnit.assertEquals(TRUE_CONTAINS_ALL.length, fwl.size());
  }


  /**
   * Test for {@link WordList#set(int, String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void set()
    throws Exception
  {
    try {
      this.fileWordList.set(-1, "MIPS");
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    try {
      this.fileWordList.set(this.fileWordList.size() + 11, "MIPS");
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    final List<String> fwl = this.fileWordList.subList(
      0, this.fileWordList.size());
    AssertJUnit.assertEquals("FDDI", fwl.get(53));
    AssertJUnit.assertEquals("FDDI", fwl.set(53, "MIPS"));
    AssertJUnit.assertEquals("MIPS", fwl.get(53));
    AssertJUnit.assertEquals("MIPS", fwl.get(107));
  }


  /**
   * Test for {@link WordList#subList(int, int)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void subList()
    throws Exception
  {
    final List<String> fwl = this.fileWordList.subList(25, 50);
    AssertJUnit.assertEquals(25, fwl.size());
    AssertJUnit.assertEquals(this.fileWordList.get(25), fwl.get(0));
    AssertJUnit.assertEquals(this.fileWordList.get(49), fwl.get(24));
  }



  /**
   * Test for {@link WordList#toArray()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void toArray()
    throws Exception
  {
    final Object[] o = this.fileWordList.toArray();
    AssertJUnit.assertEquals(o.length, this.fileWordList.size());
    for (int i = 0; i < o.length; i++) {
      AssertJUnit.assertEquals(o[i], this.fileWordList.get(i));
    }

    try {
      this.fileWordList.toArray(new Integer[0]);
      AssertJUnit.fail("Should have thrown ArrayStoreException");
    } catch (ArrayStoreException e) {
      AssertJUnit.assertEquals(e.getClass(), ArrayStoreException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown ArrayStoreException, threw " + e.getMessage());
    }

    String[] s = this.fileWordList.toArray(new String[50]);
    AssertJUnit.assertEquals(s.length, this.fileWordList.size());
    for (int i = 0; i < s.length; i++) {
      AssertJUnit.assertEquals(s[i], this.fileWordList.get(i));
    }

    s = this.fileWordList.toArray(new String[500]);
    AssertJUnit.assertEquals(s.length, 500);
    for (int i = 0; i < this.fileWordList.size(); i++) {
      AssertJUnit.assertEquals(s[i], this.fileWordList.get(i));
    }
    AssertJUnit.assertNull(s[this.fileWordList.size()]);

    s = this.fileWordList.toArray(new String[this.fileWordList.size()]);
    AssertJUnit.assertEquals(o.length, this.fileWordList.size());
    for (int i = 0; i < s.length; i++) {
      AssertJUnit.assertEquals(s[i], this.fileWordList.get(i));
    }
  }
}
