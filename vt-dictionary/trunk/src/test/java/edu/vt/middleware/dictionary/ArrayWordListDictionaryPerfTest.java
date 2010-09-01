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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test to measure search performance.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class ArrayWordListDictionaryPerfTest
  extends AbstractDictionaryPerfTest
{

  /** dictionary to test. */
  private WordListDictionary wld;

  /** total time for all searches. */
  private long wldSearchTime;


  /**
   * @param  dict1  to load.
   * @param  dict2  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "webFileSorted", "fbsdFileSorted" })
  @BeforeClass(groups = {"wlperftest"})
  public void createDictionary(final String dict1, final String dict2)
    throws Exception
  {
    super.initialize(dict1, dict2);
    long t = System.currentTimeMillis();
    this.wld = new WordListDictionary(
      WordLists.createFromReader(new FileReader[] {new FileReader(webFile)}));
    t = System.currentTimeMillis() - t;
    System.out.println(
      this.wld.getClass().getSimpleName() + " (" +
      ArrayWordList.class.getSimpleName() + ") time to construct: " + t + "ms");
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"wlperftest"})
  public void closeDictionary()
    throws Exception
  {
    System.out.println(
      this.wld.getClass().getSimpleName() + " (" +
      ArrayWordList.class.getSimpleName() + ") search time: " +
      (this.wldSearchTime / 1000 / 1000) + "ms");
    System.out.println(
      this.wld.getClass().getSimpleName() + " (" +
      ArrayWordList.class.getSimpleName() + ") avg time per search: " +
      (this.wldSearchTime / 10000) + "ns");
    this.wld = null;
  }


  /**
   * @param  word  <code>String</code> to search for
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wlperftest"}, dataProvider = "search-words-web-large")
  public void wordListSearch(final String word)
    throws Exception
  {
    this.wldSearchTime += this.doSearch(this.wld, word);
  }
}
