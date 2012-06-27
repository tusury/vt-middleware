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
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a SAX-like parsing facility for DER-encoded data where
 * elements of interest in the parse tree may be registered to handlers via the
 * {@link #registerHandler} methods.  {@link DERPath} strings are used to map
 * handlers to elements of interest.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 * @see  DERPath
 */
public class DERParser
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Handlers for DER paths. */
  private final Map<DERPath, ParseHandler> handlerMap =
    new HashMap<DERPath, ParseHandler>();

  /** Current path location. */
  private DERPath currentPath;


  /**
   * See {@link #registerHandler(DERPath, ParseHandler)}.
   *
   * @param  path  to register
   * @param  handler  to associate with the path
   */
  public void registerHandler(final String path, final ParseHandler handler)
  {
    registerHandler(new DERPath(path), handler);
  }


  /**
   * Registers the supplied handler to fire when the supplied path is
   * encountered.
   *
   * @param  path  to register
   * @param  handler  to associate with the path
   */
  public void registerHandler(final DERPath path, final ParseHandler handler)
  {
    handlerMap.put(path, handler);
  }


  /**
   * Parse a DER-encoded data structure by calling registered handlers when
   * points of interest are encountered in the parse tree.
   *
   * @param  encoded  DER-encoded bytes.
   */
  public void parse(final ByteBuffer encoded)
  {
    currentPath = new DERPath();
    parseTags(encoded);
  }


  /**
   * Gets the current state of the parser in terms of the path that describes
   * the position in the parse tree currently visited by the parser.
   *
   * @return  current path in parse tree. Changes to the returned object do not
   * affect parser position.
   */
  public DERPath getCurrentPath()
  {
    return new DERPath(currentPath);
  }


  /**
   * Reads a DER tag from a single byte at the current position of the given
   * buffer. The buffer position is naturally advanced one byte in this
   * operation.
   *
   * @param  encoded  Buffer containing DER-encoded bytes positioned at tag.
   *
   * @return  Tag or null if no universal tag or application-specific tag is
   * known that matches the byte read in.
   */
  public DERTag readTag(final ByteBuffer encoded)
  {
    if (encoded.position() >= encoded.limit()) {
      return null;
    }

    DERTag tag;
    final byte b = encoded.get();
    // CheckStyle:MagicNumber OFF
    final int tagNo = b & 0x1F;
    // Read class from first two high-order bits
    switch ((b & 0xC0) >> 6) {
    case 0:
      // Universal tag (class 00b)
      tag = UniversalDERTag.fromTagNo(tagNo);
      break;
    case 1:
      // Application tag (class 01b)
      tag = new ApplicationDERTag(tagNo, false);
      break;
    case 2:
      // Context-specific tag (class 10b)
      tag = new ContextDERTag(tagNo, false);
      break;
    default:
      // Private class (class 11b)
      throw new IllegalArgumentException("Private classes not supported.");
    }
    // CheckStyle:MagicNumber ON
    return tag;
  }


  /**
   * Reads the length of a DER-encoded value from the given byte buffer. The
   * buffer is expected to be positioned at the byte immediately following the
   * tag byte, which is where the length byte(s) begin(s). Invocation of this
   * method has two generally beneficial side effects:
   *
   * <ol>
   *   <li>Buffer is positioned at <em>start</em> of value bytes.</li>
   *   <li>Buffer limit is set to the <em>end</em> of value bytes.</li>
   * </ol>
   *
   * @param  encoded  buffer containing DER-encoded bytes positioned at start of
   * length byte(s).
   *
   * @return  number of bytes occupied by tag value.
   */
  public int readLength(final ByteBuffer encoded)
  {
    int length = 0;
    final byte b = encoded.get();
    // CheckStyle:MagicNumber OFF
    if ((b & 0x80) == 0x80) {
      final int len = b & 0x7F;
      if (len > 0) {
        encoded.limit(encoded.position() + len);
        length = IntegerType.decode(encoded).intValue();
        encoded.limit(encoded.capacity());
      }
    } else {
      length = b;
    }
    return length;
    // CheckStyle:MagicNumber ON
  }


  /**
   * Reads the supplied DER encoded bytes and invokes handlers as configured
   * paths are encountered.
   *
   * @param  encoded  to parse
   */
  private void parseTags(final ByteBuffer encoded)
  {
    int index = 0;
    while (encoded.position() < encoded.limit()) {
      final DERTag tag = readTag(encoded);
      if (tag != null) {
        currentPath.pushChild(tag.name(), index++);
        parseTag(tag, encoded);
        currentPath.popChild();
      }
    }
  }


  /**
   * Invokes the parse handler for the current path and advances to the next
   * position in the encoded bytes.
   *
   * @param  tag  to inspect for internal tags
   * @param  encoded  to parse
   */
  private void parseTag(final DERTag tag, final ByteBuffer encoded)
  {
    final int nextPos = readLength(encoded) + encoded.position();
    final ParseHandler handler = handlerMap.get(currentPath);
    if (handler != null) {
      encoded.limit(nextPos);
      handler.handle(this, encoded);
    }
    if (tag.isConstructed()) {
      parseTags(encoded);
    }
    encoded.position(nextPos);
    encoded.limit(encoded.capacity());
  }
}
