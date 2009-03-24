/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Middleware.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.util;

import org.junit.Assert;
import org.junit.Test;

import edu.vt.middleware.gator.util.FileHelper;

/**
 * Unit test for {@link FileHelper} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class FileHelperTest
{

  /**
   * Test method for {@link FileHelper#pathCat(java.lang.String[])}.
   */
  @Test
  public void testPathCat()
  {
    final String refPath = "/home/marvin/foo/file";
    Assert.assertEquals(
      refPath,
      FileHelper.pathCat("/home/marvin/", "/foo", "file"));
    Assert.assertEquals(
      refPath,
      FileHelper.pathCat("/home/marvin", "foo/", "/file"));
    Assert.assertEquals(
      refPath,
      FileHelper.pathCat("/home/marvin", "/foo", "/file"));
    Assert.assertEquals(
      refPath,
      FileHelper.pathCat("/home/marvin/", "foo/", "file"));
  }

}
