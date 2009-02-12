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
 * @author Middleware
 * @version $Revision$
 *
 */
@Entity
@Table(name = "log_layout_params")
@SequenceGenerator(
  name = "param_sequence",
  sequenceName = "log_seq_params",
  allocationSize = 1)
public class LayoutParamConfig extends ParamConfig
{
  /** Hash code seed */
  private static final int HASH_CODE_SEED = 32768;


  /** {@inheritDoc} */
  @Id
  @Column(name = "layout_param_id", nullable = false)
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "param_sequence")
  public int getId()
  {
    return id;
  }
  

  /** {@inheritDoc} */
  @Transient
  protected int getHashCodeSeed()
  {
    return HASH_CODE_SEED;
  }
}
