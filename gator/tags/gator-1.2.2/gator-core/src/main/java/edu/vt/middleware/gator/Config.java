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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Abstract configuration class from which all concrete configuration classes
 * derive.
 *
 * @author  Middleware Services
 */
@MappedSuperclass
public abstract class Config implements Serializable
{

  /** Config.java. */
  private static final long serialVersionUID = 5079357889969322910L;

  private static final int DEFAULT_ID = -1;

  protected int id = DEFAULT_ID;

  protected String name;


  /** @return  the id */
  @Transient
  public abstract int getId();

  /** @param  id  the id to set */
  protected void setId(final int id)
  {
    this.id = id;
  }

  @Transient
  public boolean isNew()
  {
    return id == DEFAULT_ID;
  }

  /** @return  the name */
  @NotNull(message = "{config.name.notNull}")
  @Size(
    max = 255,
    message = "{config.name.size}"
  )
  @Pattern(
    regexp = "[A-Za-z0-9]+[A-Za-z0-9._ -]*",
    message = "{config.name.pattern}"
  )
  @Column(
    name = "name",
    nullable = false,
    length = 255
  )
  public String getName()
  {
    return name;
  }

  /** @param  name  the name to set */
  public void setName(final String name)
  {
    this.name = name;
  }

  /** {@inheritDoc}. */
  @Override
  public String toString()
  {
    return String.format("%s::%s", getClass().getSimpleName(), getName());
  }

  /** {@inheritDoc}. */
  @Override
  public int hashCode()
  {
    if (name != null) {
      return 31 * getHashCodeSeed() + name.hashCode();
    } else {
      return getHashCodeSeed();
    }
  }

  /** {@inheritDoc}. */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    } else if (o == this) {
      return true;
    } else {
      return getClass() == o.getClass() && hashCode() == o.hashCode();
    }
  }

  /**
   * Gets the hash code seed for this class.
   *
   * @return  Hash code seed for this class;
   */
  @Transient
  protected abstract int getHashCodeSeed();
}
