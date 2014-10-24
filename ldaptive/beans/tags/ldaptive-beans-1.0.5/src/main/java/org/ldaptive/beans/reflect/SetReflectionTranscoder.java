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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Reflection transcoder for an object that implements {@link Set}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SetReflectionTranscoder
  extends AbstractCollectionReflectionTranscoder
{


  /**
   * Creates a new set reflection transcoder.
   *
   * @param  c  class that is a set
   * @param  transcoder  to operate on elements of the set
   */
  public SetReflectionTranscoder(
    final Class<?> c,
    final SingleValueReflectionTranscoder<?> transcoder)
  {
    super(c, transcoder);
  }


  /**
   * Creates a new set reflection transcoder.
   *
   * @param  c  class that is a set
   * @param  transcoder  to operate on elements of the set
   */
  public SetReflectionTranscoder(
    final Class<?> c,
    final ArrayReflectionTranscoder transcoder)
  {
    super(c, transcoder);
  }


  /** {@inheritDoc} */
  @Override
  protected <T> Collection<T> createCollection(final Class<T> clazz)
  {
    final Class<?> type = getType();
    Set<T> s;
    if (LinkedHashSet.class.isAssignableFrom(type)) {
      s = new LinkedHashSet<T>();
    } else if (TreeSet.class.isAssignableFrom(type)) {
      s = new TreeSet<T>();
    } else {
      s = new HashSet<T>();
    }
    return s;
  }
}
