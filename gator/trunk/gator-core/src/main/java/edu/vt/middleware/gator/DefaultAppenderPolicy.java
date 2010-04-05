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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default appender policy has the following behavior:
 * <ul>
 * <li>Explicitly-defined appenders are allowed on categories in all cases,
 * i.e. the {@link #allow(CategoryConfig, AppenderConfig)} method always returns
 * true.</li>
 * <li>Socket appender is not allowed on the root category by default,
 * but my be allowed by setting {@link #allowSocketAppenderOnRootCategory} to
 * true.</li>
 * <li>Socket appender is not allowed on a category if a parent category
 * is also defined with appender references.  This policy exists to prevent
 * duplicate logging events on the server.</li>
 * <li>Socket appender is not allowed if the category has no appender
 * references.</li>
 * <li>Otherwise socket appender is allowed.</li>
 * </ul>
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class DefaultAppenderPolicy implements AppenderPolicy, Serializable
{
  /** DefaultAppenderPolicy.java */
  private static final long serialVersionUID = 5623314094167715148L;

  /** Logger instance */
  private final Log logger = LogFactory.getLog(getClass());
 
  /** Flag determines whether socket appender is allowed on root category */
  private boolean allowSocketAppenderOnRootCategory;


  /**
   * @param allowRoot True to allow socket appender reference on root category,
   * false otherwise.
   */
  public void setAllowSocketAppenderOnRootCategory(final boolean allowRoot)
  {
    allowSocketAppenderOnRootCategory = allowRoot;
  }

  /**
   * @return True if socket appender reference is allowed on root category,
   * false otherwise.
   */
  public boolean isAllowSocketAppenderOnRootCategory()
  {
    return allowSocketAppenderOnRootCategory;
  }

  /** {@inheritDoc} */
  public boolean allow(
    final CategoryConfig category,
    final AppenderConfig appender)
  {
    return true;
  }

  /** {@inheritDoc} */
  public boolean allowSocketAppender(final CategoryConfig category)
  {
    boolean result = true;
    if (CategoryConfig.ROOT_CATEGORY_NAME.equals(category.getName()))
    {
      result = allowSocketAppenderOnRootCategory;
    }
    else if (hasParentWithAppenders(category) ||
             category.getAppenders().size() == 0)
    {
      result = false;
    }
    return result;
  }

  /**
   * Determines whether the given category has at least one parent category
   * defined with appender references in the containing project.
   *
   * @param category Category to examine for parents in containing project.
   * @return True if given category has a parent with at least one appender
   * reference in the containing project; false otherwise.
   */
  private boolean hasParentWithAppenders(final CategoryConfig category)
  {
    for (CategoryConfig c : category.getProject().getCategories()) {
      if (category.getName().startsWith(c.getName() + ".") && 
          category.getName().length() > c.getName().length() &&
          c.getAppenders().size() > 0) {
        logger.debug(
            String.format(
              "%s has parent %s with appenders defined.",
              category,
              c));
        return true;
      }
    }
    return false;
  }

}
