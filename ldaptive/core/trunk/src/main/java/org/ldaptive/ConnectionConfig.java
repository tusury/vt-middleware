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
package org.ldaptive;

import java.util.Arrays;
import org.ldaptive.control.RequestControl;
import org.ldaptive.sasl.SaslConfig;
import org.ldaptive.ssl.SslConfig;

/**
 * Contains all the configuration data needed to control connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectionConfig extends AbstractConfig
{

  /** URL to the LDAP(s). */
  private String ldapUrl;

  /** Amount of time in milliseconds that connects will block. */
  private long connectTimeout = -1;

  /** Amount of time in milliseconds to wait for responses. */
  private long responseTimeout = -1;

  /** DN to bind as before performing operations. */
  private String bindDn;

  /** Credential for the bind DN. */
  private Credential bindCredential;

  /** Configuration for bind SASL authentication. */
  private SaslConfig bindSaslConfig;

  /** Bind controls. */
  private RequestControl[] bindControls;

  /** Configuration for SSL and startTLS connections. */
  private SslConfig sslConfig;

  /** Connect to LDAP using SSL protocol. */
  private boolean useSSL;

  /** Connect to LDAP using startTLS. */
  private boolean useStartTLS;


  /** Default constructor. */
  public ConnectionConfig() {}


  /**
   * Creates a new ldap config.
   *
   * @param  url  to connect to
   */
  public ConnectionConfig(final String url)
  {
    setLdapUrl(url);
  }


  /**
   * Returns the ldap url.
   *
   * @return  ldap url
   */
  public String getLdapUrl()
  {
    return ldapUrl;
  }


  /**
   * Sets the ldap url.
   *
   * @param  url  of the ldap
   */
  public void setLdapUrl(final String url)
  {
    checkImmutable();
    checkStringInput(url, true);
    logger.trace("setting ldapUrl: {}", url);
    ldapUrl = url;
  }


  /**
   * Returns the connect timeout. If this value is <= 0, then connects will wait
   * indefinitely.
   *
   * @return  timeout
   */
  public long getConnectTimeout()
  {
    return connectTimeout;
  }


  /**
   * Sets the maximum amount of time in milliseconds that connects will block.
   *
   * @param  time  timeout for connects
   */
  public void setConnectTimeout(final long time)
  {
    checkImmutable();
    logger.trace("setting connectTimeout: {}", time);
    connectTimeout = time;
  }


  /**
   * Returns the response timeout. If this value is <= 0, then operations will
   * wait indefinitely for a response.
   *
   * @return  timeout
   */
  public long getResponseTimeout()
  {
    return responseTimeout;
  }


  /**
   * Sets the maximum amount of time in milliseconds that operations will wait
   * for a response.
   *
   * @param  time  timeout for responses
   */
  public void setResponseTimeout(final long time)
  {
    checkImmutable();
    logger.trace("setting responseTimeout: {}", time);
    responseTimeout = time;
  }


  /**
   * Returns the bind DN.
   *
   * @return  DN to bind as
   */
  public String getBindDn()
  {
    return bindDn;
  }


  /**
   * Sets the bind DN to authenticate as before performing operations.
   *
   * @param  dn  to bind as
   */
  public void setBindDn(final String dn)
  {
    checkImmutable();
    checkStringInput(dn, true);
    logger.trace("setting bindDn: {}", dn);
    bindDn = dn;
  }


  /**
   * Returns the credential used with the bind DN.
   *
   * @return  bind DN credential
   */
  public Credential getBindCredential()
  {
    return bindCredential;
  }


  /**
   * Sets the credential of the bind DN.
   *
   * @param  credential  to use with bind DN
   */
  public void setBindCredential(final Credential credential)
  {
    checkImmutable();
    logger.trace("setting bindCredential: <suppressed>");
    bindCredential = credential;
  }


  /**
   * Returns the bind sasl config.
   *
   * @return  sasl config
   */
  public SaslConfig getBindSaslConfig()
  {
    return bindSaslConfig;
  }


  /**
   * Sets the bind sasl config.
   *
   * @param  config  sasl config
   */
  public void setBindSaslConfig(final SaslConfig config)
  {
    checkImmutable();
    logger.trace("setting bindSaslConfig: {}", config);
    bindSaslConfig = config;
  }


  /**
   * Returns the bind controls.
   *
   * @return  controls
   */
  public RequestControl[] getBindControls()
  {
    return bindControls;
  }


  /**
   * Sets the bind controls.
   *
   * @param  c  controls to set
   */
  public void setBindControls(final RequestControl... c)
  {
    checkImmutable();
    logger.trace("setting bindControls: {}", Arrays.toString(c));
    bindControls = c;
  }


  /**
   * Returns the ssl config.
   *
   * @return  ssl config
   */
  public SslConfig getSslConfig()
  {
    return sslConfig;
  }


  /**
   * Sets the ssl config.
   *
   * @param  config  ssl config
   */
  public void setSslConfig(final SslConfig config)
  {
    checkImmutable();
    logger.trace("setting sslConfig: {}", config);
    sslConfig = config;
  }


  /**
   * Returns whether the SSL protocol will be used for connections.
   *
   * @return  whether the SSL protocol will be used
   */
  public boolean getUseSSL()
  {
    return useSSL;
  }


  /**
   * Sets whether the SSL protocol will be used for connections.
   *
   * @param  b  whether the SSL protocol will be used
   */
  public void setUseSSL(final boolean b)
  {
    checkImmutable();
    logger.trace("setting useSSL: {}", b);
    useSSL = b;
  }


  /**
   * Returns whether startTLS will be used for connections.
   *
   * @return  whether startTLS will be used
   */
  public boolean getUseStartTLS()
  {
    return useStartTLS;
  }


  /**
   * Sets whether startTLS will be used for connections.
   *
   * @param  b  whether startTLS will be used
   */
  public void setUseStartTLS(final boolean b)
  {
    checkImmutable();
    logger.trace("setting useStartTLS: {}", b);
    useStartTLS = b;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::ldapUrl=%s, connectTimeout=%s, responseTimeout=%s, " +
        "bindDn=%s, bindSaslConfig=%s, bindControls=%s, sslConfig=%s, " +
        "useSSL=%s, useStartTLS=%s]",
        getClass().getName(),
        hashCode(),
        ldapUrl,
        connectTimeout,
        responseTimeout,
        bindDn,
        bindSaslConfig,
        Arrays.toString(bindControls),
        sslConfig,
        useSSL,
        useStartTLS);
  }
}
