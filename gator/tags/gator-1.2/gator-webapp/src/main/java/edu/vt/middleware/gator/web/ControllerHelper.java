/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
 */
package edu.vt.middleware.gator.web;

import org.springframework.security.acls.model.Permission;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.LayoutParamConfig;
import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Utility class provides common controller operations.
 *
 * @author Marvin S. Addison
 *
 */
public class ControllerHelper
{
  /** Creates a new instance */
  protected ControllerHelper() {}


  /**
   * Creates a deep clone of the given appender.
   * @param source Appender to clone.
   * @return Cloned appender.
   */
  public static AppenderConfig cloneAppender(final AppenderConfig source)
  {
    final AppenderConfig clone = new AppenderConfig();
    clone.setName(source.getName());
    clone.setAppenderClassName(source.getAppenderClassName());
    clone.setErrorHandlerClassName(source.getErrorHandlerClassName());
    clone.setLayoutClassName(source.getLayoutClassName());
    for (AppenderParamConfig param : source.getAppenderParams()) {
      clone.addAppenderParam(cloneParameter(param));
    }
    for (LayoutParamConfig param : source.getLayoutParams()) {
      clone.addLayoutParam(cloneParameter(param));
    }
    return clone;
  }
  

  /**
   * Creates a clone of the given appender parameter.
   * @param source Source appender parameter to clone.
   * @return Cloned parameter.
   */
  public static AppenderParamConfig cloneParameter(
      final AppenderParamConfig source)
  {
    final AppenderParamConfig newParam = new AppenderParamConfig();
    newParam.setName(source.getName());
    newParam.setValue(source.getValue());
    return newParam;
  }
  

  /**
   * Creates a clone of the given layout parameter.
   * @param source Source layout parameter to clone.
   * @return Cloned parameter.
   */
  public static LayoutParamConfig cloneParameter(
      final LayoutParamConfig source)
  {
    final LayoutParamConfig newParam = new LayoutParamConfig();
    newParam.setName(source.getName());
    newParam.setValue(source.getValue());
    return newParam;
  }


  /**
   * Creates a deep clone of the given category.
   * @param parent Project to which cloned category will eventually belong.
   * Only appenders in the source category that also belong to the parent
   * will be associated with the cloned category.
   * @param source Category to clone.
   * @return Cloned category.
   */
  public static CategoryConfig cloneCategory(
    final ProjectConfig parent,
    final CategoryConfig source)
  {
    final CategoryConfig clone = new CategoryConfig();
    clone.setName(source.getName());
    clone.setLevel(source.getLevel());
    clone.setAdditivity(source.getAdditivity());
    clone.setAllowSocketAppender(source.isAllowSocketAppender());
    for (AppenderConfig appender : source.getAppenders()) {
      final AppenderConfig appenderRef = parent.getAppender(appender.getName());
      if (appenderRef != null) {
        clone.getAppenders().add(appenderRef);
      }
    }
    return clone;
  }


  /**
   * Creates a deep clone of the given client.
   * @param source Client to clone.
   * @return Cloned client.
   */
  public static ClientConfig cloneClient(final ClientConfig source)
  {
    final ClientConfig clone = new ClientConfig();
    clone.setName(source.getName());
    return clone;
  }


  /**
   * Creates a deep clone of the given project.  All fields except
   * modifiedDate are cloned; the modified date is set to the current
   * system date/time.
   * @param source Project to clone.
   * @return Cloned project.
   */
  public static ProjectConfig cloneProject(final ProjectConfig source)
  {
    final ProjectConfig clone = new ProjectConfig();
    clone.setName(source.getName());
    clone.setClientLogDir(source.getClientLogDir());
    for (AppenderConfig appender : source.getAppenders()) {
      clone.addAppender(cloneAppender(appender));
    }
    for (CategoryConfig category : source.getCategories()) {
      clone.addCategory(cloneCategory(clone, category));
    }
    return clone;
  }


  /**
   * Create a permission configuration containing all permissions for the
   * given security identifier.
   * @param sid Security identifier; either a username or role name.
   * @return Permission config with all permissions set for given SID.
   */
  public static PermissionConfig createAllPermissions(final String sid)
  {
    final PermissionConfig perm = new PermissionConfig();
    perm.setName(sid);
    int permBits = 0;
    for (Permission p : PermissionConfig.ALL_PERMISSIONS) {
      permBits |= p.getMask();
    }
    perm.setPermissionBits(permBits);
    return perm;
  }
}
