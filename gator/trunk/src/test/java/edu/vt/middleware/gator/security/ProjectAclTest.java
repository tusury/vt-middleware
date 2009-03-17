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

import org.junit.Assert;
import org.junit.Test;

import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;

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
    final ProjectAcl acl = new ProjectAcl(UnitTestHelper.createTestProject());
    Assert.assertEquals(
      ProjectAcl.ALL_PERMISSIONS.length + 1,
      acl.getEntries().length);
  }

  /**
   * Test method for {@link ProjectAcl#getObjectIdentity()}.
   */
  @Test
  public void testGetObjectIdentity()
  {
    final ProjectConfig project = UnitTestHelper.createTestProject();
    final ProjectAcl acl = new ProjectAcl(project);
    Assert.assertEquals(project, acl.getObjectIdentity().getIdentifier());
  }

  /**
   * Test method for {@link ProjectAcl#isGranted(Permission[], Sid[], boolean)}.
   */
  @Test
  public void testIsGranted()
  {
    final ProjectAcl acl = new ProjectAcl(UnitTestHelper.createTestProject());
    Assert.assertTrue(
      acl.isGranted(
        new Permission[] { BasePermission.WRITE, BasePermission.READ },
        new Sid[] { new PrincipalSid("admin") },
        false));
    Assert.assertTrue(
      acl.isGranted(
        new Permission[] { BasePermission.READ },
        new Sid[] { new PrincipalSid("user") },
        false));
    Assert.assertFalse(
      acl.isGranted(
        new Permission[] { BasePermission.WRITE },
        new Sid[] { new PrincipalSid("user") },
        false));
  }

}
