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
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.Config;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.UnitTestHelper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for {@link UniqueNameValidator} classs.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/test-applicationContext.xml"})
public class UniqueNameValidatorTest
{
  @Autowired
  private ConfigManager manager;


  /**
   * Test method for {@link UniqueNameValidator#isValid(Config, ConstraintValidatorContext)}.
   */
  @Test
  public void testIsValid()
  {
    final ProjectConfig project1 = UnitTestHelper.createProject(
        "p1", "a1", "a2", "client1", "client2", "cat1", "cat2");
    project1.addPermission(
        new PermissionConfig("a", PermissionConfig.parsePermissions("rwd")));
    project1.addPermission(
        new PermissionConfig("b", PermissionConfig.parsePermissions("rwd")));
    final ProjectConfig project2 = UnitTestHelper.createProject(
        "p2", "a1", "a2", "client1", "client2", "cat1", "cat2");
    project2.addPermission(
        new PermissionConfig("a", PermissionConfig.parsePermissions("rwd")));
    project2.addPermission(
        new PermissionConfig("b", PermissionConfig.parsePermissions("rwd")));
    manager.save(project1);
    manager.save(project2);

    // Project name validation
    final ProjectConfig goodProject = UnitTestHelper.createProject(
        "good", "a1", "a2", "client1", "client2", "cat1", "cat2");
    Assert.assertEquals(true, validate(goodProject));
    final ProjectConfig dupeProject = UnitTestHelper.createProject(
        "p2", "a1", "a2", "client1", "client2", "cat1", "cat2");
    Assert.assertEquals(false, validate(dupeProject));
    project2.setName("p1");
    Assert.assertEquals(false, validate(project2));

    // Appender name validation
    final AppenderConfig goodAppender = UnitTestHelper.createAppender("foo");
    goodAppender.setProject(project1);
    Assert.assertEquals(true, validate(goodAppender));
    final AppenderConfig dupeAppender = UnitTestHelper.createAppender("a2");
    dupeAppender.setProject(project1);
    Assert.assertEquals(false, validate(dupeAppender));
    final AppenderConfig existingAppender = project1.getAppender("a1");
    existingAppender.setName("a2");
    Assert.assertEquals(false, validate(existingAppender));

    // Category name validation
    final CategoryConfig goodCategory = UnitTestHelper.createCategory("foo");
    goodCategory.setProject(project1);
    Assert.assertEquals(true, validate(goodCategory));
    final CategoryConfig dupeCategory = UnitTestHelper.createCategory("cat2");
    dupeCategory.setProject(project1);
    Assert.assertEquals(false, validate(dupeCategory));
    final CategoryConfig existingCategory = project1.getCategory("cat1");
    existingCategory.setName("cat2");
    Assert.assertEquals(false, validate(existingCategory));

    // Client name validation
    final ClientConfig goodClient = UnitTestHelper.createClient("foo");
    goodClient.setProject(project1);
    Assert.assertEquals(true, validate(goodClient));
    final ClientConfig dupeClient = UnitTestHelper.createClient("client2");
    dupeClient.setProject(project1);
    Assert.assertEquals(false, validate(dupeClient));
    final ClientConfig existingClient = project1.getClient("client1");
    existingClient.setName("client2");
    Assert.assertEquals(false, validate(existingClient));

    // Permission name validation
    final PermissionConfig goodPerm = new PermissionConfig("foo", 1);
    goodPerm.setProject(project1);
    Assert.assertEquals(true, validate(goodPerm));
    final PermissionConfig dupePerm = new PermissionConfig("a", 1);
    dupePerm.setProject(project1);
    Assert.assertEquals(false, validate(dupePerm));
    final PermissionConfig existingPerm = project1.getPermission("a");
    existingPerm.setName("b");
    Assert.assertEquals(false, validate(existingPerm));
  }
  
  
  private boolean validate(Config c)
  {
    final UniqueNameValidator validator = new UniqueNameValidator();
    validator.setConfigManager(manager);
    validator.initialize(c.getClass().getAnnotation(UniqueName.class));
    return validator.isValid(c, null);
  }
}
