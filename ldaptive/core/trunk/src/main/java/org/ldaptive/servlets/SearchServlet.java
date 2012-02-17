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
package org.ldaptive.servlets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ldaptive.LdapResult;
import org.ldaptive.io.Dsmlv1Writer;
import org.ldaptive.io.LdapResultWriter;
import org.ldaptive.io.LdifWriter;

/**
 * Queries an LDAP and returns the result as LDIF or DSML. The following init
 * params can be set for this servlet:
 *
 * <ul>
 *   <li>poolType</li>
 *   <li>outputFormat</li>
 * </ul>
 *
 * All other init params can be set from properties on:
 * <ul>
 *   <li>{@link SearchRequest}</li>
 *   <li>{@link ConnectionConfig}</li>
 *   <li>{@link PoolConfig}</li>
 * </ul>
 *
 * <p>Example: http://www.server.com/Search?query=uid=dfisher If you need to
 * pass complex queries, such as (&(cn=daniel*)(surname=fisher)), then the query
 * must be form encoded. If you only want to receive a subset of attributes
 * those can be specified. Example:
 * http://www.server.com/Search?query=uid=dfisher&attrs=givenname&attrs=surname
 * </p>
 *
 * <h3>LDIF</h3>
 *
 * <p>The content returned by the servlet is of type text/plain.</p>
 * <hr/>
 * <h3>DSML</h3>
 *
 * <p>The content returned by the servlet is of type text/xml, if you want to
 * receive the content as text/plain that can be specified as well. Example:
 * http://www.server.com/Search?query=uid=dfisher&content-type=text</p>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class SearchServlet extends AbstractServlet
{

  /** Format of search output, value is {@value}. */
  public static final String OUTPUT_FORMAT = "outputFormat";

  /** Default format of search output, value is {@value}. */
  public static final String DEFAULT_OUTPUT_FORMAT = "DSML";

  /** serial version uid. */
  private static final long serialVersionUID = 3437252581014900696L;

  /** Types of available output. */
  private enum OutputType {

    /** LDIF output type. */
    LDIF,

    /** DSML output type. */
    DSML
  }

  /** Type of output to produce. */
  private OutputType output;


  /**
   * Initialize this servlet.
   *
   * @param  config  servlet config
   *
   * @throws  ServletException  if an error occurs
   */
  public void init(final ServletConfig config)
    throws ServletException
  {
    super.init(config);

    String outputType = getInitParameter(OUTPUT_FORMAT);
    if (outputType == null) {
      outputType = DEFAULT_OUTPUT_FORMAT;
    }
    logger.debug("{} = {}", OUTPUT_FORMAT, outputType);
    output = OutputType.valueOf(outputType);
  }


  /**
   * Handle all requests sent to this servlet.
   *
   * @param  request  http servlet reqeust
   * @param  response  http servlet response
   *
   * @throws  ServletException  if an error occurs
   * @throws  IOException  if an error occurs
   */
  public void service(
    final HttpServletRequest request,
    final HttpServletResponse response)
    throws ServletException, IOException
  {
    logger.info(
      "search={} for attributes={}",
      request.getParameter("query"),
      request.getParameter("attrs"));
    try {
      final LdapResult result = search(
        request.getParameter("query"),
        request.getParameterValues("attrs"));
      LdapResultWriter writer = null;
      if (output == OutputType.LDIF) {
        response.setContentType("text/plain");
        writer = new LdifWriter(
          new BufferedWriter(
            new OutputStreamWriter(response.getOutputStream())));
      } else {
        final String content = request.getParameter("content-type");
        if (content != null && "text".equalsIgnoreCase(content)) {
          response.setContentType("text/plain");
        } else {
          response.setContentType("text/xml");
        }

        writer = new Dsmlv1Writer(
          new BufferedWriter(
            new OutputStreamWriter(response.getOutputStream())));
      }
      writer.write(result);
    } catch (Exception e) {
      logger.error("Error performing search", e);
      throw new ServletException(e);
    }
  }
}
