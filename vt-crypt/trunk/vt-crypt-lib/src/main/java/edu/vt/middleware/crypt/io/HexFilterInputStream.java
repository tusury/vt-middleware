/*
  $Id: HexFilterInputStream.java 3 2008-11-11 20:58:48Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3 $
  Updated: $Date: 2008-11-11 15:58:48 -0500 (Tue, 11 Nov 2008) $
*/
package edu.vt.middleware.crypt.io;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.encoders.HexEncoder;

/**
 * Decodes hexadecimal character bytes in the wrapped input stream into raw
 * bytes.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class HexFilterInputStream extends AbstractEncodingFilterInputStream
{

  /** Does decoding work. */
  private HexEncoder encoder = new HexEncoder();


  /**
   * Creates a hex filter input stream around the given input stream.
   *
   * @param  in  Input stream to wrap.
   */
  public HexFilterInputStream(final InputStream in)
  {
    super(in);
  }


  /** {@inheritDoc} */
  protected int getDecodeBufferCapacity()
  {
    return CHUNK_SIZE / 2;
  }


  /** {@inheritDoc} */
  protected void fillBuffer()
    throws IOException
  {
    position = 0;
    decodeBuffer.reset();

    final int count = in.read(byteBuffer);
    if (count > 0) {
      encoder.decode(byteBuffer, 0, count, decodeBuffer);
    }
  }
}
