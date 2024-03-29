/*
  $Id$

  Copyright (C) 2003-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.password;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link NumericalSequenceRule}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class NumericalSequenceRuleTest extends AbstractRuleTest
{


  /**
   * @return  Test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "passwords")
  public Object[][] passwords()
    throws Exception
  {
    return
      new Object[][] {
        // Test valid password
        {
          new NumericalSequenceRule(),
          new PasswordData(new Password("p4zRcv8#n65")),
          true,
        },
        // Has numerical sequence
        {
          new NumericalSequenceRule(4, false),
          new PasswordData(new Password("p3456#n65")),
          false,
        },
        // Has wrapping numerical sequence with wrap=false
        {
          new NumericalSequenceRule(7, false),
          new PasswordData(new Password("p4zRcv2#n8901234")),
          true,
        },
        // Has wrapping numerical sequence with wrap=true
        {
          new NumericalSequenceRule(7, true),
          new PasswordData(new Password("p4zRcv2#n8901234")),
          false,
        },
        // Has backward numerical sequence
        {
          new NumericalSequenceRule(),
          new PasswordData(new Password("p54321#n65")),
          false,
        },
        // Has backward wrapping numerical sequence with wrap=false
        {
          new NumericalSequenceRule(5, false),
          new PasswordData(new Password("p987#n32109")),
          true,
        },
        // Has backward wrapping numerical sequence with wrap=true
        {
          new NumericalSequenceRule(8, true),
          new PasswordData(new Password("p54321098#n65")),
          false,
        },
      };
  }


  /**  @throws  Exception  On test failure. */
  @Test(groups = {"passtest"})
  public void resolveMessage()
    throws Exception
  {
    final Rule rule = new NumericalSequenceRule();
    final RuleResult result = rule.validate(
      new PasswordData(new Password("p34567n65")));
    for (RuleResultDetail detail : result.getDetails()) {
      AssertJUnit.assertEquals(
        String.format("Password contains the illegal sequence '%s'.", "34567"),
        DEFAULT_RESOLVER.resolve(detail));
      AssertJUnit.assertNotNull(EMPTY_RESOLVER.resolve(detail));
    }
  }
}
