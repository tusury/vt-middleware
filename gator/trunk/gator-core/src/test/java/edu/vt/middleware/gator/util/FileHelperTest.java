/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.util;

import edu.vt.middleware.gator.util.FileHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link FileHelper} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class FileHelperTest
{

  /** Test method for {@link FileHelper#pathCat(java.lang.String[])}. */
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
