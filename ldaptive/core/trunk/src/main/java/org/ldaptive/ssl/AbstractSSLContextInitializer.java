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
package org.ldaptive.ssl;

import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;

/**
 * Provides common implementation for SSL context initializer.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractSSLContextInitializer
  implements SSLContextInitializer
{


  /** {@inheritDoc} */
  @Override
  public SSLContext initSSLContext(final String protocol)
    throws GeneralSecurityException
  {
    final SSLContext ctx = SSLContext.getInstance(protocol);
    ctx.init(getKeyManagers(), getTrustManagers(), null);
    return ctx;
  }
}
