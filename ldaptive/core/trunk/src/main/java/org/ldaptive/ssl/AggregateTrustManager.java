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
package org.ldaptive.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trust manager that delegates to multiple trust managers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AggregateTrustManager implements X509TrustManager
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Trust managers to invoke. */
  private final X509TrustManager[] trustManagers;


  /**
   * Creates a new aggregate trust manager.
   *
   * @param  managers  to aggregate
   */
  public AggregateTrustManager(final X509TrustManager... managers)
  {
    trustManagers = managers;
  }


  /** {@inheritDoc} */
  @Override
  public void checkClientTrusted(
    final X509Certificate[] chain,
    final String authType)
    throws CertificateException
  {
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        logger.debug("invoking checkClientTrusted for {}", tm);
        tm.checkClientTrusted(chain, authType);
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public void checkServerTrusted(
    final X509Certificate[] chain,
    final String authType)
    throws CertificateException
  {
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        logger.debug("invoking checkServerTrusted for {}", tm);
        tm.checkServerTrusted(chain, authType);
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public X509Certificate[] getAcceptedIssuers()
  {
    final List<X509Certificate> issuers = new ArrayList<X509Certificate>();
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        logger.debug("invoking getAcceptedIssuers invoked for {}", tm);
        Collections.addAll(issuers, tm.getAcceptedIssuers());
      }
    }
    return issuers.toArray(new X509Certificate[issuers.size()]);
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::trustManagers=%s]",
        getClass().getName(),
        hashCode(),
        Arrays.toString(trustManagers));
  }
}
