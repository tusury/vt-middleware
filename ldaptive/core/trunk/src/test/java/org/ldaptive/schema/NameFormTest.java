/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.schema;

import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link NameForm}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NameFormTest
{


  /**
   * Test data for name form.
   *
   * @return  name form and string definition
   */
  @DataProvider(name = "definitions")
  public Object[][] createDefinitions()
  {
    return
      new Object[][] {
        new Object[] {
          new NameForm(
            "1.3.6.1.1.10.15.1",
            null,
            null,
            false,
            null,
            null,
            null,
            null),
          "( 1.3.6.1.1.10.15.1 )",
        },
        new Object[] {
          new NameForm(
            "1.3.6.1.1.10.15.1",
            new String[] {"uddiBusinessEntityNameForm"},
            null,
            false,
            null,
            null,
            null,
            null),
          "( 1.3.6.1.1.10.15.1 NAME 'uddiBusinessEntityNameForm' )",
        },
        new Object[] {
          new NameForm(
            "1.3.6.1.1.10.15.1",
            new String[] {"uddiBusinessEntityNameForm"},
            null,
            false,
            "uddiBusinessEntity",
            null,
            null,
            null),
          "( 1.3.6.1.1.10.15.1 NAME 'uddiBusinessEntityNameForm' " +
            "OC uddiBusinessEntity )",
        },
        new Object[] {
          new NameForm(
            "1.3.6.1.1.10.15.1",
            new String[] {"uddiBusinessEntityNameForm"},
            null,
            false,
            "uddiBusinessEntity",
            new String[] {"uddiBusinessKey"},
            null,
            null),
          "( 1.3.6.1.1.10.15.1 NAME 'uddiBusinessEntityNameForm' " +
            "OC uddiBusinessEntity MUST uddiBusinessKey )",
        },
        new Object[] {
          new NameForm(
            "1.3.6.1.1.10.15.1",
            new String[] {"uddiBusinessEntityNameForm"},
            null,
            false,
            "uddiBusinessEntity",
            new String[] {"uddiBusinessKey"},
            null,
            new Extensions("X-ORIGIN", Arrays.asList("RFC 4403"))),
          "( 1.3.6.1.1.10.15.1 NAME 'uddiBusinessEntityNameForm' " +
            "OC uddiBusinessEntity MUST uddiBusinessKey X-ORIGIN 'RFC 4403' )",
        },
      };
  }


  /**
   * @param  nameForm  to compare
   * @param  definition  to parse
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"schema"},
    dataProvider = "definitions"
  )
  public void parse(final NameForm nameForm, final String definition)
    throws Exception
  {
    final NameForm parsed = NameForm.parse(definition);
    Assert.assertEquals(nameForm, parsed);
    Assert.assertEquals(definition, parsed.format());
    Assert.assertEquals(nameForm.format(), parsed.format());
  }
}
