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

/**
 * Represents a request to delete a configuration object.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class DeleteSpec
{
  private boolean confirmationFlag;
  
  private String name;
  
  private int id;

  /**
   * @return the confirmationFlag
   */
  public boolean getConfirmationFlag()
  {
    return confirmationFlag;
  }

  /**
   * @param confirm the confirmationFlag to set
   */
  public void setConfirmationFlag(final boolean confirm)
  {
    this.confirmationFlag = confirm;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name)
  {
    this.name = name;
  }

  /**
   * @return the id
   */
  public int getId()
  {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final int id)
  {
    this.id = id;
  }


}
