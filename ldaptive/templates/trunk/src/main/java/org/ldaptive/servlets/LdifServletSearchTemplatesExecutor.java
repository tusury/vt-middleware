/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
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
import org.ldaptive.io.LdifWriter;

/**
 * Writes search results in LDIF format. See {@link
 * AbstractServletSearchTemplatesExecutor}.
 *
 * @author  Middleware Services
 * @version  $Revision $ $Date$
 */
public class LdifServletSearchTemplatesExecutor
  extends AbstractServletSearchTemplatesExecutor
{


  /** {@inheritDoc} */
  @Override
  protected void writeResponse(
    final SearchResult result,
    final HttpServletResponse response)
    throws IOException
  {
    response.setContentType("text/plain");

    final LdifWriter writer = new LdifWriter(
      new BufferedWriter(new OutputStreamWriter(response.getOutputStream())));
    writer.write(result);
  }
}
