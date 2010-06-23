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

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.encoders.Base64Encoder;

/**
 * Converts an input stream of base-64 encoded character bytes into raw bytes.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class Base64FilterInputStream extends AbstractEncodingFilterInputStream
{

  /** Wrap lines at 64 characters. */
  public static final int LINE_LENGTH_64 = 64;

  /** Wrap lines at 76 characters. */
  public static final int LINE_LENGTH_76 = 76;

  /** Number of bytes encoded into a 24-bit base-64 encoded group. */
  private static final int BYTES_PER_GROUP = 3;

  /** Number of characters encoded into a 24-bit base-64 encoded group. */
  private static final int CHARS_PER_GROUP = 4;

  /** Does work of base-64 encoding. */
  private final Base64Encoder encoder = new Base64Encoder();

  /** Length of lines of encoded input. 0 indicates no line wrapping. */
  private int lineLength;


  /**
   * Creates a base-64 filter input stream around the given input stream.
   *
   * @param  in  Input stream to wrap.
   */
  public Base64FilterInputStream(final InputStream in)
  {
    this(in, 0);
  }


  /**
   * Creates a base-64 filter input stream around the given input stream.
   *
   * @param  in  Input stream to wrap.
   * @param  charsPerLine  Number of characters per line of encoded input. Must
   * be one of {@link #LINE_LENGTH_64}, {@link #LINE_LENGTH_76}, or 0 to
   * indicate no wrapping.
   */
  public Base64FilterInputStream(final InputStream in, final int charsPerLine)
  {
    super(in);
    if (
      charsPerLine != 0 &&
        charsPerLine != LINE_LENGTH_64 &&
        charsPerLine != LINE_LENGTH_76) {
      throw new IllegalArgumentException("Invalid characters per line.");
    }
    lineLength = charsPerLine;
  }


  /** {@inheritDoc} */
  protected int getDecodeBufferCapacity()
  {
    return CHUNK_SIZE * BYTES_PER_GROUP / CHARS_PER_GROUP;
  }


  /** {@inheritDoc} */
  protected void fillBuffer()
    throws IOException
  {
    position = 0;
    decodeBuffer.reset();

    int count = 0;
    if (lineLength == 0) {
      count = in.read(byteBuffer);
    } else {
      int n = 0;
      while (count + lineLength < CHUNK_SIZE) {
        n = in.read(byteBuffer, count, lineLength);
        if (n < 0) {
          break;
        }
        count += n;
        if (count % lineLength == 0) {
          // Skip line terminator character
          in.skip(1);
        }
      }
    }
    if (count > 0) {
      encoder.decode(byteBuffer, 0, count, decodeBuffer);
    }
  }
}
