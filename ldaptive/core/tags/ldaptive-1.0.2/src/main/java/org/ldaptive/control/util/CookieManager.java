/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.control.util;

/**
 * Interface for the reading and writing of control related cookies.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface CookieManager
{


  /**
   * Read and return a cookie from storage.
   *
   * @return  cookie read from storage
   */
  byte[] readCookie();


  /**
   * Writes a cookie to storage.
   *
   * @param  cookie  to write
   */
  void writeCookie(byte[] cookie);
}
