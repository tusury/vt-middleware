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

  /** Connection timeout. */
  private long timeOut;

  /** Whether to use SSL when connecting. */
  private boolean ssl;

  /** Whether to use TLS when connecting. */
  private boolean tls;

  /** key managers used for SSL and TLS. */
  private KeyManager[] keyManagers;

  /** trust managers used for SSL and TLS. */
  private TrustManager[] trustManagers;

  /** protocol for SSL and TLS. */
  private String sslProtocol;

  /** enabled cipher suites. */
  private String[] cipherSuites;

  /** Apache ldap specific control processor. */
  private ControlProcessor<Control> controlProcessor;


  /** Default constructor. */
  public ApacheLdapProviderConfig()
  {
    controlProcessor = new ControlProcessor<Control>(
      new ApacheLdapControlHandler());
  }


  /**
   * Returns the connect socket time out value.
   *
   * @return  socket time out
   */
  public long getTimeOut()
  {
    return timeOut;
  }


  /**
   * Sets the connect socket time out value.
   *
   * @param  l  timeout for socket connections
   */
  public void setTimeOut(final long l)
  {
    logger.trace("setting timeOut: {}", l);
    timeOut = l;
  }


  /**
   * Returns whether to use SSL for connections.
   *
   * @return  whether to use SSL for connections.
   */
  public boolean getSsl()
  {
    return ssl;
  }


  /**
   * Sets whether to use SSL for connections.
   *
   * @param  b  whether to use SSL for connections.
   */
  public void setSsl(final boolean b)
  {
    logger.trace("setting ssl: {}", b);
    ssl = b;
  }


  /**
   * Returns whether to use TLS for connections.
   *
   * @return  whether to use TLS for connections.
   */
  public boolean getTls()
  {
    return tls;
  }


  /**
   * Sets whether to use TLS for connections.
   *
   * @param  b  whether to use TLS for connections.
   */
  public void setTls(final boolean b)
  {
    logger.trace("setting tls: {}", b);
    tls = b;
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
    logger.trace("setting trustManagers: {}", Arrays.toString(tm));
    trustManagers = tm;
  }


  /**
   * Returns the protocol to use for TLS/SSL connections.
   *
   * @return  ssl protocol
   */
  public String getSslProtocol()
  {
    return sslProtocol;
  }


  /**
   * Sets the protocol to use for TLS/SSL connections.
   *
   * @param  protocol  ssl protocol
   */
  public void setSslProtocol(final String protocol)
  {
    logger.trace("setting sslProtocol: {}", protocol);
    sslProtocol = protocol;
  }


  /**
   * Returns the cipher suites to use for TLS/SSL connections.
   *
   * @return  cipher suites
   */
  public String[] getEnabledCipherSuites()
  {
    return cipherSuites;
  }


  /**
   * Sets the cipher suites to use for TLS/SSL connections.
   *
   * @param  cs  cipher suites
   */
  public void setEnabledCipherSuites(final String[] cs)
  {
    logger.trace("setting enabledCipherSuites: {}", cs);
    cipherSuites = cs;
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
        "connectionStrategy=%s, logCredentials=%s, timeOut=%s, " +
        "ssl=%s, tls=%s, keyManagers=%s, trustManagers=%s, " +
        "controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        getLogCredentials(),
        timeOut,
        ssl,
        tls,
        Arrays.toString(keyManagers),
        Arrays.toString(trustManagers),
        controlProcessor);
  }
}
