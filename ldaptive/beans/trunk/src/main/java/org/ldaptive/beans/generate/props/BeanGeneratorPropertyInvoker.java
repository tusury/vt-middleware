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
package org.ldaptive.beans.generate.props;

import java.util.HashMap;
import java.util.Map;
import org.ldaptive.props.AbstractPropertyInvoker;

/**
 * Handles properties for {@link org.ldaptive.beans.generate.BeanGenerator}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BeanGeneratorPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new bean generator property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public BeanGeneratorPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (Map.class.isAssignableFrom(type)) {
        newValue = new HashMap<String, Object>();
        final String[] keyValues = value.split(",");
        for (int i = 0; i < keyValues.length; i++) {
          final String[] s = keyValues[i].split("=");
          if (s[1].endsWith(".class")) {
            ((Map) newValue).put(
              s[0],
              createTypeFromPropertyValue(
                Class.class,
                s[1].substring(0, s[1].indexOf(".class"))));
          } else if (s[1].startsWith("[")) {
            ((Map) newValue).put(
              s[0],
              createTypeFromPropertyValue(Class.class, s[1]));
          } else {
            ((Map) newValue).put(s[0], s[1]);
          }
        }
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
