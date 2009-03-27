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
package edu.vt.middleware.gator.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.util.RequestParamExtractor;

/**
 * Superclass for all delete controllers.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class BaseDeleteFromController extends BaseFormController
{
  /** {@inheritDoc} */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request)
      throws Exception
  {
    final Map<String, Object> refData = new HashMap<String, Object>();
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    refData.put("project", project);
    return refData;
  }

  /**
   * Validates the given delete specification.
   * @param errors Form binding errors.
   * @param spec To be validated.
   * @return True if validation succeeded, false otherwise.
   */
  protected boolean validate(final BindException errors, final DeleteSpec spec)
  {
    if (spec.getConfirmationFlag()) {
      return true;
    } else {
      errors.reject(
        "error.delete.confirmation",
        "Delete confirmation required.");
      return false;
    }
  }
}
