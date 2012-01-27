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
package org.ldaptive.provider.jndi;

import java.io.OutputStream;
import java.util.Arrays;
import javax.naming.ldap.Control;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderConfig;

/**
 * Contains configuration data for the JNDI provider.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiProviderConfig extends ProviderConfig
{

  /** Stream to print LDAP ASN.1 BER packets. */
  private OutputStream tracePackets;

  /** Whether to remove the URL from any DNs which are not relative. */
  private boolean removeDnUrls = true;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** ldap socket factory used for SSL and startTLS. */
  private SSLSocketFactory sslSocketFactory;

  /** hostname verifier for startTLS connections. */
  private HostnameVerifier hostnameVerifier;

  /** JNDI specific control handler. */
  private ControlProcessor<Control> controlProcessor;


  /** Default constructor. */
  public JndiProviderConfig()
  {
    setOperationRetryResultCodes(
      new ResultCode[] {
        ResultCode.PROTOCOL_ERROR,
        ResultCode.BUSY,
        ResultCode.UNAVAILABLE,
      });
    searchIgnoreResultCodes = new ResultCode[] {
      ResultCode.TIME_LIMIT_EXCEEDED,
      ResultCode.SIZE_LIMIT_EXCEEDED,
    };
    controlProcessor = new ControlProcessor<Control>(new JndiControlHandler());
  }


  /**
   * Returns the output stream used to print ASN.1 BER packets.
   *
   * @return  output stream
   */
  public OutputStream getTracePackets()
  {
    return tracePackets;
  }


  /**
   * Sets the output stream to print ASN.1 BER packets to.
   *
   * @param  stream  to output to
   */
  public void setTracePackets(final OutputStream stream)
  {
    checkImmutable();
    logger.trace("setting tracePackets: {}", stream);
    tracePackets = stream;
  }


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative. The default value is true.
   *
   * @return  whether the URL will be removed from DNs
   */
  public boolean getRemoveDnUrls()
  {
    return removeDnUrls;
  }


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   * The default value is true.
   *
   * @param  b  whether the URL will be removed from DNs
   */
  public void setRemoveDnUrls(final boolean b)
  {
    checkImmutable();
    logger.trace("setting removeDnUrls: {}", b);
    removeDnUrls = b;
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
   * Returns the SSL socket factory to use for SSL and startTLS connections.
   *
   * @return  SSL socket factory
   */
  public SSLSocketFactory getSslSocketFactory()
  {
    return sslSocketFactory;
  }


  /**
   * Sets the SSL socket factory to use for SSL and startTLS connections.
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
   * Returns the hostname verifier to use for startTLS connections.
   *
   * @return  hostname verifier
   */
  public HostnameVerifier getHostnameVerifier()
  {
    return hostnameVerifier;
  }


  /**
   * Sets the hostname verifier to use for startTLS connections.
   *
   * @param  verifier  for hostnames
   */
  public void setHostnameVerifier(final HostnameVerifier verifier)
  {
    checkImmutable();
    logger.trace("setting hostnameVerifier: {}", verifier);
    hostnameVerifier = verifier;
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
        "connectionStrategy=%s, logCredentials=%s, tracePackets=%s, " +
        "removeDnUrls=%s, searchIgnoreResultCodes=%s, sslSocketFactory=%s, " +
        "hostnameVerifier=%s, controlProcessor=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(getOperationRetryResultCodes()),
        getProperties(),
        getConnectionStrategy(),
        getLogCredentials(),
        tracePackets,
        removeDnUrls,
        Arrays.toString(searchIgnoreResultCodes),
        sslSocketFactory,
        hostnameVerifier,
        controlProcessor);
  }
}
