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
package org.ldaptive.provider.opends;

import java.util.Arrays;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderConfig;
import org.opends.sdk.LDAPOptions;
import org.opends.sdk.controls.Control;

/**
 * Contains configuration data for the OpenDS provider.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDSProviderConfig extends ProviderConfig
{

  /** Connection options. */
  private LDAPOptions options;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** OpenDS specific control processor. */
  private ControlProcessor<Control> controlProcessor;


  /** Default constructor. */
  public OpenDSProviderConfig()
  {
    setOperationRetryResultCodes(
      new ResultCode[] {ResultCode.LDAP_TIMEOUT, ResultCode.CONNECT_ERROR, });
    searchIgnoreResultCodes = new ResultCode[] {
      ResultCode.TIME_LIMIT_EXCEEDED,
      ResultCode.SIZE_LIMIT_EXCEEDED,
    };
    controlProcessor = new ControlProcessor<Control>(
      new OpenDSControlHandler());
  }


  /**
   * Returns the connection options.
   *
   * @return  ldap options
   */
  public LDAPOptions getOptions()
  {
    return options;
  }


  /**
   * Sets the connection options.
   *
   * @param  o  ldap options
   */
  public void setOptions(final LDAPOptions o)
  {
    options = o;
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


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::operationRetryResultCodes=%s, properties=%s, " +
        "connectionStrategy=%s, options=%s, searchIgnoreResultCodes=%s, " +
        "controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        options,
        Arrays.toString(searchIgnoreResultCodes),
        controlProcessor);
  }
}
