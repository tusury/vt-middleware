/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
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

import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.web.support.RequestParamExtractor;

import org.springframework.util.Assert;

/**
 * Servlet filter that sets response headers for XML-based views of project
 * configuration, e.g. log4j.xml.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ConfigurationFilter implements Filter
{
  protected ConfigManager configManager;


  /**
   * Sets the configuration manager.
   *
   * @param  manager  Configuration manager;
   */
  public void setConfigManager(final ConfigManager manager)
  {
    this.configManager = manager;
  }


  /** {@inheritDoc}. */
  public void init(final FilterConfig config)
    throws ServletException
  {
    Assert.notNull(configManager, "ConfigManager is required.");
  }


  /** {@inheritDoc}. */
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


  /** {@inheritDoc}. */
  public void destroy() {}
}
