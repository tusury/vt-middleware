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
package org.ldaptive.beans;

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
  private static final Map<Class<?>, ValueTranscoder<?>> TRANSCODERS =
    new HashMap<Class<?>, ValueTranscoder<?>>();


  /**
   * Default constructor.
   */
  private TranscoderFactory() {}


  /**
   * Returns a value transcoder for the supplied type. If the type cannot be
   * found it is instantiated and cached for future use.
   *
   * @param  type  of value transcoder
   *
   * @return  value transcoder
   */
  public static ValueTranscoder<?> getInstance(
    final Class<? extends ValueTranscoder<?>> type)
  {
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
   * Creates a value transcoder for the supplied type. Ignores {@link
   * Attribute.NoValueTranscoder} and returns null.
   *
   * @param  type  to create value transcoder for
   *
   * @return  value transcoder
   *
   * @throws  IllegalArgumentException  if the supplied type cannot be
   * instantiated
   */
  protected static ValueTranscoder<?> createValueTranscoder(
    final Class<? extends ValueTranscoder<?>> type)
  {
    ValueTranscoder<?> transcoder = null;
    if (Attribute.NoValueTranscoder.class != type) {
      try {
        transcoder = type.newInstance();
      } catch (InstantiationException e) {
        throw new IllegalArgumentException(
          "Could not instantiate transcoder", e);
      } catch (IllegalAccessException e) {
        throw new IllegalArgumentException(
          "Could not instantiate transcoder", e);
      }
    }
    return transcoder;
  }
}
