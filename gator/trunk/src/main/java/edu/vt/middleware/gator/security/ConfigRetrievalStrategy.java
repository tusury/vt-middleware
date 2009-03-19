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
package edu.vt.middleware.gator.security;

import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;

import edu.vt.middleware.gator.Config;

/**
 * Domain object retrieval strategy for configuration objects.  Uses the
 * object itself as the identifier.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ConfigRetrievalStrategy implements ObjectIdentityRetrievalStrategy
{
  /** {@inheritDoc} */
  public ObjectIdentity getObjectIdentity(final Object domainObject)
  {
    if (!(domainObject instanceof Config)) {
      throw new IllegalArgumentException(
        "Domain object must be instance of Config.");
    }
    return new ObjectIdentityImpl(
      domainObject.getClass(),
      (Config) domainObject);
  }

}
