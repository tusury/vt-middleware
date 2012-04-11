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

/**
 * Enum to define the type of attribute modification.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum AttributeModificationType {

  /** add an attribute. */
  ADD,

  /** replace an attribute. */
  REPLACE,

  /** remove an attribute. */
  REMOVE
}
