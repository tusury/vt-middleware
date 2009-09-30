/*
  $Id: Base64FilterOutputStream.java 3 2008-11-11 20:58:48Z dfisher $

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
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Base64Encoder;

/**
 * Encodes raw bytes into base-64 encoded character bytes in the wrapped output
 * stream.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class Base64FilterOutputStream extends AbstractEncodingFilterOutputStream
{

  /** Wrap lines at 64 characters. */
  public static final int LINE_LENGTH_64 = 64;

  /** Wrap lines at 76 characters. */
  public static final int LINE_LENGTH_76 = 76;

  /** Line termination character. */
  public static final char LINE_TERMINATOR = '\n';

  /** Number of bytes encoded into a 24-bit base-64 encoded group. */
  private static final int BYTES_PER_GROUP = 3;

  /** Number of characters encoded into a 24-bit base-64 encoded group. */
  private static final int CHARS_PER_GROUP = 4;

  /**
   * Write out buffer size must be multiple of 3 to accommodate 24-bit groups of
   * base 64 encoding.
   */
  private static final int BUFFER_SIZE = 1536;

  /** Buffers raw bytes written out to facilite writing on 24-bit boundaries. */
  private final DirectByteArrayOutputStream writeBuffer =
    new DirectByteArrayOutputStream(BUFFER_SIZE);

  /** Does work of base-64 encoding. */
  private final Base64Encoder encoder = new Base64Encoder();

  /** Helps implement line wrapping in encoded output. */
  private final DirectByteArrayOutputStream lineBuffer;

  /** Length of lines of encoded input. 0 indicates no line wrapping. */
  private int lineLength;

  /**
   * 0-based column in which last flush operation ended for line-wrapped output.
   */
  private int position;


  /**
   * Creates a base-64 filter output stream around the given output stream.
   *
   * @param  out  Output stream to wrap.
   */
  public Base64FilterOutputStream(final OutputStream out)
  {
    this(out, 0);
  }


  /**
   * /** Creates a base-64 filter output stream around the given output stream.
   *
   * @param  out  Output stream to wrap.
   * @param  charsPerLine  Number of characters per line of encoded output. Must
   * be one of {@link #LINE_LENGTH_64}, {@link #LINE_LENGTH_76}, or 0 to
   * indicate no wrapping.
   */
  public Base64FilterOutputStream(
    final OutputStream out,
    final int charsPerLine)
  {
    super(out);
    if (charsPerLine == 0) {
      lineBuffer = null;
    } else if (
      charsPerLine == LINE_LENGTH_64 ||
        charsPerLine == LINE_LENGTH_76) {
      lineBuffer = new DirectByteArrayOutputStream(
        BUFFER_SIZE * CHARS_PER_GROUP / BYTES_PER_GROUP);
    } else {
      throw new IllegalArgumentException("Invalid characters per line.");
    }
    lineLength = charsPerLine;
  }


  /** {@inheritDoc} */
  public void close()
    throws IOException
  {
    flushToStream();
    if (lineLength > 0) {
      out.write(LINE_TERMINATOR);
    }
    super.close();
  }


  /** {@inheritDoc} */
  protected void writeEncoded(
    final byte[] data,
    final int offset,
    final int length)
    throws IOException
  {
    int remaining = BUFFER_SIZE - writeBuffer.size();
    if (length >= remaining) {
      int count = 0;
      do {
        writeBuffer.write(data, offset + count, remaining);
        count += remaining;
        flushToStream();
        remaining = BUFFER_SIZE - writeBuffer.size();
      } while (count + remaining < length);
      writeBuffer.write(data, offset + count, length - count);
    } else {
      writeBuffer.write(data, offset, length);
    }
  }


  /**
   * Encodes the contents of the write buffer and writes the result to the
   * wrapped output stream.
   *
   * @throws  IOException  On write errors.
   */
  protected void flushToStream()
    throws IOException
  {
    if (lineLength == 0) {
      encoder.encode(writeBuffer.getBuffer(), 0, writeBuffer.size(), out);
    } else {
      lineBuffer.reset();
      encoder.encode(
        writeBuffer.getBuffer(),
        0,
        writeBuffer.size(),
        lineBuffer);

      int count = 0;
      if (position > 0) {
        count = lineLength - position;
        out.write(lineBuffer.getBuffer(), 0, count);
        out.write(LINE_TERMINATOR);
      }
      while (count + lineLength < lineBuffer.size()) {
        out.write(lineBuffer.getBuffer(), count, lineLength);
        out.write(LINE_TERMINATOR);
        count += lineLength;
      }
      position = lineBuffer.size() - count;
      if (position > 0) {
        out.write(lineBuffer.getBuffer(), count, position);
      }
    }
    writeBuffer.reset();
  }
}
