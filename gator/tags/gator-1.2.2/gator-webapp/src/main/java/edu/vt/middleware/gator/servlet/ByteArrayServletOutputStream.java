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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 * A memory-backed {@link ServletOutputStream}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ByteArrayServletOutputStream extends ServletOutputStream
{

  /** Memory-backed stream backing this instance. */
  private ByteArrayOutputStream baos;


  /** Creates a new instance with the default backing stream. */
  public ByteArrayServletOutputStream()
  {
    this(new ByteArrayOutputStream());
  }


  /**
   * Creates a new instance with the given backing stream.
   *
   * @param  os  Backing stream.
   */
  public ByteArrayServletOutputStream(final ByteArrayOutputStream os)
  {
    this.baos = os;
  }


  /** {@inheritDoc}. */
  @Override
  public void write(int b)
    throws IOException
  {
    baos.write(b);
  }
}
