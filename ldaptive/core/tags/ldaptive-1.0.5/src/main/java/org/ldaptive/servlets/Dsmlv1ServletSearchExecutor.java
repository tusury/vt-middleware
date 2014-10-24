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
package org.ldaptive.servlets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.servlet.http.HttpServletResponse;
import org.ldaptive.SearchResult;
import org.ldaptive.io.Dsmlv1Writer;

/**
 * Writes search results as DSML version 1. See {@link
 * AbstractServletSearchExecutor}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Dsmlv1ServletSearchExecutor extends AbstractServletSearchExecutor
{


  /** {@inheritDoc} */
  @Override
  protected void writeResponse(
    final SearchResult result,
    final HttpServletResponse response)
    throws IOException
  {
    response.setContentType("text/xml");

    final Dsmlv1Writer writer = new Dsmlv1Writer(
      new BufferedWriter(new OutputStreamWriter(response.getOutputStream())));
    writer.write(result);
  }
}
