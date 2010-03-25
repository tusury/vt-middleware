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

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.AppenderPolicy;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.UnitTestHelper;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link AppenderPolicyConstraintValidator} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AppenderPolicyConstraintValidatorTest
{

  /**
   * Test method for {@link AppenderPolicyConstraintValidator#isValid(CategoryConfig, ConstraintValidatorContext)}.
   */
  @Test
  public void testIsValid()
  {
    final ProjectConfig project1 = UnitTestHelper.createProject(
        "test", "a1", "a2", "client1", "client2", "foo", "bar");
    Assert.assertTrue(validate(project1.getCategory("foo")));
    
    
    final ProjectConfig project2 = UnitTestHelper.createProject(
        "test", "a1", "a2", "client1", "client2", "foo", "bar");
    project2.setAppenderPolicy(new DenyAllAppenderPolicy());
    Assert.assertFalse(validate(project2.getCategory("foo")));
    Assert.assertFalse(validate(project2.getCategory("bar")));
  }


  private boolean validate(final CategoryConfig category)
  {
    final AppenderPolicyConstraintValidator validator =
      new AppenderPolicyConstraintValidator();
    validator.initialize(
        category.getClass().getAnnotation(AppenderPolicyConstraint.class));
    return validator.isValid(category, null);
  }
  
  private static class DenyAllAppenderPolicy implements AppenderPolicy
  {
    /** {@inheritDoc} */
    public boolean allow(CategoryConfig category, AppenderConfig appender)
    {
      return false;
    }

    /** {@inheritDoc} */
    public boolean allowSocketAppender(CategoryConfig category)
    {
      return false;
    }
  }
}
