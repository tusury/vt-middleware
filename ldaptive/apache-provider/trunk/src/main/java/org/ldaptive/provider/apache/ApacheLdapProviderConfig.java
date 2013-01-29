/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.provider.apache;

import java.util.Arrays;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.shared.ldap.model.message.Control;
import org.ldaptive.ResultCode;
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

  /** Connection configuration. */
  private LdapConnectionConfig connectionConfig;

  /** Apache ldap specific control processor. */
  private ControlProcessor<Control> controlProcessor;


  /** Default constructor. */
  public ApacheLdapProviderConfig()
  {
    setOperationExceptionResultCodes(ResultCode.SERVER_DOWN);
    controlProcessor = new ControlProcessor<Control>(
      new ApacheLdapControlHandler());
  }


  /**
   * Returns the connection configuration.
   *
   * @return  connection configuration
   */
  public LdapConnectionConfig getLdapConnectionConfig()
  {
    return connectionConfig;
  }


  /**
   * Sets the connection configuration.
   *
   * @param  config  connection configuration
   */
  public void setLdapConnectionConfig(final LdapConnectionConfig config)
  {
    checkImmutable();
    logger.trace("setting ldapConnectionConfig: {}", config);
    connectionConfig = config;
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


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::operationExceptionResultCodes=%s, properties=%s, " +
        "connectionStrategy=%s, ldapConnectionConfig=%s, controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationExceptionResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        connectionConfig,
        controlProcessor);
  }
}
