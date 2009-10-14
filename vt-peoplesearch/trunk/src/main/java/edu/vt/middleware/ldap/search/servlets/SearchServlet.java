/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.search.servlets;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.vt.middleware.ldap.search.PeopleSearch;
import edu.vt.middleware.ldap.search.PeopleSearch.OutputFormat;
import edu.vt.middleware.ldap.search.PeopleSearchException;
import edu.vt.middleware.ldap.search.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>SearchServlet</code> queries a LDAP and attempts to find the best fit
 * results based on the query. The results are returned as XML in DSML format.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public final class SearchServlet extends HttpServlet
{

  /** Domain to look for properties in. */
  public static final String PROPERTIES_DOMAIN =
    "edu.vt.middleware.ldap.search.";

  /** Output type. */
  public static final String OUTPUT_TYPE = PROPERTIES_DOMAIN + "outputType";

  /** Default output type. */
  public static final String DEFAULT_OUTPUT_TYPE = "DSML";

  /** Output type. */
  public static final String SPRING_CONTEXT_PATH = PROPERTIES_DOMAIN +
    "springContextPath";

  /** Default output type. */
  public static final String DEFAULT_SPRING_CONTEXT_PATH =
    "/peoplesearch-context.xml";

  /** Name of PeopleSearch bean in Spring context. */
  public static final String PEOPLE_SEARCH_BEAN_NAME = PROPERTIES_DOMAIN +
    "peopleSearchBeanName";

  /** Default output type. */
  public static final String DEFAULT_PEOPLE_SEARCH_BEAN_NAME = "peopleSearch";

  /** serial version uid. */
  private static final long serialVersionUID = -2489565202170951966L;

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(SearchServlet.class);

  /** Types of available output. */
  private enum OutputType {

    /** LDIF output type. */
    LDIF,

    /** DSML output type. */
    DSML
  }

  /** Type of output to produce. */
  private OutputType output;

  /** Search object to use for searching. */
  private PeopleSearch search;


  /**
   * Initialize this servlet.
   *
   * @param  config  <code>ServletConfig</code>
   *
   * @throws  ServletException  if an error occurs
   */
  public void init(final ServletConfig config)
    throws ServletException
  {
    super.init(config);

    // construct the people search object
    String springContextPath = getInitParameter(SPRING_CONTEXT_PATH);
    if (springContextPath == null) {
      springContextPath = DEFAULT_SPRING_CONTEXT_PATH;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug(SPRING_CONTEXT_PATH + " = " + springContextPath);
    }
    String peopleSearchBeanName = getInitParameter(PEOPLE_SEARCH_BEAN_NAME);
    if (peopleSearchBeanName == null) {
      peopleSearchBeanName = DEFAULT_PEOPLE_SEARCH_BEAN_NAME;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug(PEOPLE_SEARCH_BEAN_NAME + " = " + peopleSearchBeanName);
    }
    this.search = PeopleSearch.createFromSpringContext(
        springContextPath, peopleSearchBeanName);

    // determine output type
    String outputType = getInitParameter(OUTPUT_TYPE);
    if (outputType == null) {
      outputType = DEFAULT_OUTPUT_TYPE;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug(OUTPUT_TYPE + " = " + outputType);
    }
    this.output = OutputType.valueOf(outputType);
  }


  /**
   * Handle all requests sent to this servlet. Valid parameters are: query,
   * attrs, content-type, and dsml-version. content-type can be set to 'text',
   * otherwise the default is 'xml'. dsml-version can be set to '2', otherwise
   * the default is 1.
   *
   * @param  request  <code>HttpServletRequest</code>
   * @param  response  <code>HttpServletResponse</code>
   *
   * @throws  ServletException  if an error occurs
   * @throws  IOException  if an error occurs
   */
  public void service(
    final HttpServletRequest request,
    final HttpServletResponse response)
    throws ServletException, IOException
  {
    final X509Certificate[] certChain = (X509Certificate[])
      request.getAttribute("javax.servlet.request.X509Certificate");
    String certSubject = null;
    if (certChain != null && certChain[0] != null) {
      certSubject = "dn:" + certChain[0].getSubjectX500Principal().getName();
    }
    if (LOG.isDebugEnabled()) {
      if (certSubject != null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "Received the following client certificate: " + certSubject);
        }
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Did not receive a client certificate");
        }
      }
    }

    // determine content
    OutputFormat format = null;
    if (this.output == OutputType.LDIF) {
      response.setContentType("text/plain");
      format = OutputFormat.LDIF;
    } else {
      final String dsmlVersion = request.getParameter("dsml-version");
      if (dsmlVersion != null && dsmlVersion.equals("2")) {
        format = OutputFormat.DSMLV2;
      } else {
        format = OutputFormat.DSMLV1;
      }

      final String content = request.getParameter("content-type");
      if (content != null && content.equalsIgnoreCase("text")) {
        response.setContentType("text/plain");
      } else {
        response.setContentType("text/xml");
      }
    }

    Integer fromResult = null;
    if (request.getParameter("from-result") != null) {
      try {
        fromResult = Integer.valueOf(request.getParameter("from-result"));
      } catch (NumberFormatException e) {
        if (LOG.isWarnEnabled()) {
          LOG.warn(
            "Received invalid fromResult parameter: " +
            request.getParameter("from-result"));
        }
      }
    }

    Integer toResult = null;
    if (request.getParameter("to-result") != null) {
      try {
        toResult = Integer.valueOf(request.getParameter("to-result"));
      } catch (NumberFormatException e) {
        if (LOG.isWarnEnabled()) {
          LOG.warn(
            "Received invalid toResult parameter: " +
            request.getParameter("to-result"));
        }
      }
    }
    try {
      final Query query = new Query();
      query.setRawQuery(request.getParameter("query"));
      query.setQueryAttributes(request.getParameterValues("attrs"));
      query.setSearchRestrictions(request.getParameter("search-restrictions"));
      query.setFromResult(fromResult);
      query.setToResult(toResult);
      if (certSubject != null) {
        query.setSaslAuthorizationId(certSubject);
      }
      if (LOG.isInfoEnabled()) {
        LOG.info("Performing search with: " + query);
      }
      this.search.search(
        query,
        format,
        new BufferedWriter(new OutputStreamWriter(response.getOutputStream())));
    } catch (PeopleSearchException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error performing search", e);
      }
      throw new ServletException(e);
    }
  }


  /**
   * Called by the servlet container to indicate to a servlet that the servlet
   * is being taken out of service.
   */
  public void destroy()
  {
    try {
      this.search.getLdapPoolManager().close();
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error closing ldap connections", e);
      }
    } finally {
      super.destroy();
    }
  }
}
