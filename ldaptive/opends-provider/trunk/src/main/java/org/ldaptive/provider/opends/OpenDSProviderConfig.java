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
public class OpenDSProviderConfig extends ProviderConfig<Control>
{

  /** Connection options. */
  private LDAPOptions options;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;


  /** Default constructor. */
  public OpenDSProviderConfig()
  {
    setOperationExceptionResultCodes(ResultCode.SERVER_DOWN);
    setControlProcessor(
      new ControlProcessor<Control>(new OpenDSControlHandler()));
    searchIgnoreResultCodes = new ResultCode[] {
      ResultCode.TIME_LIMIT_EXCEEDED,
      ResultCode.SIZE_LIMIT_EXCEEDED,
    };
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


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::operationExceptionResultCodes=%s, properties=%s, " +
        "connectionStrategy=%s, controlProcessor=%s, options=%s, " +
        "searchIgnoreResultCodes=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationExceptionResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        getControlProcessor(),
        options,
        Arrays.toString(searchIgnoreResultCodes));
  }
}
