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
package org.ldaptive.provider.netscape;

import java.util.Arrays;
import netscape.ldap.LDAPControl;
import netscape.ldap.LDAPSocketFactory;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderConfig;

/**
 * Contains configuration data for the Netscape provider.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeProviderConfig extends ProviderConfig
{

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** Socket factory used for SSL. */
  private LDAPSocketFactory ldapSocketFactory;

  /** Netscape specific control handler. */
  private ControlProcessor<LDAPControl> controlProcessor;


  /** Default constructor. */
  public NetscapeProviderConfig()
  {
    setOperationRetryResultCodes(
      new ResultCode[] {ResultCode.LDAP_TIMEOUT, ResultCode.CONNECT_ERROR, });
    searchIgnoreResultCodes = new ResultCode[] {
      ResultCode.TIME_LIMIT_EXCEEDED,
      ResultCode.SIZE_LIMIT_EXCEEDED,
    };
    controlProcessor = new ControlProcessor<LDAPControl>(
      new NetscapeControlHandler());
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
   * Returns the LDAP socket factory to use for SSL connections.
   *
   * @return  LDAP socket factory
   */
  public LDAPSocketFactory getLDAPSocketFactory()
  {
    return ldapSocketFactory;
  }


  /**
   * Sets the LDAP socket factory to use for SSL connections.
   *
   * @param  sf  LDAP socket factory
   */
  public void setLDAPSocketFactory(final LDAPSocketFactory sf)
  {
    checkImmutable();
    logger.trace("setting ldapSocketFactory: {}", sf);
    ldapSocketFactory = sf;
  }


  /**
   * Returns the control processor.
   *
   * @return  control processor
   */
  public ControlProcessor<LDAPControl> getControlProcessor()
  {
    return controlProcessor;
  }


  /**
   * Sets the control processor.
   *
   * @param  processor  control processor
   */
  public void setControlProcessor(final ControlProcessor<LDAPControl> processor)
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
        "connectionStrategy=%s, logCredentials=%s, " +
        "searchIgnoreResultCodes=%s, ldapSocketFactory=%s, " +
        "controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        getLogCredentials(),
        Arrays.toString(searchIgnoreResultCodes),
        ldapSocketFactory,
        controlProcessor);
  }
}
