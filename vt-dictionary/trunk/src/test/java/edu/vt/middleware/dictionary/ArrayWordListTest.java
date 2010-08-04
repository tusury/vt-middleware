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

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ArrayWordList}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class ArrayWordListTest extends AbstractWordListTest
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
    this.fileWordList = new ArrayWordList(
      new FileReader[] {new FileReader(file1)});
    this.equalFileWordList = new ArrayWordList(
      new FileReader[] {new FileReader(file1)});
    this.unequalFileWordList = new ArrayWordList(
      new FileReader[] {new FileReader(file2)});
  }


  /**
   * Test for {@link ArrayArrayWordList#close()}.
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

    AssertJUnit.assertNotNull(((ArrayWordList) this.fileWordList).getWords());
    this.fileWordList.close();
    AssertJUnit.assertNull(((ArrayWordList) this.fileWordList).getWords());

    AssertJUnit.assertNotNull(
      ((ArrayWordList) this.equalFileWordList).getWords());
    this.equalFileWordList.close();
    AssertJUnit.assertNull(((ArrayWordList) this.equalFileWordList).getWords());

    AssertJUnit.assertNotNull(
      ((ArrayWordList) this.unequalFileWordList).getWords());
    this.unequalFileWordList.close();
    AssertJUnit.assertNull(
      ((ArrayWordList) this.unequalFileWordList).getWords());
  }


  /**
   * Test for {@link ArrayWordList#add(String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void add()
    throws Exception
  {
    super.add();
    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    fwl.add(FALSE_CONTAINS);
    AssertJUnit.assertEquals(fwl.get(fwl.size() - 1), FALSE_CONTAINS);

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    fwl.add(3, FALSE_CONTAINS);
    AssertJUnit.assertEquals(fwl.get(3), FALSE_CONTAINS);
  }


  /**
   * Test for {@link ArrayWordList#addAll(java.util.Collection)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void addAll()
    throws Exception
  {
    super.addAll();
    List<String> fwl = this.fileWordList.subList(0, this.fileWordList.size());
    fwl.addAll(Arrays.asList(FALSE_CONTAINS_ALL));
    AssertJUnit.assertEquals(
      fwl.subList(0, this.fileWordList.size()), this.fileWordList);
    AssertJUnit.assertEquals(
      fwl.subList(this.fileWordList.size(), fwl.size()),
      Arrays.asList(FALSE_CONTAINS_ALL));

    fwl = this.fileWordList.subList(0, this.fileWordList.size());
    fwl.addAll(10, Arrays.asList(FALSE_CONTAINS_ALL));
  }


  /**
   * Test for {@link ArrayWordList#listIterator()}.
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
    i.add("AAA");
    AssertJUnit.assertEquals(fwl.get(0), "AAA");
    i.next();
    i.next();
    i.next();
    i.set("BBB");
    AssertJUnit.assertEquals(fwl.get(3), "BBB");
  }


  /**
   * Test for {@link ArrayWordList#set(int, String)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void set()
    throws Exception
  {
    super.set();
    final List<String> fwl =
      this.fileWordList.subList(0, this.fileWordList.size());
    fwl.set(11, "ZZZZ");
    AssertJUnit.assertEquals(fwl.get(11), "ZZZZ");
  }
}
