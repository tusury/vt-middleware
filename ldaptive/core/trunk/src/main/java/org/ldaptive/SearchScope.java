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
 * Enum to define the type of search scope.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum SearchScope {

  /** object level search. */
  OBJECT,

  /** one level search. */
  ONELEVEL,

  /** subtree search. */
  SUBTREE
}
