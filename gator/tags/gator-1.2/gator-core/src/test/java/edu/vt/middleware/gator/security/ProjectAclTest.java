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
package edu.vt.middleware.gator.security;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;

import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.UnitTestHelper;

/**
 * Unit test for {@link ProjectAcl} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ProjectAclTest
{
  /**
   * Test method for {@link ProjectAcl#getEntries()}.
   */
  @Test
  public void testGetEntries()
  {
    final ProjectAcl acl = new ProjectAcl(createProject());
    Assert.assertEquals(
      PermissionConfig.ALL_PERMISSIONS.length + 1,
      acl.getEntries().size());
  }

  /**
   * Test method for {@link ProjectAcl#getObjectIdentity()}.
   */
  @Test
  public void testGetObjectIdentity()
  {
    final ProjectConfig project = createProject();
    final ProjectAcl acl = new ProjectAcl(project);
    Assert.assertEquals(project, acl.getObjectIdentity().getIdentifier());
  }

  /**
   * Test method for {@link ProjectAcl#isGranted(Permission[], Sid[], boolean)}.
   */
  @Test
  public void testIsGranted()
  {
    final ProjectAcl acl = new ProjectAcl(createProject());
    Assert.assertTrue(
      acl.isGranted(
        Arrays.asList(new Permission[] { BasePermission.WRITE, BasePermission.READ }),
        Arrays.asList(new Sid[] { new PrincipalSid("adm") }),
        false));
    Assert.assertTrue(
      acl.isGranted(
        Arrays.asList(new Permission[] { BasePermission.READ }),
        Arrays.asList(new Sid[] { new PrincipalSid("usr") }),
        false));
    Assert.assertFalse(
      acl.isGranted(
          Arrays.asList(new Permission[] { BasePermission.WRITE }),
          Arrays.asList(new Sid[] { new PrincipalSid("usr") }),
        false));
  }

  
  private static ProjectConfig createProject()
  {
    final ProjectConfig project = UnitTestHelper.createProject(
        "p", "a1", "a2", "c1", "c2", "cat1", "cat2");
    project.addPermission(
        new PermissionConfig("adm", PermissionConfig.parsePermissions("rwd")));
    project.addPermission(
        new PermissionConfig("usr", PermissionConfig.parsePermissions("r")));
    return project;
  }
}
