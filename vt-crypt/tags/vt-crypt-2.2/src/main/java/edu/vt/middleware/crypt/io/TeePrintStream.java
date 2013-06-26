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
package edu.vt.middleware.crypt.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Works like the Unix <code>tee</code> utility by writing to two streams
 * simultaneously, where one is typically STDOUT or STDERR.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TeePrintStream extends PrintStream
{

  /** Other output stream. */
  private final OutputStream other;


  /**
   * Creates a tee stream that writes to both of the given streams
   * simultaneously. To operate like the Unix <code>tee</code>, the second
   * stream should be STDOUT or STDERR.
   *
   * @param  out1  Primary output stream.
   * @param  out2  Secondary output stream, usually <code>System.out</code> or
   * <code>System.err</code>.
   */
  public TeePrintStream(final OutputStream out1, final OutputStream out2)
  {
    super(out1);
    other = out2;
  }


  /** {@inheritDoc} */
  public void write(final int b)
  {
    super.write(b);
    try {
      other.write(b);
    } catch (IOException e) {
      throw new RuntimeException("Error writing to secondary stream.");
    }
  }


  /** {@inheritDoc} */
  public void write(final byte[] buf, final int off, final int len)
  {
    super.write(buf, off, len);
    try {
      other.write(buf, off, len);
    } catch (IOException e) {
      throw new RuntimeException("Error writing to secondary stream.");
    }
  }


  /** {@inheritDoc} */
  public void flush()
  {
    super.flush();
    try {
      other.flush();
    } catch (IOException e) {
      throw new RuntimeException("Error flushing secondary stream.");
    }
  }
}
