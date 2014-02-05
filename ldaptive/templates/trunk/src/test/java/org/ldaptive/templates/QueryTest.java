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
package org.ldaptive.templates;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Query}.
 *
 * @author  Middleware Services
 * @version  $Revision $ $Date$
 */
public class QueryTest
{


  /**
   * Sample query data.
   *
   * @return  query data
   */
  @DataProvider(name = "query-data")
  public Object[][] createTestData()
  {
    return
      new Object[][] {
        {
          new Query(null),
          new String[] {},
        },
        {
          new Query(""),
          new String[] {},
        },
        {
          new Query("  dfisher "),
          new String[] {"dfisher"},
        },
        {
          new Query("d fisher"),
          new String[] {"d", "fisher", },
        },
        {
          new Query("daniel fisher"),
          new String[] {"daniel", "fisher", },
        },
        {
          new Query("daniel w fisher"),
          new String[] {"daniel", "w", "fisher", },
        },
      };
  }


  /**
   * @param  query  to get terms from
   * @param  terms  to compare
   */
  @Test(groups = {"querytest"}, dataProvider = "query-data")
  public void format(final Query query, final String[] terms)
  {
    AssertJUnit.assertArrayEquals(terms, query.getTerms());
  }
}
