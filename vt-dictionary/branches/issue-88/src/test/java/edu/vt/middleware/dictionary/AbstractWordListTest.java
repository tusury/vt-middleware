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

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Common unit tests for {@link WordList} implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 166 $
 */
public abstract class AbstractWordListTest
{

  /** False contains. */
  public static final String FALSE_CONTAINS = "not-found-in-the-list";

  /** True contains. */
  public static final String TRUE_CONTAINS = "LinuxDoc";

  /** True contains index. */
  public static final int TRUE_CONTAINS_INDEX = 103;

  /** True contains all. */
  public static final String[] FALSE_CONTAINS_ALL = new String[] {
    "TrueType",
    "LDAP",
    "Estonia",
    "NDIS",
    "Vidrine",
    "http",
    "ISDN",
    "shareware",
    "libc",
    "NetBIOS",
    "downtime",
  };

  /** True contains all. */
  public static final String[] TRUE_CONTAINS_ALL = new String[] {
    "TrueType",
    "CDROMs",
    "Estonia",
    "Vidrine",
    "ISDN",
    "shareware",
    "MPEG",
    "NetBIOS",
    "OpenSSL",
    "downtime",
  };

  /** Test list. */
  protected WordList wordList;


  /**
   * Test for {@link WordList#get(int)}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void get()
    throws Exception
  {
    try {
      this.wordList.get(-1);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    try {
      this.wordList.get(282);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      AssertJUnit.assertEquals(
        e.getClass(), IndexOutOfBoundsException.class);
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IndexOutOfBoundsException, threw " +
        e.getMessage());
    }

    AssertJUnit.assertEquals("ABI", this.wordList.get(0));
    AssertJUnit.assertEquals("MIPS", this.wordList.get(107));
    AssertJUnit.assertEquals("Wemm", this.wordList.get(281));
  }


  /**
   * Test for {@link WordList#size()}.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wltest"})
  public void size()
    throws Exception
  {
    AssertJUnit.assertEquals(282, this.wordList.size());
  }
}
