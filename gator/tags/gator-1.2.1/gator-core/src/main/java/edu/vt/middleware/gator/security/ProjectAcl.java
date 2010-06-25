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
package edu.vt.middleware.gator.security;

import java.util.ArrayList;
import java.util.List;
import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.UnloadedSidException;

/**
 * Simple ACL implementation that does not support ACL inheritance, auditing, or
 * logging.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ProjectAcl implements Acl
{

  /** ProjectAcl.java. */
  private static final long serialVersionUID = 7764134389962328091L;


  private final List<AccessControlEntry> entries;

  private final ObjectIdentity objectIdentity;

  /**
   * Creates a simple ACL containing the given access control entries that apply
   * to the given project configuration object.
   *
   * @param  object  Project configuration object.
   */
  public ProjectAcl(final ProjectConfig object)
  {
    objectIdentity = new ObjectIdentityImpl(ProjectConfig.class, object);
    entries = new ArrayList<AccessControlEntry>();
    for (PermissionConfig perm : object.getPermissions()) {
      entries.addAll(getAces(perm));
    }
  }

  /** {@inheritDoc}. */
  public List<AccessControlEntry> getEntries()
  {
    return entries;
  }

  /** {@inheritDoc}. */
  public ObjectIdentity getObjectIdentity()
  {
    return objectIdentity;
  }

  /**
   * Ownership is not supported.
   *
   * @return  null to indicate ownership not supported.
   */
  public Sid getOwner()
  {
    return null;
  }

  /**
   * ACL inheritance is not supported.
   *
   * @return  null
   */
  public Acl getParentAcl()
  {
    return null;
  }

  /**
   * ACL inheritance is not supported.
   *
   * @return  False in all cases.
   */
  public boolean isEntriesInheriting()
  {
    return false;
  }

  /** {@inheritDoc}. */
  public boolean isGranted(
    final List<Permission> permission,
    final List<Sid> sids,
    final boolean administrativeMode)
    throws NotFoundException, UnloadedSidException
  {
    for (AccessControlEntry ace : entries) {
      for (Sid sid : sids) {
        if (ace.getSid().equals(sid)) {
          // Exit on first matching permission
          for (Permission perm : permission) {
            if (ace.getPermission().equals(perm)) {
              return ace.isGranting();
            }
          }
        }
      }
    }
    // Implicit deny
    return false;
  }

  /**
   * All SIDs are loaded by default, so this method always returns true.
   *
   * @return  true
   */
  public boolean isSidLoaded(final List<Sid> sids)
  {
    return true;
  }

  /**
   * Gets the access control entries for the given permission configuration
   * object.
   *
   * @param  perm  Permission configuration object.
   *
   * @return  List of access control entries.
   */
  private List<AccessControlEntry> getAces(final PermissionConfig perm)
  {
    final List<AccessControlEntry> aceList =
      new ArrayList<AccessControlEntry>();
    for (Permission p : PermissionConfig.ALL_PERMISSIONS) {
      int result = p.getMask() & perm.getPermissionBits();
      if (result > 0) {
        Sid sid = null;
        if (perm.getName().startsWith("ROLE_")) {
          sid = new GrantedAuthoritySid(perm.getName());
        } else {
          sid = new PrincipalSid(perm.getName());
        }
        aceList.add(
          new AccessControlEntryImpl(
            perm.getProject(),
            this,
            sid,
            p,
            true,
            false,
            false));
      }
    }
    return aceList;
  }

}
