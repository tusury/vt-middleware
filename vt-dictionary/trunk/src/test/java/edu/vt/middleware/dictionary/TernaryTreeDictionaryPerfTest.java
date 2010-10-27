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
public class TernaryTreeDictionaryPerfTest extends AbstractDictionaryPerfTest
{

  /** dictionary to test. */
  private TernaryTreeDictionary ttd;

  /** total time for all searches. */
  private long ttdSearchTime;


  /**
   * @param  dict1  to load.
   * @param  dict2  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "webFileSorted", "fbsdFileSorted" })
  @BeforeClass(groups = {"ttperftest"})
  public void createDictionary(final String dict1, final String dict2)
    throws Exception
  {
    super.initialize(dict1, dict2);

    long t = System.currentTimeMillis();
    this.ttd = new TernaryTreeDictionary(
      new FileWordList(new RandomAccessFile(webFile, "r")));
    t = System.currentTimeMillis() - t;
    System.out.println(
      this.ttd.getClass().getSimpleName() + " time to construct: " + t + "ms");
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"ttperftest"})
  public void closeDictionary()
    throws Exception
  {
    System.out.println(
      this.ttd.getClass().getSimpleName() + " search time: " +
      (this.ttdSearchTime / 1000 / 1000) + "ms");
    System.out.println(
      this.ttd.getClass().getSimpleName() + " avg time per search: " +
      (this.ttdSearchTime / 10000) + "ns");
    this.ttd = null;
  }


  /**
   * @param  word  <code>String</code> to search for
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"ttperftest"},
    dataProvider = "search-words-web-large"
  )
  public void ternaryTreeSearch(final String word)
    throws Exception
  {
    this.ttdSearchTime += this.doSearch(this.ttd, word);
  }
}
