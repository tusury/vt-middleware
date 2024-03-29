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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

/**
 * Configuration for log4j appenders.
 *
 * @author Marvin S. Addison
 *
 */
@Entity
@Table(name = "log_appenders")
@SequenceGenerator(
  name = "appender_sequence",
  sequenceName = "log_seq_appenders",
  allocationSize = 1)
public class AppenderConfig extends Config
{
  /** AppenderConfig.java */
  private static final long serialVersionUID = -8271442546718020967L;

  /** Hash code seed */
  private static final int HASH_CODE_SEED = 2048;

  private ProjectConfig project;
  
  private String appenderClassName;
  
  private String errorHandlerClassName;
  
  private String layoutClassName;

  private Set<AppenderParamConfig> appenderParams;

  private Set<LayoutParamConfig> layoutParams;
  

  /** {@inheritDoc} */
  @Id
  @Column(name = "appender_id", nullable = false)
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "appender_sequence")
  public int getId()
  {
    return id;
  }


  /**
   * @return Project to which this appender belongs.
   */
  @ManyToOne
  @JoinColumn(
    name = "project_id",
    nullable = false,
    updatable = false)
  public ProjectConfig getProject()
  {
    return project;
  }

  /**
   * @param p Project to which this appender belongs.
   */
  public void setProject(final ProjectConfig p)
  {
    this.project = p;
  }

  /**
   * @return the appenderClassName
   */
  @Column(name = "appender_class", nullable = false)
  public String getAppenderClassName()
  {
    return appenderClassName;
  }

  /**
   * @param appenderClassName the appenderClassName to set
   */
  public void setAppenderClassName(final String appenderClassName)
  {
    this.appenderClassName = appenderClassName;
  }

  /**
   * @return the errorHandlerClassName
   */
  @Column(name = "error_handler_class")
  public String getErrorHandlerClassName()
  {
    return errorHandlerClassName;
  }

  /**
   * @param errorHandlerClassName the errorHandlerClassName to set
   */
  public void setErrorHandlerClassName(final String errorHandlerClassName)
  {
    this.errorHandlerClassName = errorHandlerClassName;
  }

  /**
   * @return the layoutClassName
   */
  @Column(name = "layout_class")
  public String getLayoutClassName()
  {
    return layoutClassName;
  }

  /**
   * @param layoutClassName the layoutClassName to set
   */
  public void setLayoutClassName(String layoutClassName)
  {
    this.layoutClassName = layoutClassName;
  }

  /**
   * Gets the parameters that define the behavior of this appender.
   * @return Appender parameters.
   */
  @OneToMany(
    mappedBy = "appender",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER)
  @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
  protected Set<AppenderParamConfig> getAppenderParamsInternal()
  {
    if (appenderParams == null) {
      appenderParams = new HashSet<AppenderParamConfig>();
    }
    return appenderParams;
  }
  
  /**
   * Sets the parameters that define the behavior of this appender.
   * @param s Appender parameters.
   */
  protected void setAppenderParamsInternal(final Set<AppenderParamConfig> s)
  {
    appenderParams = s;
  }

  /**
   * Gets an immutable collection of the appender parameters of this appender.
   * The collection of parameters is returned in sorted name order.
   * @return Appender parameters.
   */
  @Transient
  public Collection<AppenderParamConfig> getAppenderParams()
  {
    final SortedSet<AppenderParamConfig> paramSet =
      new TreeSet<AppenderParamConfig>(new ConfigComparator());
    paramSet.addAll(getAppenderParamsInternal());
    return Collections.unmodifiableCollection(paramSet);
  }

  /**
   * Gets the appender param with the given ID belonging to this project.
   * @param paramId Appender param ID.
   * @return Appender parameter of this appender with matching ID
   * or null if no matching parameter is found.
   */
  @Transient
  public AppenderParamConfig getAppenderParam(final int paramId)
  {
    for (AppenderParamConfig param : getAppenderParamsInternal()) {
      if (param.getId() == paramId) {
        return param;
      }
    }
    return null;
  }

  /**
   * Gets the appender param with the given name (case-insensitive).
   * @param name Name of appender parameter configuration to return.
   * @return Configuration parameter of given name
   * or null if no matching parameter is found.
   */
  @Transient
  public AppenderParamConfig getAppenderParam(final String name)
  {
    for (AppenderParamConfig param : getAppenderParamsInternal()) {
      if (name != null && param.getName().equalsIgnoreCase(name)) {
        return param;
      }
    }
    return null;
  }
  
  /**
   * Adds an appender parameter to this appender.
   * @param param Appender param to add.
   */
  public void addAppenderParam(final AppenderParamConfig param)
  {
    param.setAppender(this);
    getAppenderParamsInternal().add(param);
  }
  
  /**
   * Removes an appender parameter from this appender.
   * @param param To be removed.
   */
  public void removeAppenderParam(final AppenderParamConfig param)
  {
    param.setAppender(null);
    getAppenderParamsInternal().remove(param);
  }
  
  /**
   * Removes all appender parameters from this appender.
   */
  public void removeAllAppenderParams()
  {
    for (AppenderParamConfig p : getAppenderParamsInternal()) {
      p.setAppender(null);
    }
    getAppenderParamsInternal().clear();
  }

  /**
   * Gets the parameters that define the behavior of this appender layout.
   * @return Layout parameters.
   */
  @OneToMany(
    mappedBy = "appender",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER)
  @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
  protected Set<LayoutParamConfig> getLayoutParamsInternal()
  {
    if (layoutParams == null) {
      layoutParams = new HashSet<LayoutParamConfig>();
    }
    return layoutParams;
  }
  
  /**
   * Sets the parameters that define the behavior of this appender layout.
   * @param s Layout parameters.
   */
  protected void setLayoutParamsInternal(final Set<LayoutParamConfig> s)
  {
    layoutParams = s;
  }

  /**
   * Gets an immutable collection of the layout parameters of this appender.
   * The collection of parameters is returned in sorted name order.
   * @return Layout parameters.
   */
  @Transient
  public Collection<LayoutParamConfig> getLayoutParams()
  {
    final SortedSet<LayoutParamConfig> paramSet =
      new TreeSet<LayoutParamConfig>(new ConfigComparator());
    paramSet.addAll(getLayoutParamsInternal());
    return Collections.unmodifiableCollection(paramSet);
  }

  /**
   * Gets the layout param with the given ID belonging to this project.
   * @param paramId Layout param ID.
   * @return Layout parameter of this appender with matching ID
   * or null if no matching parameter is found.
   */
  @Transient
  public LayoutParamConfig getLayoutParam(final int paramId)
  {
    for (LayoutParamConfig param : getLayoutParamsInternal()) {
      if (param.getId() == paramId) {
        return param;
      }
    }
    return null;
  }

  /**
   * Gets the layout param with the given name (case-insensitive).
   * @param name Name of appender parameter configuration to return.
   * @return Configuration parameter of given name
   * or null if no matching parameter is found.
   */
  @Transient
  public LayoutParamConfig getLayoutParam(final String name)
  {
    for (LayoutParamConfig param : getLayoutParamsInternal()) {
      if (name != null && param.getName().equalsIgnoreCase(name)) {
        return param;
      }
    }
    return null;
  }
  
  /**
   * Adds a layout parameter to this appender.
   * @param param Layout param to add.
   */
  public void addLayoutParam(final LayoutParamConfig param)
  {
    param.setAppender(this);
    getLayoutParamsInternal().add(param);
  }
  
  /**
   * Removes a layout parameter from this appender.
   * @param param To be removed.
   */
  public void removeLayoutParam(final LayoutParamConfig param)
  {
    param.setAppender(null);
    getLayoutParamsInternal().remove(param);
  }
  
  /**
   * Removes all layout parameters from this appender.
   */
  public void removeAllLayoutParams()
  {
    for (LayoutParamConfig p : getLayoutParamsInternal()) {
      p.setAppender(null);
    }
    getLayoutParamsInternal().clear();
  }

  /** {@inheritDoc} */
  @Transient
  protected int getHashCodeSeed()
  {
    return HASH_CODE_SEED;
  }
}
