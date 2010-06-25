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
package edu.vt.middleware.gator.web.validation;

import javax.validation.constraints.NotNull;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.validation.AbstractAnnotationValidator;
import edu.vt.middleware.gator.web.CopySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

/**
 * Validates the {@link CopySpec} for the project copy operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ProjectCopyValidator extends AbstractAnnotationValidator
{
  @Autowired @NotNull
  protected ConfigManager configManager;


  /** {@inheritDoc}. */
  public boolean supports(Class<?> clazz)
  {
    return CopySpec.class.equals(clazz);
  }


  /** {@inheritDoc}. */
  @Override
  public void validate(final Object target, final Errors errors)
  {
    super.validate(target, errors);

    final CopySpec spec = (CopySpec) target;
    if (configManager.findProject(spec.getName()) != null) {
      errors.rejectValue(
        "name",
        "error.project.uniqueName",
        new Object[] {spec.getName()},
        "Duplicate project name.");
    }
  }
}
