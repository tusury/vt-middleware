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

import org.ldaptive.sasl.SaslConfig;

/**
 * Provides implementation common to bind authentication handlers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractBindAuthenticationHandler
  extends AbstractAuthenticationHandler
{

  /** sasl configuration used by this handler. */
  private SaslConfig authenticationSaslConfig;


  /**
   * Returns the sasl config for this authentication handler.
   *
   * @return  sasl config
   */
  public SaslConfig getAuthenticationSaslConfig()
  {
    return authenticationSaslConfig;
  }


  /**
   * Sets the sasl config for this authentication handler.
   *
   * @param  config  sasl config
   */
  public void setAuthenticationSaslConfig(final SaslConfig config)
  {
    authenticationSaslConfig = config;
  }
}
