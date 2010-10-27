/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.dictionary;

import java.io.RandomAccessFile;
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
public class FileWordListDictionaryPerfTest extends AbstractDictionaryPerfTest
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
      new FileWordList(new RandomAccessFile(webFile, "r")));
    t = System.currentTimeMillis() - t;
    System.out.println(
      this.wld.getClass().getSimpleName() + " (" +
      FileWordList.class.getSimpleName() + ") time to construct: " + t + "ms");
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"wlperftest"})
  public void closeDictionary()
    throws Exception
  {
    System.out.println(
      this.wld.getClass().getSimpleName() + " (" +
      FileWordList.class.getSimpleName() + ") search time: " +
      (this.wldSearchTime / 1000 / 1000) + "ms");
    System.out.println(
      this.wld.getClass().getSimpleName() + " (" +
      FileWordList.class.getSimpleName() + ") avg time per search: " +
      (this.wldSearchTime / 1000 / 1000 / 10) + "ms");
    this.wld = null;
  }


  /**
   * @param  word  <code>String</code> to search for
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"wlperftest"},
    dataProvider = "search-words-web-small"
  )
  public void wordListSearch(final String word)
    throws Exception
  {
    this.wldSearchTime += this.doSearch(this.wld, word);
  }
}
