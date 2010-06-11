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
package edu.vt.middleware.gator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link CategoryComparator} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@RunWith(Parameterized.class)
public class CategoryComparatorTest
{
  private String[] original;
  private String[] expected;
  
  public CategoryComparatorTest(
      final String[] originalCategories,
      final String[] expectedCategories)
  {
    this.original = originalCategories;
    this.expected = expectedCategories;
  }
  
  @Parameters
  public static Collection<Object[]> getTestParameters() throws Exception
  {
    final Collection<Object[]> params = new ArrayList<Object[]>();
    params.add(new Object[] {
       new String[] { "edu.vt.middleware", "root", "sun", "org.jboss" },
       new String[] { "edu.vt.middleware", "org.jboss", "sun", "root" },
    });
    return params;
  }

  /**
   * Test method for {@link CategoryComparator#compare(CategoryConfig, CategoryConfig)}.
   */
  @Test
  public void testCompare()
  {
    final List<CategoryConfig> categories = new ArrayList<CategoryConfig>();
    for (String name : original) {
      final CategoryConfig category = new CategoryConfig();
      category.setName(name);
      categories.add(category);
    }
    final Set<CategoryConfig> sorted =
      new TreeSet<CategoryConfig>(new CategoryComparator());
    sorted.addAll(categories);
    Assert.assertEquals(expected.length, sorted.size());
    int i = 0;
    for (CategoryConfig c : sorted) {
      Assert.assertEquals(expected[i++], c.getName());
    }
  }

}
