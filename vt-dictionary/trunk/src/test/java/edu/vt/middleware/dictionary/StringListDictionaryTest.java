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
import java.io.RandomAccessFile;
import edu.vt.middleware.dictionary.sort.CollectionsSort;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link StringListDictionary}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class StringListDictionaryTest extends AbstractDictionaryTest
{

  /** Test dictionary. */
  private StringListDictionary caseSensitive;

  /** Test dictionary. */
  private StringListDictionary caseInsensitive;


  /**
   * @throws  Exception  On test failure.
   */
  @BeforeClass(groups = {"sldicttest"})
  public void createDictionary()
    throws Exception
  {
    final CollectionsSort sorter = new CollectionsSort();

    final ArrayWordList awl1 = new ArrayWordList(
      new FileReader[] {new FileReader(this.fbsdFile)});
    sorter.sort(awl1);
    this.caseSensitive = new StringListDictionary();
    this.caseSensitive.setWordList(awl1);
    this.caseSensitive.initialize();

    final ArrayWordList awl2 = new ArrayWordList(
      new FileReader[] {new FileReader(this.fbsdFile)}, true);
    sorter.sort(awl2);
    this.caseInsensitive = new StringListDictionary();
    this.caseInsensitive.setWordList(awl2);
    this.caseInsensitive.initialize();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"sldicttest"})
  public void closeDictionary()
    throws Exception
  {
    this.caseSensitive.close();
    this.caseInsensitive.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"sldicttest"})
  public void getWords()
    throws Exception
  {
    FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(this.fbsdFileSorted, "r")});
    AssertJUnit.assertEquals(fwl, this.caseSensitive.getStringList());
    fwl.close();
    fwl = new FilePointerWordList(
      new RandomAccessFile[] {
        new RandomAccessFile(this.fbsdFileLowerCaseSorted, "r"), });
    AssertJUnit.assertEquals(fwl, this.caseInsensitive.getStringList());
    fwl.close();
  }


  /**
   * @param  word  to search for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "fbsdSearchWord" })
  @Test(groups = {"sldicttest"})
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
  @Test(groups = {"sldicttest"}, dataProvider = "all-fbsd-words")
  public void searchAll(final String word)
    throws Exception
  {
    AssertJUnit.assertTrue(this.caseSensitive.search(word));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word));
  }
}
