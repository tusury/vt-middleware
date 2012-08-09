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
import java.util.UUID;

/**
 * Converts UUIDs to and from their DER encoded format. See RFC 4122.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UuidType extends AbstractDERType implements DEREncoder
{

  /** Number of bytes in a uuid. */
  private static final int UUID_LENGTH = 16;

  /** UUID to encode. */
  private final byte[] derItem;


  /**
   * Creates a new uuid type.
   *
   * @param  item  to DER encode
   */
  public UuidType(final UUID item)
  {
    derItem = toBytes(item);
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    return encode(UniversalDERTag.OCTSTR.getTagNo(), derItem);
  }


  /**
   * Converts bytes in the buffer to a uuid by reading from the current position
   * to the limit.
   *
   * @param  encoded  buffer containing DER-encoded data where the buffer is
   * positioned at the start of uuid bytes and the limit is set beyond the last
   * byte of uuid data.
   *
   * @return  decoded bytes as a uuid.
   */
  public static UUID decode(final ByteBuffer encoded)
  {
    final ByteBuffer buffer = ByteBuffer.wrap(readBuffer(encoded));
    final long mostSig = buffer.getLong();
    final long leastSig = buffer.getLong();
    return new UUID(mostSig, leastSig);
  }


  /**
   * Converts the supplied uuid to a byte array.
   *
   * @param  uuid  to convert
   *
   * @return  byte array
   */
  public static byte[] toBytes(final UUID uuid)
  {
    final ByteBuffer buffer = ByteBuffer.wrap(new byte[UUID_LENGTH]);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());
    return buffer.array();
  }
}
