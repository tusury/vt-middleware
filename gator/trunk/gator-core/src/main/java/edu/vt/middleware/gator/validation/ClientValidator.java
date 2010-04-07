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

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Validates client configuration elements.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ClientValidator extends AbstractAnnotationValidator
{
  @Autowired
  @NotNull
  private ConfigManager configManager;


  /** {@inheritDoc} */
  public boolean supports(Class<?> clazz)
  {
    return ClientConfig.class.equals(clazz);
  }


  /** {@inheritDoc} */
  @Override
  public void validate(final Object target, final Errors errors)
  {
    super.validate(target, errors);
   
    final ClientConfig client = (ClientConfig) target;
    // Ensure client names are globally unique
    final List<ProjectConfig> otherProjects =
      configManager.findProjectsByClientName(client.getName());
    for (ProjectConfig p : otherProjects)
    {
      if (!p.equals(client.getProject())) {
        errors.rejectValue(
          "name",
          "error.client.globallyUnique",
          new Object[] {client.getName(), p.getName()},
          "A client is only allowed in a single project.");
        break;
      }
    }
  }
}
