/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import java.util.Arrays;

/**
 * Enum to define constants specific to ldap return attributes.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum ReturnAttributes {

  /** all user and operational attributes. */
  ALL(new String[] {"*", "+"}),

  /** all user attributes. */
  ALL_USER(new String[] {"*"}),

  /** all operational attributes. */
  ALL_OPERATIONAL(new String[] {"+"}),

  /** no attributes. */
  NONE(new String[] {"1.1"});

  /** underlying value. */
  private final String[] value;


  /**
   * Creates a new return attributes.
   *
   * @param  s  value
   */
  ReturnAttributes(final String[] s)
  {
    value = s;
  }


  /**
   * Returns the value(s).
   *
   * @return  ldap return attribute
   */
  public String[] value()
  {
    return value;
  }


  /**
   * Returns whether the supplied attributes matches the value of this return
   * attributes.
   *
   * @param  attrs  to compare
   *
   * @return  whether attrs contains only this return attributes
   */
  public boolean equalsAttributes(final String... attrs)
  {
    return Arrays.equals(value, attrs);
  }


  /**
   * Combines the supplied attributes with the value of this return attributes.
   *
   * @param  attrs  to combine
   *
   * @return  combined attributes
   */
  public String[] add(final String... attrs)
  {
    return LdapUtils.concatArrays(value, attrs);
  }


  /**
   * Parses the supplied return attributes and applies the following convention:
   * <ul>
   *   <li>null == {@link ReturnAttributes#ALL_USER}</li>
   *   <li>empty array == {@link ReturnAttributes#NONE}</li>
   * </ul>
   *
   * @param  attrs  to parse
   *
   * @return  parsed attributes according to convention
   */
  public static String[] parse(final String... attrs)
  {
    String[] retAttrs;
    if (attrs == null) {
      retAttrs = ReturnAttributes.ALL_USER.value();
    } else if (attrs.length == 0) {
      retAttrs = ReturnAttributes.NONE.value();
    } else {
      retAttrs = attrs;
    }
    return retAttrs;
  }
}
