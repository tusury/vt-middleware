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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * HTTP servlet response wrapper that uses memory-backed streams.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ByteArrayResponseWrapper extends HttpServletResponseWrapper
{
  /** Memory backing store for both writer and output stream */
  final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  
  /** Memory-backed print writer */
  private ByteArrayPrintWriter writer;
 
  /** Memory-backed output stream */
  private ByteArrayServletOutputStream outStream;


  /**
   * Creates a memory-backed response wrapper around the given response.
   * @param response HTTP servlet response to wrap.
   */
  public ByteArrayResponseWrapper(final HttpServletResponse response)
  {
    super(response);
    writer = new ByteArrayPrintWriter(baos);
    outStream = new ByteArrayServletOutputStream(baos);
  }


  /** {@inheritDoc} */
  @Override
  public ServletOutputStream getOutputStream() throws IOException
  {
    return outStream;
  }


  /** {@inheritDoc} */
  @Override
  public PrintWriter getWriter() throws IOException
  {
    return writer;
  }

  
  /**
   * Creates a new byte array containing the data in the memory-backed stream.
   * @return Byte array containing copy of stream data.
   */
  public byte[] toByteArray()
  {
    return baos.toByteArray();
  }
}
