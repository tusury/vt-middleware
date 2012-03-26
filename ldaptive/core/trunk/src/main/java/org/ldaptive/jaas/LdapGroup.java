/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.jaas;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.ldaptive.LdapUtils;

/**
 * Provides a custom implementation for grouping principals.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapGroup implements Group, Serializable
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 431;

  /** serial version uid. */
  private static final long serialVersionUID = 2075424472884533862L;

  /** LDAP role name. */
  private String roleName;

  /** Principal members. */
  private Set<Principal> members = new HashSet<Principal>();


  /**
   * Creates a new ldap group with the supplied name.
   *
   * @param  name  of the group
   */
  public LdapGroup(final String name)
  {
    roleName = name;
  }


  /**
   * Returns the name for this ldap group.
   *
   * @return  name
   */
  public String getName()
  {
    return roleName;
  }


  /** {@inheritDoc} */
  @Override
  public boolean addMember(final Principal user)
  {
    return members.add(user);
  }


  /** {@inheritDoc} */
  @Override
  public boolean removeMember(final Principal user)
  {
    return members.remove(user);
  }


  /** {@inheritDoc} */
  @Override
  public boolean isMember(final Principal member)
  {
    for (Principal p : members) {
      if (p.getName() != null && p.getName().equals(member.getName())) {
        return true;
      }
    }
    return false;
  }


  /** {@inheritDoc} */
  @Override
  public Enumeration<? extends Principal> members()
  {
    return Collections.enumeration(members);
  }


  /**
   * Returns an unmodifiable set of the members in this group.
   *
   * @return  set of member principals
   */
  public Set<Principal> getMembers()
  {
    return Collections.unmodifiableSet(members);
  }


  /**
   * Returns whether the supplied object is equal to this ldap group.
   *
   * @param  o  to compare
   *
   * @return  whether the supplied object is equal
   */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this || (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /**
   * Returns the hash code for this ldap group.
   *
   * @return  hash code
   */
  @Override
  public int hashCode()
  {
    return LdapUtils.computeHashCode(HASH_CODE_SEED, roleName, members);
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::%s%s]",
        getClass().getName(),
        hashCode(),
        roleName,
        members);
  }
}
