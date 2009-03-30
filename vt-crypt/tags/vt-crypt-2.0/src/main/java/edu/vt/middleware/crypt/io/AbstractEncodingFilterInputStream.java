/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract base class for filter input streams that decode encoded character
 * bytes into raw bytes.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractEncodingFilterInputStream
  extends FilterInputStream
{

  /** Number of encoded bytes to read in one buffer filling round. */
  protected static final int CHUNK_SIZE = 2048;

  /** Buffer containing decoded bytes. */
  protected final DirectByteArrayOutputStream decodeBuffer;

  /** Holds bytes encoded bytes read from input stream. */
  protected final byte[] byteBuffer = new byte[CHUNK_SIZE];

  /** Position in decoded byte buffer. */
  protected int position;


  /**
   * Creates an input filter that decodes characters in the given input stream.
   *
   * @param  in  Input stream to wrap.
   */
  protected AbstractEncodingFilterInputStream(final InputStream in)
  {
    super(in);
    decodeBuffer = new DirectByteArrayOutputStream(getDecodeBufferCapacity());
  }


  /** {@inheritDoc} */
  public int read()
    throws IOException
  {
    if (position == decodeBuffer.size() || decodeBuffer.size() == 0) {
      fillBuffer();
    }
    if (decodeBuffer.size() > 0) {
      return decodeBuffer.getBuffer()[position++];
    } else {
      return -1;
    }
  }


  /** {@inheritDoc} */
  public int read(final byte[] b)
    throws IOException
  {
    return read(b, 0, b.length);
  }


  /** {@inheritDoc} */
  public int read(final byte[] b, final int off, final int len)
    throws IOException
  {
    int count = 0;
    while (count < len) {
      if (position == decodeBuffer.size() || decodeBuffer.size() == 0) {
        fillBuffer();
      }
      if (decodeBuffer.size() == 0) {
        break;
      }
      b[off + count++] = decodeBuffer.getBuffer()[position++];
    }
    return count;
  }


  /**
   * Gets the encoder that decodes encoded character data in the input stream to
   * raw bytes.
   *
   * @return  Encoder instance.
   */
  protected abstract int getDecodeBufferCapacity();


  /**
   * Reads characters from the input reader and decodes them to fill {@link
   * #decodeBuffer}.
   *
   * @throws  IOException  On read errors.
   */
  protected abstract void fillBuffer()
    throws IOException;
}
