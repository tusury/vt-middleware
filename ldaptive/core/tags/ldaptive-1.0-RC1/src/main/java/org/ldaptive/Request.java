/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import org.ldaptive.control.RequestControl;

/**
 * Marker interface for all ldap requests.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Request extends Message<RequestControl> {}
