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
package org.ldaptive.auth;

/**
 * Enum to define authentication results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum AuthenticationResultCode {

  /** The configured authentication handler produced a result of true. */
  AUTHENTICATION_HANDLER_SUCCESS,

  /** The configured authentication handler produced a result of false. */
  AUTHENTICATION_HANDLER_FAILURE,

  /** The supplied credential was empty or null. */
  INVALID_CREDENTIAL,

  /** The configured DN resolver produced an empty or null value. */
  DN_RESOLUTION_FAILURE
}
