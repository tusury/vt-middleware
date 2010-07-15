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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Appender layout configuration parameter.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
@Entity
@Table(name = "log_layout_params")
@SequenceGenerator(
  name = "param_sequence",
  sequenceName = "log_seq_params",
  initialValue = 1,
  allocationSize = 1
)
public class LayoutParamConfig extends ParamConfig
{

  /** LayoutParamConfig.java. */
  private static final long serialVersionUID = 6865570215727728635L;

  /** Hash code seed. */
  private static final int HASH_CODE_SEED = 32768;


  /** Default constructor. */
  public LayoutParamConfig() {}


  /**
   * Creates a new appender parameter with the given name and value.
   *
   * @param  name  Layout parameter name.
   * @param  value  Layout parameter value.
   */
  public LayoutParamConfig(final String name, final String value)
  {
    setName(name);
    setValue(value);
  }


  /** {@inheritDoc}. */
  @Id
  @Column(
    name = "layout_param_id",
    nullable = false
  )
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "param_sequence"
  )
  public int getId()
  {
    return id;
  }


  /** {@inheritDoc}. */
  @Transient
  protected int getHashCodeSeed()
  {
    return HASH_CODE_SEED;
  }
}
