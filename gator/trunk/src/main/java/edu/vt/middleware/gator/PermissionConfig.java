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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Stores security permissions for principals/roles on a project.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Entity
@Table(name = "log_permissions")
@SequenceGenerator(
  name = "permission_sequence",
  sequenceName = "log_seq_permissions",
  allocationSize = 1)
public class PermissionConfig extends Config
{
  /** PermissionConfig.java */
  private static final long serialVersionUID = -1996976284469566605L;

  /** Hash code seed */
  private static final int HASH_CODE_SEED = 65536;
 
  /** Permission bits that have been or'ed together */
  private int permissionBits;

  private ProjectConfig project;


  /** Creates a new instance */
  public PermissionConfig() {}
 
  /**
   * Creates a new instance with the given permissions for the given SID.
   * @param sid Security identifier, either user principal name or role name.
   * @param permBits Security permissions.
   */
  public PermissionConfig(final String sid, final int permBits)
  {
    setName(sid);
    setPermissionBits(permBits);
  }

  /** {@inheritDoc} */
  @Id
  @Column(name = "perm_id", nullable = false)
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "permission_sequence")
  public int getId()
  {
    return id;
  }

  /**
   * @return the permissionBits
   */
  @Column(
    name = "perm_bits",
    nullable = false)
  public int getPermissionBits()
  {
    return permissionBits;
  }

  /**
   * @param bits the permissionBits to set
   */
  public void setPermissionBits(final int bits)
  {
    this.permissionBits = bits;
  }

  /**
   * @return the project
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
   * @param p the project to set
   */
  public void setProject(final ProjectConfig p)
  {
    this.project = p;
  }

  /** {@inheritDoc} */
  @Transient
  protected int getHashCodeSeed()
  {
    return HASH_CODE_SEED;
  }
}
