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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import edu.vt.middleware.gator.validation.UniqueName;

/**
 * Configuration for log4j clients that belong to one or more projects. "Client"
 * in this sense is that of client-server computing, where the client is
 * registered with one or more projects to generate a logging configuration and
 * then is expected to send messages to the log4j socket server and have the
 * configuration applied.
 *
 * @author  Middleware Services
 */
@Entity
@Table(name = "log_clients")
@SequenceGenerator(
  name = "client_sequence",
  sequenceName = "log_seq_clients",
  initialValue = 1,
  allocationSize = 1
)
@UniqueName(message = "{client.uniqueName}")
public class ClientConfig extends Config
{

  /** ClientConfig.java. */
  private static final long serialVersionUID = -8758342722734231737L;

  /** Hash code seed. */
  private static final int HASH_CODE_SEED = 8192;

  private ProjectConfig project;


  /** {@inheritDoc}. */
  @Id
  @Column(
    name = "client_id",
    nullable = false
  )
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "client_sequence"
  )
  public int getId()
  {
    return id;
  }

  /** @return  Project to which this client belongs. */
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

  /** @param  p  Project to which this client belongs. */
  public void setProject(final ProjectConfig p)
  {
    this.project = p;
  }

  /** {@inheritDoc}. */
  @Transient
  protected int getHashCodeSeed()
  {
    return HASH_CODE_SEED;
  }
}
