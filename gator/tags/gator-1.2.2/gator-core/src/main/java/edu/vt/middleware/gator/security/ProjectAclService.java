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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.vt.middleware.gator.ProjectConfig;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;

/**
 * ACL service implementation for configuration objects. Supports queries for
 * any type of configuration object, but permissions only apply to {@link
 * ProjectConfig} objects, so for queries on all other types, the corresponding
 * project configuration is looked up then
 *
 * @author  Middleware Services
 * @version  $Revision: $
 */
public class ProjectAclService implements AclService
{

  /**
   * ACL inheritance is not supported.
   *
   * @param  parentIdentity  Parent object.
   *
   * @return  null -- inheritance is not supported.
   */
  public List<ObjectIdentity> findChildren(final ObjectIdentity parentIdentity)
  {
    return null;
  }

  /** {@inheritDoc}. */
  public Acl readAclById(final ObjectIdentity object)
    throws NotFoundException
  {
    final Object id = object.getIdentifier();
    if (id instanceof ProjectConfig) {
      return new ProjectAcl((ProjectConfig) id);
    } else {
      throw new NotFoundException(
        String.format("Invalid object identity '%s'", id));
    }
  }

  /** {@inheritDoc}. */
  public Acl readAclById(final ObjectIdentity object, final List<Sid> sids)
    throws NotFoundException
  {
    return readAclById(object);
  }

  /** {@inheritDoc}. */
  public Map<ObjectIdentity, Acl> readAclsById(
    final List<ObjectIdentity> objects)
    throws NotFoundException
  {
    final Map<ObjectIdentity, Acl> map = new HashMap<ObjectIdentity, Acl>(
      objects.size());
    for (ObjectIdentity object : objects) {
      map.put(object, readAclById(object));
    }
    return map;
  }

  /** {@inheritDoc}. */
  public Map<ObjectIdentity, Acl> readAclsById(
    final List<ObjectIdentity> objects,
    final List<Sid> sids)
    throws NotFoundException
  {
    final Map<ObjectIdentity, Acl> map = new HashMap<ObjectIdentity, Acl>(
      objects.size());
    for (ObjectIdentity object : objects) {
      map.put(object, readAclById(object, sids));
    }
    return map;
  }
}
