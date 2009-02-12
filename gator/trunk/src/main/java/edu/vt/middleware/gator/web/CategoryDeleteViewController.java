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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Handles deletion of category configuration items.
 *
 * @author Marvin S. Addison
 *
 */
public class CategoryDeleteViewController extends BaseViewController
{
  /** {@inheritDoc} */
  protected ModelAndView handleRequestInternal(
      final HttpServletRequest request,
      final HttpServletResponse response) throws Exception
  {
    final CategoryConfig category = configManager.find(
      CategoryConfig.class,
      RequestParamExtractor.getCategoryId(request));
    if (category == null) {
      throw new IllegalArgumentException(
	      "Illegal attempt to delete non-existent category.");
    }
    final ProjectConfig project = category.getProject();
    project.removeCategory(category);
    configManager.delete(category);
    return new ModelAndView(
        ControllerHelper.filterViewName(getViewName(), project));
  }
}
