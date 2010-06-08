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
package edu.vt.middleware.gator.validation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.UnitTestHelper;

/**
 * Unit test for {@link AppenderConstraintValidator} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/test-applicationContext.xml"})
public class AppenderConstraintValidatorTest
{
  @Autowired
  private ConfigManager manager;


  /**
   * Test method for {@link AppenderConstraintValidator#isValid(CategoryConfig, ConstraintValidatorContext)}.
   */
  @Test
  public void testIsValid()
  {
    final ProjectConfig p1 = UnitTestHelper.createProject(
        "p1", "a1", "a2", "c1", "c2", "test.category.1");
    final ProjectConfig p2 = UnitTestHelper.createProject(
        "p2", "b1", "b2", "c1", "c2", "test.category.2");
    manager.save(p1);
    manager.save(p2);
    p1.addAppender(UnitTestHelper.createAppender("a3"));
    final CategoryConfig category = p1.getCategory("test.category.1");
    category.getAppenders().add(p1.getAppender("a3"));
    Assert.assertTrue(validate(category));
    category.getAppenders().add(p2.getAppender("b1"));
    Assert.assertFalse(validate(category));
  }


  private boolean validate(final CategoryConfig category)
  {
    final AppenderConstraintValidator validator =
      new AppenderConstraintValidator();
    validator.initialize(
        category.getClass().getAnnotation(AppenderConstraint.class));
    return validator.isValid(category, null);
  }
}
