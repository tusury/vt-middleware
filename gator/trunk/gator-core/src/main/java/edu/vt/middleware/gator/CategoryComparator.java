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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two categories such that categories are sorted in name order
 * (ignoring case) with the special root category appearing at the end.
 *
 * @author Middleware
 * @version $Revision$
 * @since 1.2.1
 *
 */
public class CategoryComparator
  implements Comparator<CategoryConfig>, Serializable
{
  /** CategoryComparator.java */
  private static final long serialVersionUID = -1962041529726092904L;

  /** {@inheritDoc} */
  public int compare(final CategoryConfig a, final CategoryConfig b)
  {
    int result = 0;
    if (isRoot(a)) {
      result = 1;
    } else if (isRoot(b)) {
      result = -1;
    } else {
      result = a.getName().compareToIgnoreCase(b.getName());
    }
    return result;
  }


  /**
   * Determines whether the given category represents the root category.
   *
   * @param category Category to consider.
   * @return True if category is root category, false otherwise.
   */
  private boolean isRoot(final CategoryConfig category)
  {
    return CategoryConfig.ROOT_CATEGORY_NAME.equalsIgnoreCase(
        category.getName());
  }
}
