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
package org.ldaptive;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;

/**
 * Simple bean for an ldap search filter and it's parameters.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchFilter
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 311;

  /** filter. */
  private String searchFilter;

  /** filter parameters. */
  private Map<String, Object> parameters = new HashMap<String, Object>();


  /** Default constructor. */
  public SearchFilter() {}


  /**
   * Creates a new search filter with the supplied filter.
   *
   * @param  filter  to set
   */
  public SearchFilter(final String filter)
  {
    searchFilter = filter;
  }


  /**
   * Creates a new search filter with the supplied filter and parameters.
   *
   * @param  filter  to set
   * @param  params  to set
   */
  public SearchFilter(final String filter, final Object[] params)
  {
    setFilter(filter);
    setParameters(params);
  }


  /**
   * Gets the filter.
   *
   * @return  filter
   */
  public String getFilter()
  {
    return searchFilter;
  }


  /**
   * Sets the filter.
   *
   * @param  filter  to set
   */
  public void setFilter(final String filter)
  {
    searchFilter = filter;
  }


  /**
   * Sets a positional filter parameter.
   *
   * @param  position  of the parameter in the filter
   * @param  value  to set
   */
  public void setParameter(final int position, final Object value)
  {
    parameters.put(Integer.toString(position), value);
  }


  /**
   * Sets a named filter parameter.
   *
   * @param  name  of the parameter in the filter
   * @param  value  to set
   */
  public void setParameter(final String name, final Object value)
  {
    parameters.put(name, value);
  }


  /**
   * Sets positional filter parameters.
   *
   * @param  values  to set
   */
  public void setParameters(final Object[] values)
  {
    int i = 0;
    for (Object o : values) {
      parameters.put(Integer.toString(i++), o);
    }
  }


  /**
   * Returns this filter with it's parameters encoded and replaced. See
   * {@link #encode(Object)}.
   *
   * @return  formated and encoded filter
   */
  public String format()
  {
    String s = searchFilter;
    if (parameters.size() > 0) {
      for (Map.Entry<String, Object> e : parameters.entrySet()) {
        final String encoded = encode(e.getValue());
        if (encoded != null) {
          s = s.replace("{" + e.getKey() + "}", encoded);
        }
      }
    }
    return s;
  }


  /**
   * Hex encodes the supplied object if it is of type byte[], otherwise the
   * string format of the object is escaped. See {@link #escape(String)}.
   *
   * @param  obj  to encode
   *
   * @return  encoded object
   */
  private static String encode(final Object obj)
  {
    if (obj == null) {
      return null;
    }

    String str;
    if (obj instanceof byte[]) {
      final char[] c = Hex.encodeHex((byte[]) obj, false);
      final StringBuilder sb = new StringBuilder(c.length + c.length / 2);
      for (int i = 0; i < c.length; i += 2) {
        sb.append('\\');
        sb.append(c[i]).append(c[i + 1]);
      }
      str = sb.toString();
    } else {
      String s = null;
      if (obj instanceof String) {
        s = (String) obj;
      } else {
        s = obj.toString();
      }
      str = escape(s);
    }
    return str;
  }


  /**
   * Escapes the supplied string per RFC 2254.
   *
   * @param  s  to escape
   *
   * @return  escaped string
   */
  private static String escape(final String s)
  {
    final int len = s.length();
    final StringBuffer sb = new StringBuffer(len);
    char ch;
    for (int i = 0; i < len; i++) {
      ch = s.charAt(i);
      switch (ch) {

      case '*':
        sb.append("\\2a");
        break;

      case '(':
        sb.append("\\28");
        break;

      case ')':
        sb.append("\\29");
        break;

      case '\\':
        sb.append("\\5c");
        break;

      case 0:
        sb.append("\\00");
        break;

      default:
        sb.append(ch);
      }
    }
    return sb.toString();
  }


  /**
   * Returns whether the supplied object contains the same data as this filter.
   * Delegates to {@link #hashCode()} implementation.
   *
   * @param  o  to compare for equality
   *
   * @return  equality result
   */
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this || (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtil.computeHashCode(HASH_CODE_SEED, searchFilter, parameters);
  }


  /**
   * This returns a string representation of this search filter.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::filter=%s, parameters=%s]",
        getClass().getName(),
        hashCode(),
        searchFilter,
        parameters);
  }
}
