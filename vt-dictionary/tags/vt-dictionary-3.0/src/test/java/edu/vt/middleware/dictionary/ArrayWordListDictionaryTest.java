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

import java.io.FileReader;
import edu.vt.middleware.dictionary.sort.ArraysSort;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link WordListDictionary}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class ArrayWordListDictionaryTest extends AbstractDictionaryTest
{

  /** Test dictionary. */
  private WordListDictionary caseSensitive;

  /** Test dictionary. */
  private WordListDictionary caseInsensitive;


  /** @throws  Exception  On test failure. */
  @BeforeClass(groups = {"wldicttest"})
  public void createDictionary()
    throws Exception
  {
    final ArrayWordList awl1 = WordLists.createFromReader(
      new FileReader[] {new FileReader(this.fbsdFile)},
      true,
      new ArraysSort());
    this.caseSensitive = new WordListDictionary(awl1);

    final ArrayWordList awl2 = WordLists.createFromReader(
      new FileReader[] {new FileReader(this.fbsdFile)},
      false,
      new ArraysSort());
    this.caseInsensitive = new WordListDictionary(awl2);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"wldicttest"})
  public void closeDictionary()
    throws Exception
  {
    this.caseSensitive = null;
    this.caseInsensitive = null;
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
  @Test(
    groups = {"wldicttest"},
    dataProvider = "all-fbsd-words"
  )
  public void searchAll(final String word)
    throws Exception
  {
    AssertJUnit.assertTrue(this.caseSensitive.search(word));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word.toLowerCase()));
    AssertJUnit.assertTrue(this.caseInsensitive.search(word.toUpperCase()));
  }
}
