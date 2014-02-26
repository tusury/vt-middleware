/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.beans.spring;

import org.ldaptive.beans.AbstractLdapEntryMapper;
import org.ldaptive.beans.ClassDescriptor;

/**
 * Uses a {@link SpringClassDescriptor} for ldap entry mapping.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SpringLdapEntryMapper extends AbstractLdapEntryMapper<Object>
{


  /** {@inheritDoc} */
  @Override
  protected ClassDescriptor getClassDescriptor(final Object object)
  {
    final SpringClassDescriptor descriptor = new SpringClassDescriptor(object);
    descriptor.initialize(object.getClass());
    return descriptor;
  }
}
