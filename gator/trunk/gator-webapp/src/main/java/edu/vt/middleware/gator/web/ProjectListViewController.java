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

import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.vt.middleware.gator.ConfigComparator;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Handles displaying a list of project configurations.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
public class ProjectListViewController extends AbstractController
{
  @RequestMapping(value = "/project/list.html", method = RequestMethod.GET)
  public String getProjects(final Model model)
  {
    final SortedSet<ProjectConfig> sortedProjects =
      new TreeSet<ProjectConfig>(new ConfigComparator());
    sortedProjects.addAll(configManager.findAll(ProjectConfig.class));
    model.addAttribute("projects", sortedProjects);
    return "projectList";
  }
}
