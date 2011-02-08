/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
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
import org.bouncycastle.util.encoders.HexEncoder;

/**
 * Encodes raw bytes into hexadecimal characters in the wrapped output stream.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class HexFilterOutputStream extends AbstractEncodingFilterOutputStream
{

  /** Does encoding work. */
  private HexEncoder encoder = new HexEncoder();

  /**
   * Creates a hex filter output stream around the given output stream.
   *
   * @param  out  Output stream to wrap.
   */
  public HexFilterOutputStream(final OutputStream out)
  {
    super(out);
  }


  /** {@inheritDoc} */
  protected void writeEncoded(
    final byte[] data,
    final int offset,
    final int length)
    throws IOException
  {
    encoder.encode(data, offset, length, out);
  }
}
