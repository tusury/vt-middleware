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
    testProject = UnitTestHelper.createTestProject();
    final CategoryConfig catNone = new CategoryConfig();
    catNone.setName(TEST_CAT_NONE);
    final CategoryConfig catFoo = new CategoryConfig();
    catFoo.setName(TEST_CAT_FOO);
    catFoo.getAppenders().add(
      testProject.getAppender(UnitTestHelper.TEST_APPENDER));
    final CategoryConfig catFooBar = new CategoryConfig();
    catFooBar.setName(TEST_CAT_FOOBAR);
    catFooBar.getAppenders().add(
      testProject.getAppender(UnitTestHelper.TEST_APPENDER));
    final CategoryConfig catFooDotBar = new CategoryConfig();
    catFooDotBar.setName(TEST_CAT_FOO_BAR);
    catFooDotBar.getAppenders().add(
      testProject.getAppender(UnitTestHelper.TEST_APPENDER));
    final CategoryConfig catRoot = new CategoryConfig();
    catRoot.setName(CategoryConfig.ROOT_CATEGORY_NAME);
    catRoot.getAppenders().add(
      testProject.getAppender(UnitTestHelper.TEST_APPENDER));
    testProject.addCategory(catNone);
    testProject.addCategory(catFoo);
    testProject.addCategory(catFooBar);
    testProject.addCategory(catFooDotBar);
    testProject.addCategory(catRoot);
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
      testProject.getAppender(UnitTestHelper.TEST_APPENDER);
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
  }

}
