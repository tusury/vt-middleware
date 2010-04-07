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
package edu.vt.middleware.gator.validation;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.validation.Errors;

import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Validates project permission configuration data.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PermissonValidator extends AbstractAnnotationValidator
{
  @Autowired
  @NotNull
  private ConfigManager configManager;

  
  /** {@inheritDoc} */
  public boolean supports(final Class<?> clazz)
  {
    return PermissionConfig.class.equals(clazz);
  }


  /** {@inheritDoc} */
  @Override
  public void validate(final Object target, final Errors errors)
  {
    super.validate(target, errors);
    final PermissionConfig permFromDb = configManager.find(
        PermissionConfig.class,
        ((PermissionConfig) target).getId());
    if (permFromDb != null) {
      if (isLastFullPermissions(permFromDb.getProject(), permFromDb.getId())) {
        errors.rejectValue(
            "permissions",
            "error.edit.lastAllPermissions",
        "Cannot modify last permission entry with full permissions.");
      }
    }
  }


  /**
   * Determines whether the given permission is the last full permission in
   * the given project.
   * @param project Project to test.
   * @param permissionId ID of permission to check.
   * @return True if given permission is last full permission in the given
   * project.
   */
  private static boolean isLastFullPermissions(
    final ProjectConfig project,
    final int permissionId)
  {
    int count = 0;
    int fullPermissionId = 0;
    for (PermissionConfig perm : project.getPermissions()) {
      if (perm.hasPermission(BasePermission.READ) &&
          perm.hasPermission(BasePermission.WRITE) &&
          perm.hasPermission(BasePermission.DELETE))
      {
        count++;
        fullPermissionId = perm.getId();
      }
    }
    return count <= 1 && fullPermissionId == permissionId;
  }
}
