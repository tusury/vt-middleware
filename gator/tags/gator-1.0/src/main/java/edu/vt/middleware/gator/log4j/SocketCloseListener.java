/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.log4j;

import java.net.Socket;

/**
 * Subscriber-side interface for a publisher-subscriber pattern that allows
 * subscribers to be notified of socket closing events.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public interface SocketCloseListener
{
  /**
   * Callback method is invoked on registered listeners whenever the given
   * socket closes.
   * @param sender Publisher of socket close event.
   * @param socket Socket that closed.
   */
  void socketClosed(Object sender, Socket socket);
}
