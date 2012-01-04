/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.pool;

import org.ldaptive.AbstractTest;
import org.ldaptive.CompareRequest;
import org.ldaptive.Connection;
import org.ldaptive.LdapAttribute;
import org.ldaptive.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test for {@link CompareValidator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CompareValidatorTest extends AbstractTest
{


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"validator"})
  public void defaultSettings()
    throws Exception
  {
    final Connection c = TestUtil.createConnection();
    final CompareValidator sv = new CompareValidator();
    try {
      c.open();
      AssertJUnit.assertTrue(sv.validate(c));
    } finally {
      c.close();
    }
    AssertJUnit.assertFalse(sv.validate(c));
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"validator"})
  public void customSettings()
    throws Exception
  {
    final Connection c = TestUtil.createConnection();
    final CompareValidator cv = new CompareValidator(
      new CompareRequest(
        "uid=1,ou=test,dc=vt,dc=edu",
        new LdapAttribute("objectClass", "inetOrgPerson")));
    try {
      c.open();
      AssertJUnit.assertTrue(cv.validate(c));
      cv.getCompareRequest().setDn("uid=dne,ou=test,dc=vt,dc=edu");
      AssertJUnit.assertFalse(cv.validate(c));
    } finally {
      c.close();
    }
    AssertJUnit.assertFalse(cv.validate(c));
  }
}
