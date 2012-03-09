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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapResult;
import org.ldaptive.LdapUtil;

/**
 * Provides a custom implementation for adding LDAP principals to a subject that
 * represent roles.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapRole implements Principal, Serializable, Comparable<Principal>
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 421;

  /** serial version uid. */
  private static final long serialVersionUID = 1578734888816839199L;

  /** LDAP role name. */
  private String roleName;


  /**
   * Creates a new ldap role with the supplied name.
   *
   * @param  name  of this role
   */
  public LdapRole(final String name)
  {
    roleName = name;
  }


  /**
   * Returns the name for this ldap role.
   *
   * @return  role name
   */
  public String getName()
  {
    return roleName;
  }


  /**
   * Returns whether the supplied object is equal to this ldap role.
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
   * Returns the hash code for this ldap role.
   *
   * @return  hash code
   */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(HASH_CODE_SEED, roleName);
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
      String.format("[%s@%d::%s]", getClass().getName(), hashCode(), roleName);
  }


  /**
   * Principals are compared lexicographically by name. See {@link
   * Comparable#compareTo(Object)}.
   *
   * @param  p  principal to compare
   *
   * @return  a negative integer, zero, or a positive integer as this object is
   * less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(final Principal p)
  {
    return roleName.compareTo(p.getName());
  }


  /**
   * Iterates over the supplied result and returns all attributes as a set of
   * ldap roles.
   *
   * @param  result  to read
   *
   * @return  ldap roles
   */
  public static Set<LdapRole> toRoles(final LdapResult result)
  {
    final Set<LdapRole> r = new HashSet<LdapRole>();
    for (LdapEntry le : result.getEntries()) {
      r.addAll(toRoles(le));
    }
    return r;
  }


  /**
   * Iterates over the supplied entry and returns all attributes as a set of
   * ldap roles.
   *
   * @param  entry  to read
   *
   * @return  ldap roles
   */
  public static Set<LdapRole> toRoles(final LdapEntry entry)
  {
    return toRoles(entry.getAttributes());
  }


  /**
   * Iterates over the supplied attributes and returns all values as a set of
   * ldap roles.
   *
   * @param  attributes  to read
   *
   * @return  ldap roles
   */
  public static Set<LdapRole> toRoles(
    final Collection<LdapAttribute> attributes)
  {
    final Set<LdapRole> r = new HashSet<LdapRole>();
    if (attributes != null) {
      for (LdapAttribute ldapAttr : attributes) {
        for (String attrValue : ldapAttr.getStringValues()) {
          r.add(new LdapRole(attrValue));
        }
      }
    }
    return r;
  }
}
