/*
  $Id$

  Copyright (C) 2007-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for filter output streams that convert raw bytes into
 * encoded characters.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public abstract class AbstractEncodingFilterOutputStream
  extends FilterOutputStream
{

  /**
   * Creates an output filter that encodes raw bytes into encoded character
   * bytes in the wrapped output stream.
   *
   * @param  out  Output stream to wrap.
   */
  protected AbstractEncodingFilterOutputStream(final OutputStream out)
  {
    super(out);
  }


  /** {@inheritDoc} */
  public void write(final int b)
    throws IOException
  {
    final byte[] buffer = new byte[1];
    buffer[0] = (byte) b;
    writeEncoded(buffer, 0, 1);
  }


  /** {@inheritDoc} */
  public void write(final byte[] b)
    throws IOException
  {
    writeEncoded(b, 0, b.length);
  }


  /** {@inheritDoc} */
  public void write(final byte[] b, final int off, final int len)
    throws IOException
  {
    writeEncoded(b, off, len);
  }


  /**
   * Encode the given raw bytes and write the result to the wrapped output
   * stream.
   *
   * @param  data  Raw bytes to be encoded and written.
   * @param  offset  Starting index of data to be written in input byte array.
   * @param  length  Number of bytes to be written from input byte array.
   *
   * @throws  IOException  On write errors.
   */
  protected abstract void writeEncoded(
    final byte[] data,
    final int offset,
    final int length)
    throws IOException;
}
