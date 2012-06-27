/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.asn1;

import java.nio.ByteBuffer;

/**
 * Provides functionality common to DER types implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractDERType
{

  /** Length of short form integers. */
  private static final int SHORT_FORM_INT_LENGTH = 127;


  /**
   * DER encode the supplied items with the supplied tag. If the length is
   * greater than 127 bytes the long form is always expressed using 4 bytes.
   *
   * @param  tag  for this DER type
   * @param  items  to encode
   *
   * @return  DER encoded items
   */
  protected static byte[] encode(final int tag, final byte[]... items)
  {
    int itemLength = 0;
    for (byte[] b : items) {
      itemLength += b.length;
    }

    byte[] lengthBytes;
    if (itemLength <= SHORT_FORM_INT_LENGTH) {
      lengthBytes = new byte[] {(byte) itemLength};
    } else {
      // use 4 bytes for all long form integers
      // CheckStyle:MagicNumber OFF
      lengthBytes = new byte[] {
        (byte) 0x84,
        (byte) (itemLength >>> 24),
        (byte) (itemLength >>> 16),
        (byte) (itemLength >>> 8),
        (byte) itemLength,
      };
      // CheckStyle:MagicNumber ON
    }

    // add 1 for the type tag, 1 or 5 for the length
    final ByteBuffer encodedItem = ByteBuffer.allocate(
      itemLength + 1 + lengthBytes.length);
    encodedItem.put((byte) tag);
    for (byte b : lengthBytes) {
      encodedItem.put(b);
    }
    for (byte[] b : items) {
      encodedItem.put(b);
    }
    return encodedItem.array();
  }


  /**
   * Returns a byte array containing the bytes from {@link ByteBuffer#limit()}
   * to {@link ByteBuffer#position()}.
   *
   * @param  encoded  to read bytes from
   *
   * @return  bytes
   */
  public static byte[] readBuffer(final ByteBuffer encoded)
  {
    final byte[] bytes = new byte[encoded.limit() - encoded.position()];
    encoded.get(bytes);
    return bytes;
  }
}
