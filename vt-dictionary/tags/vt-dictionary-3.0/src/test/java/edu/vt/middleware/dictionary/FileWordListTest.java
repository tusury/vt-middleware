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
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link FileWordList}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class FileWordListTest extends AbstractWordListTest
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
    this.wordList = new FileWordList(new RandomAccessFile(file, "r"));
  }


  /**
   * Test for {@link FileWordList#close()}.
   *
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"wltest"})
  public void closeWordList()
    throws Exception
  {
    AssertJUnit.assertTrue(
      ((FileWordList) this.wordList).getFile().getFD().valid());
    ((FileWordList) this.wordList).close();
    AssertJUnit.assertFalse(
      ((FileWordList) this.wordList).getFile().getFD().valid());
  }


  /**
   * @param  file1  dictionary to load.
   * @param  file2  dictionary to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "fbsdFileSorted", "fbsdFileLowerCaseSorted" })
  @Test(groups = {"wltest"})
  public void construt(final String file1, final String file2)
    throws Exception
  {
    try {
      new FileWordList(new RandomAccessFile(file1, "r"), true, -1);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }

    try {
      new FileWordList(new RandomAccessFile(file1, "r"), true, 101);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(e.getClass(), IllegalArgumentException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e.getMessage());
    }

    FileWordList fwl = new FileWordList(
      new RandomAccessFile(file1, "r"),
      true,
      0);
    fwl.close();

    fwl = new FileWordList(new RandomAccessFile(file2, "r"), false, 0);
    fwl.close();
  }
}
