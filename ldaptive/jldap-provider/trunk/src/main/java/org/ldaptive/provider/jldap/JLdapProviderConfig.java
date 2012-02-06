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
package org.ldaptive.provider.jldap;

import java.util.Arrays;
import javax.net.ssl.SSLSocketFactory;
import com.novell.ldap.LDAPControl;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderConfig;

/**
 * Contains configuration data for the JLdap provider.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JLdapProviderConfig extends ProviderConfig
{

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** ldap socket factory used for SSL and TLS. */
  private SSLSocketFactory sslSocketFactory;

  /** JLDAP specific control processor. */
  private ControlProcessor<LDAPControl> controlProcessor;


  /** Default constructor. */
  public JLdapProviderConfig()
  {
    setOperationRetryResultCodes(
      new ResultCode[] {ResultCode.LDAP_TIMEOUT, ResultCode.CONNECT_ERROR, });
    searchIgnoreResultCodes = new ResultCode[] {
      ResultCode.TIME_LIMIT_EXCEEDED,
      ResultCode.SIZE_LIMIT_EXCEEDED,
    };
    controlProcessor = new ControlProcessor<LDAPControl>(
      new JLdapControlHandler());
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
   * Returns the SSL socket factory to use for TLS/SSL connections.
   *
   * @return  SSL socket factory
   */
  public SSLSocketFactory getSslSocketFactory()
  {
    return sslSocketFactory;
  }


  /**
   * Sets the SSL socket factory to use for TLS/SSL connections.
   *
   * @param  sf  SSL socket factory
   */
  public void setSslSocketFactory(final SSLSocketFactory sf)
  {
    checkImmutable();
    logger.trace("setting sslSocketFactory: {}", sf);
    sslSocketFactory = sf;
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
        "connectionStrategy=%s, searchIgnoreResultCodes=%s, " +
        "sslSocketFactory=%s, controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        Arrays.toString(searchIgnoreResultCodes),
        sslSocketFactory,
        controlProcessor);
  }
}
