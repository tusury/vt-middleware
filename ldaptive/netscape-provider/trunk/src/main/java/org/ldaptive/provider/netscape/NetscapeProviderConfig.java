/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.provider.netscape;

import java.util.Arrays;
import netscape.ldap.LDAPConstraints;
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
public class NetscapeProviderConfig extends ProviderConfig<LDAPControl>
{

  /** Connection constraints. */
  private LDAPConstraints ldapConstraints;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** Socket factory used for SSL. */
  private LDAPSocketFactory ldapSocketFactory;


  /** Default constructor. */
  public NetscapeProviderConfig()
  {
    setOperationExceptionResultCodes(ResultCode.CONNECT_ERROR);
    setControlProcessor(
      new ControlProcessor<LDAPControl>(new NetscapeControlHandler()));
    searchIgnoreResultCodes = new ResultCode[] {
      ResultCode.TIME_LIMIT_EXCEEDED,
      ResultCode.SIZE_LIMIT_EXCEEDED,
    };
  }


  /**
   * Returns the connection constraints.
   *
   * @return  ldap connection constraints
   */
  public LDAPConstraints getLDAPConstraints()
  {
    return ldapConstraints;
  }


  /**
   * Sets the connection constraints.
   *
   * @param  constraints  ldap connection constraints
   */
  public void setLDAPConstraints(final LDAPConstraints constraints)
  {
    checkImmutable();
    logger.trace("setting ldapConstraints: {}", constraints);
    ldapConstraints = constraints;
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


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::operationExceptionResultCodes=%s, properties=%s, " +
        "connectionStrategy=%s, controlProcessor=%s, ldapConstraints=%s, " +
        "searchIgnoreResultCodes=%s, ldapSocketFactory=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationExceptionResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        getControlProcessor(),
        ldapConstraints,
        Arrays.toString(searchIgnoreResultCodes),
        ldapSocketFactory);
  }
}
