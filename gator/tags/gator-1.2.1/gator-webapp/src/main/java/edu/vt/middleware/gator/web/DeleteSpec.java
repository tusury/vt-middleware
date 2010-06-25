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

import javax.validation.constraints.AssertTrue;
import edu.vt.middleware.gator.Config;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Represents a request to delete a configuration object.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DeleteSpec
{
  private ProjectConfig project;

  private Config configToBeDeleted;

  private boolean confirmationFlag;

  private String typeName;


  /** @return  the project */
  public ProjectConfig getProject()
  {
    return project;
  }

  /** @param  p  the project to set */
  public void setProject(final ProjectConfig p)
  {
    project = p;
  }

  /** @return  the config */
  public Config getConfigToBeDeleted()
  {
    return configToBeDeleted;
  }

  /** @param  config  the config to set */
  public void setConfigToBeDeleted(final Config config)
  {
    configToBeDeleted = config;
  }

  /** @return  the confirmationFlag */
  @AssertTrue(message = "{deleteSpec.confirmationFlag.assertTrue}")
  public boolean getConfirmationFlag()
  {
    return confirmationFlag;
  }

  /** @param  confirm  the confirmationFlag to set */
  public void setConfirmationFlag(final boolean confirm)
  {
    this.confirmationFlag = confirm;
  }


  /** @param  typeName  the typeName to set */
  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }

  /** @return  the typeName */
  public String getTypeName()
  {
    return typeName;
  }


}
