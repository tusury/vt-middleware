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

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapResult;

/**
 * Queries an LDAP and returns the value of a single attribute. Example:
 * http://www.server.com/Attribute?query=uid=dfisher&attr=givenName If you need
 * to pass complex queries, such as (&(cn=daniel*)(surname=fisher)), then the
 * query must be form encoded. The content returned by the servlet is of type
 * text/plain, if you want to receive the content as application/octet-stream
 * that can be specified by passing the content-type=octet param. The following
 * init params can be set for this servlet:
 *
 * <ul>
 *   <li>poolType</li>
 * </ul>
 *
 * All other init params can be set from properties on:
 * <ul>
 *   <li>{@link org.ldaptive.SearchRequest}</li>
 *   <li>{@link org.ldaptive.ConnectionConfig}</li>
 *   <li>{@link org.ldaptive.pool.PoolConfig}</li>
 * </ul>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AttributeServlet extends AbstractServlet
{

  /** serial version uid. */
  private static final long serialVersionUID = 3437252581014900696L;


  /** {@inheritDoc} */
  @Override
  public void service(
    final HttpServletRequest request,
    final HttpServletResponse response)
    throws ServletException, IOException
  {
    final String attribute = request.getParameter("attr");
    byte[] value = null;
    final String content = request.getParameter("content-type");

    if ("octet".equalsIgnoreCase(content)) {
      response.setContentType("application/octet-stream");
      response.setHeader(
        "Content-Disposition",
        "attachment; filename=\"" + attribute + ".bin\"");
    } else {
      response.setContentType("text/plain");
    }

    try {
      final LdapResult result = search(
        request.getParameter("query"),
        request.getParameterValues("attrs"));
      final LdapEntry e = result.getEntry();
      if (e != null && e.size() > 0) {
        final LdapAttribute a = e.getAttribute(attribute);
        if (a != null && a.size() > 0) {
          value = a.getBinaryValue();
        }
      }

      if (value != null) {
        final OutputStream out = response.getOutputStream();
        out.write(value);
        out.flush();
        out.close();
      }

    } catch (Exception e) {
      logger.error("Error performing search", e);
      throw new ServletException(e.getMessage());
    }
  }
}
