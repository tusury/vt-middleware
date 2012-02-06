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
package org.ldaptive.provider.apache;

import java.util.Arrays;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import org.apache.directory.shared.ldap.model.message.Control;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderConfig;

/**
 * Contains configuration data for the Apache Ldap provider.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapProviderConfig extends ProviderConfig
{

  /** key managers used for SSL and TLS. */
  private KeyManager[] keyManagers;

  /** trust managers used for SSL and TLS. */
  private TrustManager[] trustManagers;

  /** Apache ldap specific control processor. */
  private ControlProcessor<Control> controlProcessor;


  /** Default constructor. */
  public ApacheLdapProviderConfig()
  {
    controlProcessor = new ControlProcessor<Control>(
      new ApacheLdapControlHandler());
  }


  /**
   * Returns the key managers to use for TLS/SSL connections.
   *
   * @return  key managers
   */
  public KeyManager[] getKeyManagers()
  {
    return keyManagers;
  }


  /**
   * Sets the key managers to use for TLS/SSL connections.
   *
   * @param  km  key managers
   */
  public void setKeyManagers(final KeyManager[] km)
  {
    checkImmutable();
    logger.trace("setting keyManagers: {}", Arrays.toString(km));
    keyManagers = km;
  }


  /**
   * Returns the trust managers to use for TLS/SSL connections.
   *
   * @return  trust managers
   */
  public TrustManager[] getTrustManagers()
  {
    return trustManagers;
  }


  /**
   * Sets the trust managers to use for TLS/SSL connections.
   *
   * @param  tm  trust managers
   */
  public void setTrustManagers(final TrustManager[] tm)
  {
    checkImmutable();
    logger.trace("setting trustManagers: {}", Arrays.toString(tm));
    trustManagers = tm;
  }


  /**
   * Returns the control processor.
   *
   * @return  control processor
   */
  public ControlProcessor<Control> getControlProcessor()
  {
    return controlProcessor;
  }


  /**
   * Sets the control processor.
   *
   * @param  processor  control processor
   */
  public void setControlProcessor(final ControlProcessor<Control> processor)
  {
    checkImmutable();
    logger.trace("setting controlProcessor: {}", processor);
    controlProcessor = processor;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::operationRetryResultCodes=%s, properties=%s, " +
        "connectionStrategy=%s, keyManagers=%s, trustManagers=%s, " +
        "controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        Arrays.toString(keyManagers),
        Arrays.toString(trustManagers),
        controlProcessor);
  }
}
