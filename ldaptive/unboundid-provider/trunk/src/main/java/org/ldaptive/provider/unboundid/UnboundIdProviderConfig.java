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

  /** socket factory for ldap connections. */
  private SocketFactory socketFactory;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** Unbound id specific control processor. */
  private ControlProcessor<Control> controlProcessor;


  /** Default constructor. */
  public UnboundIdProviderConfig()
  {
    setOperationRetryResultCodes(
      new ResultCode[] {ResultCode.LDAP_TIMEOUT, ResultCode.CONNECT_ERROR, });
    setSearchIgnoreResultCodes(
      new ResultCode[] {
        ResultCode.TIME_LIMIT_EXCEEDED, ResultCode.SIZE_LIMIT_EXCEEDED, });
    controlProcessor = new ControlProcessor<Control>(
      new UnboundIdControlHandler());
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
    checkImmutable();
    logger.trace("setting socketFactory: {}", sf);
    socketFactory = sf;
  }


  /**
   * Returns the search ignore result codes.
   *
   * @return  result codes to ignore
   */
  public ResultCode[] getSearchIgnoreResultCodes()
  {
    return searchIgnoreResultCodes;
  }


  /**
   * Sets the search ignore result codes.
   *
   * @param  codes  to ignore
   */
  public void setSearchIgnoreResultCodes(final ResultCode[] codes)
  {
    checkImmutable();
    logger.trace("setting searchIgnoreResultCodes: {}", Arrays.toString(codes));
    searchIgnoreResultCodes = codes;
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
        "connectionStrategy=%s, socketFactory=%s, " +
        "searchIgnoreResultCodes=%s, controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        socketFactory,
        Arrays.toString(searchIgnoreResultCodes),
        controlProcessor);
  }
}
