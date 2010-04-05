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
package edu.vt.middleware.gator.web.validation;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.validation.AbstractAnnotationValidator;
import edu.vt.middleware.gator.web.CopySpec;

/**
 * Validates the {@link CopySpec} for the appender copy operation.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AppenderCopyValidator extends AbstractAnnotationValidator
{
  @Autowired
  @NotNull
  protected ConfigManager configManager;


  /** {@inheritDoc} */
  public boolean supports(Class<?> clazz)
  {
    return CopySpec.class.equals(clazz);
  }


  /** {@inheritDoc} */
  @Override
  public void validate(final Object target, final Errors errors)
  {
    super.validate(target, errors);
    final CopySpec spec = (CopySpec) target;
    if (AppenderConfig.class.equals(spec.getSourceType())) {
      final AppenderConfig source = configManager.find(AppenderConfig.class, spec.getSourceId());
      if (source.getProject().getAppender(spec.getName()) != null) {
	      errors.rejectValue("name", "error.appender.uniqueName",
	          new Object[] {spec.getName()}, "Duplicate appender name.");
      }
    } else {
      errors.rejectValue("name", "error.appender.sourceType", "Copy source is not an appender.");
    }
  }

}
