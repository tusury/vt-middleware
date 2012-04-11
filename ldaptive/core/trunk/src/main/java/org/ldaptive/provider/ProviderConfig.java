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
package org.ldaptive.provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.ldaptive.AbstractConfig;
import org.ldaptive.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains configuration data common to providers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ProviderConfig extends AbstractConfig
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Result codes indicating that an operation should be retried. */
  private ResultCode[] operationRetryResultCodes;

  /** Additional provider properties. */
  private Map<String, Object> properties = new HashMap<String, Object>();

  /** Connection strategy. */
  private ConnectionStrategy connectionStrategy = ConnectionStrategy.DEFAULT;


  /**
   * Returns the result codes that trigger an operation retry.
   *
   * @return  ldap result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the result codes that trigger an operation retry.
   *
   * @param  codes  ldap result codes
   */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    checkImmutable();
    logger.trace(
      "setting operationRetryResultCodes: {}",
      Arrays.toString(codes));
    operationRetryResultCodes = codes;
  }


  /**
   * Returns provider specific properties.
   *
   * @return  map of additional provider properties
   */
  public Map<String, Object> getProperties()
  {
    return properties;
  }


  /**
   * Sets provider specific properties.
   *
   * @param  props  map of additional provider properties
   */
  public void setProperties(final Map<String, Object> props)
  {
    checkImmutable();
    logger.trace("setting properties: {}", props);
    properties = props;
  }


  /**
   * Returns the connection strategy.
   *
   * @return  strategy for making connections
   */
  public ConnectionStrategy getConnectionStrategy()
  {
    return connectionStrategy;
  }


  /**
   * Sets the connection strategy.
   *
   * @param  strategy  for making connections
   */
  public void setConnectionStrategy(final ConnectionStrategy strategy)
  {
    checkImmutable();
    logger.trace("setting connectionStrategy: {}", strategy);
    connectionStrategy = strategy;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::operationRetryResultCodes=%s, properties=%s, " +
        "connectionStrategy=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(operationRetryResultCodes),
        properties,
        connectionStrategy);
  }
}
