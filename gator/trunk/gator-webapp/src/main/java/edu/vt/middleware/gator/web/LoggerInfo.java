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
package edu.vt.middleware.gator.web;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Logger information for Web display.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class LoggerInfo implements Comparable<LoggerInfo>
{
  private static final int HASH_CODE_SEED = 23;

  private String category;
  
  private String level;
  
  private String effectiveLevel;
  
  private boolean additivity;
  
  private SortedSet<String> appenders = new TreeSet<String>();


  /**
   * @return the category
   */
  public String getCategory()
  {
    return category;
  }

  /**
   * @param category the category to set
   */
  public void setCategory(String category)
  {
    this.category = category;
  }

  /**
   * @return the level
   */
  public String getLevel()
  {
    return level;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(String level)
  {
    this.level = level;
  }

  /**
   * @return the effectiveLevel
   */
  public String getEffectiveLevel()
  {
    return effectiveLevel;
  }

  /**
   * @param effectiveLevel the effectiveLevel to set
   */
  public void setEffectiveLevel(String effectiveLevel)
  {
    this.effectiveLevel = effectiveLevel;
  }

  /**
   * @return the additivity
   */
  public boolean isAdditivity()
  {
    return additivity;
  }

  /**
   * @param additivity the additivity to set
   */
  public void setAdditivity(boolean additivity)
  {
    this.additivity = additivity;
  }

  /**
   * @return the appenders
   */
  public SortedSet<String> getAppenders()
  {
    return appenders;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = HASH_CODE_SEED;
    hash = hash * 31 + category.hashCode();
    hash = hash * 31 + level.hashCode();
    hash = hash * 31 + effectiveLevel.hashCode();
    hash = hash * 31 + appenders.hashCode();
    hash = hash * 31 + (additivity ? 1 : 0);
    return hash;
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null || !(o instanceof LoggerInfo)) {
      return false;
    } else {
      final LoggerInfo other = (LoggerInfo) o;
      boolean result = false;
      if (result && category != null) {
        result &= category.equals(other.getCategory());
      }
      if (result && level != null) {
        result &= level.equals(other.getLevel());
      }
      if (result && effectiveLevel != null) {
        result &= effectiveLevel.equals(other.getEffectiveLevel());
      }
      if (result && effectiveLevel != null) {
        result &= (additivity == other.isAdditivity());
      }
      if (result && appenders != null) {
        result &= appenders.equals(other.getAppenders());
      }
      return result;
    }
  }

  /** {@inheritDoc} */
  public int compareTo(LoggerInfo o)
  {
    int result = 0;
    if (category == null) {
      if (o.getCategory() == null) {
        result = 0;
      } else {
        result = -1;
      }
    } else {
      if (o == null || o.getCategory() == null) {
        result = 1;
      } else {
        result = category.compareToIgnoreCase(o.getCategory());
      }
    }
    return result;
  }
}
