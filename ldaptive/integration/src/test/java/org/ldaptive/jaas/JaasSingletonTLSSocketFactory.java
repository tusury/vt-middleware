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
package org.ldaptive.jaas;

import java.security.GeneralSecurityException;
import javax.net.SocketFactory;
import org.ldaptive.ssl.SingletonTLSSocketFactory;
import org.slf4j.LoggerFactory;

/**
 * Singleton TLS socket factory for JAAS testing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JaasSingletonTLSSocketFactory extends SingletonTLSSocketFactory
{


  /**
   * This returns the default SSL socket factory.
   *
   * @return  socket factory
   */
  public static SocketFactory getDefault()
  {
    final JaasSingletonTLSSocketFactory sf =
      new JaasSingletonTLSSocketFactory();
    try {
      sf.initialize();
    } catch (GeneralSecurityException e) {
      LoggerFactory.getLogger(JaasSingletonTLSSocketFactory.class).error(
        "Error initializing socket factory",
        e);
    }
    return sf;
  }
}
