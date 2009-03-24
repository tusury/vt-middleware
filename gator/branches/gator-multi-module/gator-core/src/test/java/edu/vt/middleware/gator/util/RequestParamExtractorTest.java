/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit test for {@link RequestParamExtractor} class.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class RequestParamExtractorTest
{
  /**
   * Test method for
   * {@link RequestParamExtractor#getProjectName(HttpServletRequest)}.
   */
  @Test
  public void testGetProjectName()
  {
    final MockHttpServletRequest request = new MockHttpServletRequest(
      "GET",
      "/logather/project/foo/edit.html");
    Assert.assertEquals(
        "foo",
        RequestParamExtractor.getProjectName(request));
  }


  /**
   * Test method for
   * {@link RequestParamExtractor#getAppenderId(HttpServletRequest)}.
   */
  @Test
  public void testGetAppenderId()
  {
    final MockHttpServletRequest request = new MockHttpServletRequest(
      "GET",
      "/logather/project/foo/appender/8/edit.html");
    Assert.assertEquals(8, RequestParamExtractor.getAppenderId(request));
  }


  /**
   * Test method for
   * {@link RequestParamExtractor#getAppenderId(HttpServletRequest)}.
   */
  @Test
  public void testGetCategoryId()
  {
    final MockHttpServletRequest request = new MockHttpServletRequest(
      "GET",
      "/logather/project/foo/category/8/edit.html");
    Assert.assertEquals(8, RequestParamExtractor.getCategoryId(request));
  }


  /**
   * Test method for
   * {@link RequestParamExtractor#getClientId(HttpServletRequest)}.
   */
  @Test
  public void testGetClientId()
  {
    final MockHttpServletRequest request = new MockHttpServletRequest(
      "GET",
      "/logather/project/foo/client/8/edit.html");
    Assert.assertEquals(8, RequestParamExtractor.getClientId(request));
  }
}
