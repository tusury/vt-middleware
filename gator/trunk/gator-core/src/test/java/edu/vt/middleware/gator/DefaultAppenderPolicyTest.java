/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link DefaultAppenderPolicy} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class DefaultAppenderPolicyTest
{
  /** Category with no appenders */
  private static final String TEST_CAT_NONE = "org.none";
  
  /** Parent test category*/
  private static final String TEST_CAT_FOO = "org.foo";
 
  /** Child test category */
  private static final String TEST_CAT_FOO_BAR = "org.foo.bar";
 
  /** Similar name category */
  private static final String TEST_CAT_FOOBAR = "org.foobar";

  /** Test project used to verify default appender policy */
  private ProjectConfig testProject;
 
  /** Test subject */
  private final DefaultAppenderPolicy policy = new DefaultAppenderPolicy();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
    testProject = UnitTestHelper.createProject(
        "p", "a1", "a2", "c1", "c2",
        TEST_CAT_FOO, TEST_CAT_FOO_BAR, TEST_CAT_FOOBAR,
        CategoryConfig.ROOT_CATEGORY_NAME);
    testProject.addCategory(UnitTestHelper.createCategory(TEST_CAT_NONE));
  }

  /**
   * Test method for
   * {@link DefaultAppenderPolicy#allow(CategoryConfig, AppenderConfig)}.
   */
  @Test
  public void testAllow()
  {
    final CategoryConfig category = testProject.getCategory(TEST_CAT_FOO);
    final AppenderConfig appender =
      testProject.getAppender("a1");
    Assert.assertTrue(policy.allow(category, appender));
  }

  /**
   * Test method for
   * {@link DefaultAppenderPolicy#allowSocketAppender(CategoryConfig)}.
   */
  @Test
  public void testAllowSocketAppender()
  {
    Assert.assertFalse(
      policy.allowSocketAppender(
        testProject.getCategory(TEST_CAT_NONE)));
    Assert.assertFalse(
      policy.allowSocketAppender(
        testProject.getCategory(CategoryConfig.ROOT_CATEGORY_NAME)));
    Assert.assertTrue(
      policy.allowSocketAppender(
        testProject.getCategory(TEST_CAT_FOO)));
    Assert.assertTrue(
      policy.allowSocketAppender(
        testProject.getCategory(TEST_CAT_FOOBAR)));
    Assert.assertFalse(
      policy.allowSocketAppender(
        testProject.getCategory(TEST_CAT_FOO_BAR)));
    policy.setAllowSocketAppenderOnRootCategory(true);
    Assert.assertTrue(
      policy.allowSocketAppender(
        testProject.getCategory(CategoryConfig.ROOT_CATEGORY_NAME)));
  }

}
