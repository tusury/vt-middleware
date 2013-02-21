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

  /** OID for the notice of disconnection notification. */
  String NOTICE_OF_DISCONNECTION_OID = "1.3.6.1.4.1.1466.20036";


  /**
   * Processes an unsolicited notification from the server.
   *
   * @param  oid  of the unsolicited notification
   * @param  response  server response
   */
  void notificationReceived(String oid, Response<Void> response);
}
