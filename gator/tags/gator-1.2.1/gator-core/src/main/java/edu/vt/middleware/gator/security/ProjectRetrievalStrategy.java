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
package edu.vt.middleware.gator.security;

import edu.vt.middleware.gator.ProjectConfig;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

/**
 * Domain object retrieval strategy for project configurations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ProjectRetrievalStrategy implements ObjectIdentityRetrievalStrategy
{

  /** {@inheritDoc}. */
  public ObjectIdentity getObjectIdentity(final Object domainObject)
  {
    if (!(domainObject instanceof ProjectConfig)) {
      throw new IllegalArgumentException(
        "Domain object must be instance of ProjectConfig.");
    }
    return
      new ObjectIdentityImpl(
        domainObject.getClass(),
        (ProjectConfig) domainObject);
  }

}
