/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.servlet.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <code>SetCertTestFilter</code> adds a certificate to the request attributes
 * to facilitate certificate filter testing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SetCertTestFilter implements Filter
{

  /** certificates to add as a request attribute. */
  private X509Certificate[] certs;


  /**
   * Initialize this filter.
   *
   * @param  config  <code>FilterConfig</code>
   */
  public void init(final FilterConfig config)
  {
    try {
      final CertificateFactory cf = CertificateFactory.getInstance("X.509");
      final X509Certificate c = (X509Certificate) cf.generateCertificate(
        new FileInputStream(config.getInitParameter("cert")));
      certs = new X509Certificate[] {c};
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Handle all requests sent to this filter.
   *
   * @param  request  <code>ServletRequest</code>
   * @param  response  <code>ServletResponse</code>
   * @param  chain  <code>FilterChain</code>
   *
   * @throws  ServletException  if this request cannot be serviced
   * @throws  IOException  if a response cannot be sent
   */
  public void doFilter(
    final ServletRequest request,
    final ServletResponse response,
    final FilterChain chain)
    throws ServletException, IOException
  {
    request.setAttribute("javax.servlet.request.X509Certificate", this.certs);
    chain.doFilter(request, response);
  }


  /**
   * Called by the web container to indicate to a filter that it is being taken
   * out of service.
   */
  public void destroy() {}
}
