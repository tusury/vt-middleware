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
package org.ldaptive.schema;

/**
 * Enum for an attribute usage schema element.
 *
 * <pre>
   ObjectClassType = "ABSTRACT" / "STRUCTURAL" / "AUXILIARY"
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum ObjectClassType {

  /** abstract. */
  ABSTRACT,

  /** structural. */
  STRUCTURAL,

  /** auxiliary. */
  AUXILIARY
}
