/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Describes appender-related configuration parameters.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
@MappedSuperclass
public abstract class ParamConfig extends Config
{
  /** ParamConfig.java */
  private static final long serialVersionUID = -4157155815415246168L;

  private AppenderConfig appender;

  private String value;


  /**
   * @return Appender to which this appender belongs.
   */
  @ManyToOne
  @JoinColumn(
    name = "appender_id",
    nullable = false,
    updatable = false)
  public AppenderConfig getAppender()
  {
    return appender;
  }

  /**
   * @param a Appender to which this param belongs.
   */
  public void setAppender(final AppenderConfig a)
  {
    this.appender = a;
  }

  /**
   * @return the value
   */
  @Column(name = "value", nullable = false)
  public String getValue()
  {
    return value;
  }

  /**
   * @param val the value to set
   */
  public void setValue(final String val)
  {
    this.value = val;
  }

}
