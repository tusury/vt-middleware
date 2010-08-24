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

import java.io.RandomAccessFile;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

/**
 * Unit test for {@link FilePointerWordList}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class FilePointerWordListTest extends AbstractWordListTest
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
    this.wordList = new FilePointerWordList(
      new RandomAccessFile[] {new RandomAccessFile(file1, "r")});
  }


  /**
   * Cleans up after tests run.
   *
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"wltest"})
  public void cleanUp() throws Exception
  {
    ((FilePointerWordList) this.wordList).close();
  }
}
