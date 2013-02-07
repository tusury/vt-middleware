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
package org.ldaptive.extended;

import org.ldaptive.Response;

/**
 * Processes an unsolicited notification.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface UnsolicitedNotificationListener
{


  /**
   * Processes an unsolicited notification from the server.
   *
   * @param  oid  of the unsolicited notification
   * @param  response  server response
   */
  void notificationReceived(String oid, Response<Void> response);
}
