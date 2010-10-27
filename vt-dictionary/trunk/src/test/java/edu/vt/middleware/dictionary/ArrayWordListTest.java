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
   * @param  file  dictionary to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "fbsdFileSorted" })
  @BeforeClass(groups = {"wltest"})
  public void createWordList(final String file)
    throws Exception
  {
    this.wordList = WordLists.createFromReader(
      new FileReader[] {new FileReader(file)});
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"wltest"})
  public void closeWordList()
    throws Exception
  {
    this.wordList = null;
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"wltest"})
  public void construt()
    throws Exception
  {
    try {
      new ArrayWordList(null, true);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }

    final String[] arrayWithNull = new String[] {"a", "b", null, "c"};
    try {
      new ArrayWordList(arrayWithNull, true);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }
  }
}
