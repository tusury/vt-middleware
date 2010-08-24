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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

/**
 * Unit test for {@link ArrayWordList}.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public class ArrayWordListTest extends AbstractWordListTest
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
    this.wordList = WordListUtils.createFromFile(new FileReader(file1));
  }

}
