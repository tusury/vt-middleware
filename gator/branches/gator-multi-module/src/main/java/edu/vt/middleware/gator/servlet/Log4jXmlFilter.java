/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Middleware.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.RequestParamExtractor;

/**
 * Servlet filter operates on responses that presumably contain a log4j XML
 * configuration.  This filter generates certain response headers that are
 * expected by the log4j {@link DOMConfigurator} when acting on URI that
 * is an HTTP URL.  These headers may not be correctly set under normal
 * circumstances by a response generated from a JSP view.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class Log4jXmlFilter implements Filter
{
  protected ConfigManager configManager;


  /**
   * Sets the configuration manager.
   * @param manager Configuration manager;
   */
  public void setConfigManager(final ConfigManager manager)
  {
    this.configManager = manager;
  }


  /** {@inheritDoc} */
  public void init(final FilterConfig config) throws ServletException
  {
    Assert.notNull(configManager, "ConfigManager is required.");
  }


  /** {@inheritDoc} */
  public void doFilter(
    final ServletRequest request,
    final ServletResponse response,
    final FilterChain chain)
    throws IOException, ServletException
  {
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    final ByteArrayResponseWrapper wrappedResponse =
      new ByteArrayResponseWrapper(httpResponse);
    chain.doFilter(request, wrappedResponse);

    final byte[] outBytes = wrappedResponse.toByteArray();
    
    // Try to determine an accurate last modified date for the configuration
    final long timestamp = System.currentTimeMillis();
    long lastModTime = timestamp;
    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(httpRequest));
    if (project != null) {
      lastModTime = project.getModifiedDate().getTimeInMillis();
    }

    // Flush old headers now that we have the response bytes
    httpResponse.reset();
    
    // Set the headers like a static resource
    httpResponse.setContentType("text/xml");
    httpResponse.setContentLength(outBytes.length);
    httpResponse.setDateHeader("Date", timestamp);
    httpResponse.setDateHeader("Last-Modified", lastModTime);
    response.getOutputStream().write(outBytes);
  }


  /** {@inheritDoc} */
  public void destroy() {}
}
