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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.ConfigComparator;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Handles displaying a list of log4j project configurations.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class ProjectListViewController extends BaseViewController
{
  /** {@inheritDoc} */
  protected ModelAndView handleRequestInternal(
      final HttpServletRequest request,
      final HttpServletResponse response) throws Exception
  {
    final Map<String, Object> model = new HashMap<String, Object>();
    final SortedSet<ProjectConfig> sortedProjects =
      new TreeSet<ProjectConfig>(new ConfigComparator());
    sortedProjects.addAll(configManager.findAll(ProjectConfig.class));
    model.put("projects", sortedProjects);
    return new ModelAndView(getViewName(), "model", model);
  }

}
