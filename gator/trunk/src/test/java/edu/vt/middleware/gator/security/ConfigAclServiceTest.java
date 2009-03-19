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
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.UnitTestHelper;

/**
 * Unit test for {@link ConfigAclService} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/test-applicationContext.xml"})
@TransactionConfiguration(transactionManager="txManager", defaultRollback=false)
@Transactional
public class ConfigAclServiceTest
{
  /** Test project configuration */
  private ProjectConfig testProject;

  /** Transaction manager */
  @Autowired
  private PlatformTransactionManager txManager;
 
  /** Handles persisting projects */
  @Autowired
  private ConfigManager configManager;

  /** ACL service */
  @Autowired
  private ConfigAclService aclService;


  /**
   * Test setup routine called before each test method.
   * @throws Exception On errors.
   */
  @BeforeTransaction
  public void setUp() throws Exception
  {
    testProject = UnitTestHelper.createTestProject();
    new TransactionTemplate(txManager).execute(
        new TransactionCallbackWithoutResult() {
          protected void doInTransactionWithoutResult(
            final TransactionStatus status) {
            configManager.save(testProject);
          }
        });
  }

  /**
   * Test method for {@link ConfigAclService#findChildren(ObjectIdentity)}.
   */
  @Test
  public void testFindChildren()
  {
    final ObjectIdentity oid = new ObjectIdentityImpl(
      ProjectConfig.class, testProject);
    Assert.assertNull(aclService.findChildren(oid));
  }

  /**
   * Test method for {@link ConfigAclService#readAclById(ObjectIdentity)}.
   */
  @Test
  public void testReadAclByIdObjectIdentity()
  {
    final ObjectIdentity projectOid = new ObjectIdentityImpl(
      ProjectConfig.class, testProject);
    Assert.assertEquals(
      ProjectAcl.ALL_PERMISSIONS.length + 1,
      aclService.readAclById(projectOid).getEntries().length);
    final ObjectIdentity appenderOid = new ObjectIdentityImpl(
      ProjectConfig.class, testProject.getAppender("FILE"));
    Assert.assertEquals(
      ProjectAcl.ALL_PERMISSIONS.length + 1,
      aclService.readAclById(appenderOid).getEntries().length);
  }
}
