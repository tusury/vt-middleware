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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import edu.vt.middleware.gator.Config;


/**
 * Describes a configuration object to be copied.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CopySpec
{

  /** Type of object being copied. */
  private Class<? extends Config> sourceType;

  /** ID of source config object to be copied. */
  private int sourceId;

  /** Name of new config object to be created. */
  private String name;


  public CopySpec(final Class<? extends Config> type)
  {
    sourceType = type;
  }


  /** @return  Type of source configuration object to be copied. */
  public Class<? extends Config> getSourceType()
  {
    return sourceType;
  }


  /** @return  the sourceProjectId */
  @Min(
    value = 1,
    message = "{copySpec.sourceId.min}"
  )
  public int getSourceId()
  {
    return sourceId;
  }


  /** @param  id  ID of source config object which will be copied. */
  public void setSourceId(final int id)
  {
    this.sourceId = id;
  }


  /** @return  Name of new configuration object. */
  @NotNull(message = "{copySpec.name.notNull}")
  public String getName()
  {
    return name;
  }


  /** @param  newName  Name of new configuration object created from source. */
  public void setName(final String newName)
  {
    this.name = newName;
  }
}
