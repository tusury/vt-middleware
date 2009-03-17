/*
  $Id: $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.AclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.sid.Sid;
import org.springframework.util.Assert;

import edu.vt.middleware.gator.Config;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * ACL service implementation for configuration objects.  Supports
 * queries for any type of configuration object, but permissions
 * only apply to {@link ProjectConfig} objects, so for queries on
 * all other types, the corresponding project configuration is looked
 * up then 
 *
 * @author Middleware
 * @version $Revision: $
 *
 */
public class ConfigAclService implements AclService, InitializingBean
{
  /** Configuration manager */
  private ConfigManager configManager;
  

  /**
   * @param configManager the configManager to set
   */
  public void setConfigManager(final ConfigManager configManager)
  {
    this.configManager = configManager;
  }

  /** {@inheritDoc} */
  public void afterPropertiesSet() throws Exception
  {
    Assert.notNull(configManager, "configManger cannot be null.");
  }

  /**
   * ACL inheritance is not supported.
   * @param parentIdentity Parent object.
   * @return null -- inheritance is not supported.
   */
  public ObjectIdentity[] findChildren(final ObjectIdentity parentIdentity)
  {
    return null;
  }

  /** {@inheritDoc} */
  public Acl readAclById(final ObjectIdentity object) throws NotFoundException
  {
    final Object id = object.getIdentifier();
    if (id instanceof Config) {
      final ProjectConfig project = configManager.getProject((Config) id);
      if (project == null) {
        throw new NotFoundException("Could not find project for " + id);
      }
      return new ProjectAcl(project);
    } else {
      throw new NotFoundException("Invalid object identity " + object);
    }
  }

  /** {@inheritDoc} */
  public Acl readAclById(final ObjectIdentity object, final Sid[] sids)
    throws NotFoundException
  {
    return readAclById(object);
  }

  /** {@inheritDoc} */
  public Map<ObjectIdentity, Acl> readAclsById(final ObjectIdentity[] objects)
    throws NotFoundException
  {
    final Map<ObjectIdentity, Acl> map =
      new HashMap<ObjectIdentity, Acl>(objects.length);
    for (ObjectIdentity object : objects) {
      map.put(object, readAclById(object));
    }
    return map;
  }

  /** {@inheritDoc} */
  public Map<ObjectIdentity, Acl> readAclsById(
    final ObjectIdentity[] objects,
    final Sid[] sids)
    throws NotFoundException
  {
    final Map<ObjectIdentity, Acl> map =
      new HashMap<ObjectIdentity, Acl>(objects.length);
    for (ObjectIdentity object : objects) {
      map.put(object, readAclById(object, sids));
    }
    return map;
  }

}
