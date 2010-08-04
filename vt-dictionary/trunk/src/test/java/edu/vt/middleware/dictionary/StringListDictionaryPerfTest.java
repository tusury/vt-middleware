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
public class StringListDictionaryPerfTest extends AbstractDictionaryPerfTest
{

  /** dictionary to test. */
  private StringListDictionary sld;

  /** total time for all searches. */
  private long sldSearchTime;


  /**
   * @param  dict1  to load.
   * @param  dict2  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "webFileSorted", "fbsdFileSorted" })
  @BeforeClass(groups = {"slperftest"})
  public void createDictionary(final String dict1, final String dict2)
    throws Exception
  {
    super.initialize(dict1, dict2);
    long t = System.currentTimeMillis();
    this.sld = new StringListDictionary();
    this.sld.setWordList(
      new ArrayWordList(new FileReader[] {new FileReader(webFile)}));
    this.sld.initialize();
    t = System.currentTimeMillis() - t;
    System.out.println(
      this.sld.getClass().getSimpleName() + " time to construct: " + t + "ms");
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"slperftest"})
  public void closeDictionary()
    throws Exception
  {
    this.sld.close();
    System.out.println(
      this.sld.getClass().getSimpleName() + " search time: " +
      (this.sldSearchTime / 1000 / 1000) + "ms");
    System.out.println(
      this.sld.getClass().getSimpleName() + " avg time per search: " +
      (this.sldSearchTime / 10000) + "ns");
  }


  /**
   * @param  word  <code>String</code> to search for
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"slperftest"}, dataProvider = "search-words-web-large")
  public void stringListSearch(final String word)
    throws Exception
  {
    this.sldSearchTime += this.doSearch(this.sld, word);
  }
}
