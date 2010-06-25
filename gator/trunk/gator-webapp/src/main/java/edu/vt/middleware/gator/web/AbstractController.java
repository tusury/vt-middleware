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
package edu.vt.middleware.gator.web;

import javax.validation.constraints.NotNull;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for all controllers that simply display views.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractController
{
  protected final Log logger = LogFactory.getLog(getClass());

  @Autowired @NotNull
  protected ConfigManager configManager;


  /**
   * Loads the project of the given name from the database.
   *
   * @param  name  Project name.
   *
   * @return  Project with given name.
   *
   * @throws  IllegalArgumentException  If no project exists with given name.
   */
  public ProjectConfig getProject(final String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("No project name specified.");
    }

    final ProjectConfig project = configManager.findProject(name);
    if (project == null) {
      throw new IllegalArgumentException(
        String.format("Project '%s' not found.", name));
    }
    return project;
  }
}
