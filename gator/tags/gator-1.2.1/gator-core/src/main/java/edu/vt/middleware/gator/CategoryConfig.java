/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import edu.vt.middleware.gator.validation.AppenderConstraint;
import edu.vt.middleware.gator.validation.UniqueName;
import org.apache.log4j.Level;

/**
 * Configuration for log4j logging categories.
 *
 * @author  Middleware Services
 */
@Entity
@Table(name = "log_categories")
@SequenceGenerator(
  name = "category_sequence",
  sequenceName = "log_seq_clients",
  allocationSize = 1
)
@UniqueName(message = "{category.uniqueName}")
@AppenderConstraint(message = "{category.appenderConstraint}")
public class CategoryConfig extends Config
{

  /** CategoryConfig.java. */
  private static final long serialVersionUID = -1021970964264194991L;

  /** Special category name for root appender. */
  public static final String ROOT_CATEGORY_NAME = "root";

  /** log4j log level strings. */
  public static final String[] LOG_LEVELS = new String[] {
    "TRACE",
    "DEBUG",
    "INFO",
    "WARN",
    "ERROR",
    "FATAL",
    "OFF",
  };

  /** Hash code seed. */
  private static final int HASH_CODE_SEED = 4096;

  private String level;

  private boolean additivity = true;

  private boolean allowSocketAppender = true;

  private ProjectConfig project;

  /** Appenders that receive logging events from this category. */
  private Set<AppenderConfig> appenders = new HashSet<AppenderConfig>();


  /** {@inheritDoc}. */
  @Id
  @Column(
    name = "category_id",
    nullable = false
  )
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "category_sequence"
  )
  public int getId()
  {
    return id;
  }

  /** @return  the level */
  @NotNull(message = "{category.level.notNull}")
  @Size(
    max = 10,
    message = "{category.level.max}"
  )
  @Column(
    name = "level",
    nullable = false,
    length = 10
  )
  public String getLevel()
  {
    return level;
  }

  /** @param  l  the level to set */
  public void setLevel(final String l)
  {
    this.level = l;
  }

  /** @return  the additivity */
  @Column(
    name = "additivity",
    nullable = false
  )
  public boolean getAdditivity()
  {
    return additivity;
  }

  /** @param  additivity  the additivity to set */
  public void setAdditivity(final boolean flag)
  {
    this.additivity = flag;
  }

  /**
   * Gets the corresponding Log4j log level associated with the level string.
   *
   * @return  Log4j logger level.
   */
  @Transient
  public Level getLog4jLevel()
  {
    return Level.toLevel(level);
  }

  /** @return  Project to which this category belongs. */
  @ManyToOne
  @JoinColumn(
    name = "project_id",
    nullable = false,
    updatable = false
  )
  public ProjectConfig getProject()
  {
    return project;
  }

  /** @param  p  Project to which this category belongs. */
  public void setProject(final ProjectConfig p)
  {
    this.project = p;
  }

  /**
   * Gets the appenders that logging events of this category will be sent to.
   *
   * @return  Appenders for this category.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "log_categories_to_appenders",
    joinColumns = @JoinColumn(name = "category_id"),
    inverseJoinColumns = @JoinColumn(name = "appender_id")
  )
  public Set<AppenderConfig> getAppenders()
  {
    return appenders;
  }

  /**
   * Sets the appenders that logging events of this category will be sent to.
   *
   * @param  s  Appenders for this category.
   */
  public void setAppenders(final Set<AppenderConfig> s)
  {
    appenders = s;
  }

  /**
   * Determines whether this category is a root category. The name of this
   * category is compared case-insensitive against {@link #ROOT_CATEGORY_NAME}.
   *
   * @return  True if this category is the root category, false otherwise.
   */
  @Transient
  public boolean isRoot()
  {
    return ROOT_CATEGORY_NAME.equalsIgnoreCase(getName());
  }

  /**
   * Javabean compliant synonym for {@link #isRoot}.
   *
   * @return  True if this category is the root category, false otherwise.
   */
  @Transient
  public boolean getRoot()
  {
    return isRoot();
  }

  /**
   * @return  True if socket appender reference is allowed on this category,
   * false otherwise.
   */
  @Column(
    name = "allow_socket_appender",
    nullable = false
  )
  public boolean isAllowSocketAppender()
  {
    return allowSocketAppender;
  }

  /**
   * Sets whether socket appender reference is allowed for this category.
   *
   * @param  allow  True if socket appender reference is allowed on this
   * category, false otherwise.
   */
  public void setAllowSocketAppender(final boolean allow)
  {
    allowSocketAppender = allow;
  }

  /** {@inheritDoc}. */
  @Transient
  protected int getHashCodeSeed()
  {
    return HASH_CODE_SEED;
  }
}
