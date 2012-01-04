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
package org.ldaptive.provider.unboundid;

import java.util.Arrays;
import javax.net.SocketFactory;
import com.unboundid.ldap.sdk.Control;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderConfig;

/**
 * Contains configuration data for the UnboundId provider.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIdProviderConfig extends ProviderConfig
{

  /** Amount of time in milliseconds that connect operations will block. */
  private int connectTimeout;

  /** Amount of time in milliseconds that operations will wait. */
  private long responseTimeout;

  /** socket factory for ldap connections. */
  private SocketFactory socketFactory;

  /** Whether to use TLS when connecting. */
  private boolean tls;

  /** Unbound id specific control processor. */
  private ControlProcessor<Control> controlProcessor;


  /** Default constructor. */
  public UnboundIdProviderConfig()
  {
    setOperationRetryResultCodes(
      new ResultCode[] {ResultCode.LDAP_TIMEOUT, ResultCode.CONNECT_ERROR, });
    controlProcessor = new ControlProcessor<Control>(
      new UnboundIdControlHandler());
  }


  /**
   * Returns the connect time out value.
   *
   * @return  connect time out
   */
  public int getConnectTimeout()
  {
    return connectTimeout;
  }


  /**
   * Sets the connect time out value.
   *
   * @param  timeout  for connections
   */
  public void setConnectionTimeOut(final int timeout)
  {
    logger.trace("setting connectTimeout: {}", timeout);
    connectTimeout = timeout;
  }


  /**
   * Returns the response time out in milliseconds.
   *
   * @return  response time out
   */
  public long getResponseTimeout()
  {
    return responseTimeout;
  }


  /**
   * Sets the response time out.
   *
   * @param  timeout  time in milliseconds
   */
  public void setResponseTimeout(final long timeout)
  {
    logger.trace("setting responseTimeout: {}", timeout);
    responseTimeout = timeout;
  }


  /**
   * Returns the socket factory to use for LDAP connections.
   *
   * @return  socket factory
   */
  public SocketFactory getSocketFactory()
  {
    return socketFactory;
  }


  /**
   * Sets the socket factory to use for LDAP connections.
   *
   * @param  sf  socket factory
   */
  public void setSocketFactory(final SocketFactory sf)
  {
    logger.trace("setting socketFactory: {}", sf);
    socketFactory = sf;
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
        "connectionStrategy=%s, logCredentials=%s, connectTimeout=%s, " +
        "responseTimeout=%s, socketFactory=%s, controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        getLogCredentials(),
        connectTimeout,
        responseTimeout,
        socketFactory,
        controlProcessor);
  }
}
