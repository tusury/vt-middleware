/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.beans.reflect;

import java.util.HashMap;
import java.util.Map;
import org.ldaptive.io.ValueTranscoder;

/**
 * Creates value transcoders and stores them in a static map.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class TranscoderFactory
{

  /** Value transcoders. */
  private static final Map<String, ValueTranscoder<?>> TRANSCODERS =
    new HashMap<String, ValueTranscoder<?>>();


  /** Default constructor. */
  private TranscoderFactory() {}


  /**
   * Returns a value transcoder for the supplied type. If the type cannot be
   * found it is instantiated and cached for future use.
   *
   * @param  type  of value transcoder
   *
   * @return  value transcoder
   */
  public static ValueTranscoder<?> getInstance(final String type)
  {
    if (type == null || "".equals(type)) {
      return null;
    }

    ValueTranscoder<?> transcoder;
    synchronized (TRANSCODERS) {
      if (!TRANSCODERS.containsKey(type)) {
        transcoder = createValueTranscoder(type);
        TRANSCODERS.put(type, transcoder);
      } else {
        transcoder = TRANSCODERS.get(type);
      }
    }
    return transcoder;
  }


  /**
   * Creates a value transcoder for the supplied type.
   *
   * @param  type  to create value transcoder for
   *
   * @return  value transcoder
   *
   * @throws  IllegalArgumentException  if the supplied type cannot be
   * instantiated
   */
  protected static ValueTranscoder<?> createValueTranscoder(final String type)
  {
    try {
      return (ValueTranscoder<?>) Class.forName(type).newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not instantiate transcoder", e);
    }
  }
}
