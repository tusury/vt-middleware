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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link WordListDictionary}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class WordListDictionaryTest extends AbstractDictionaryTest
{

  /** Test dictionary. */
  private WordListDictionary caseSensitive;

  /** Test dictionary. */
  private WordListDictionary caseInsensitive;


  /**
   * @throws  Exception  On test failure.
   */
  @BeforeClass(groups = {"wldicttest"})
  public void createDictionary()
    throws Exception
  {
    final CollectionsSort sorter = new CollectionsSort();

    final ArrayWordList awl1 = new ArrayWordList(
      new FileReader[] {new FileReader(this.fbsdFile)});
    sorter.sort(awl1);
    this.caseSensitive = new WordListDictionary();
    this.caseSensitive.setWordList(awl1);
    this.caseSensitive.initialize();

    final ArrayWordList awl2 = new ArrayWordList(
      new FileReader[] {new FileReader(this.fbsdFile)}, true);
    sorter.sort(awl2);
    this.caseInsensitive = new WordListDictionary();
    this.caseInsensitive.setWordList(awl2);
    this.caseInsensitive.initialize();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wldicttest"})
  public void getWords()
    throws Exception
  {
    FilePointerWordList fwl = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(this.fbsdFileSorted, "r")});
    AssertJUnit.assertEquals(fwl, this.caseSensitive.getWordList());
    fwl.close();
    fwl = new FilePointerWordList(
      new RandomAccessFile[] {
        new RandomAccessFile(this.fbsdFileLowerCaseSorted, "r"), });
    AssertJUnit.assertEquals(fwl, this.caseInsensitive.getWordList());
    fwl.close();
  }


  /**
   * @param  word  to search for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "fbsdSearchWord" })
  @Test(groups = {"wldicttest"})
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
  @Test(groups = {"wldicttest"}, dataProvider = "all-fbsd-words")
  public void searchAll(final String word)
    throws Exception
  {
    AssertJUnit.assertTrue(this.caseSensitive.search(word));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word));
  }
}
