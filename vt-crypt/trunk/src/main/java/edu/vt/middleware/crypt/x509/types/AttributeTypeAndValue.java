/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of AttributeTypeAndValue type described in section 2 of
 * RFC 2253.  This type is used to represent the RelativeDistinguishedName
 * types that comprise the RDN sequence describing an LDAPv3
 * distinguished name (DN).
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AttributeTypeAndValue
{
  /**
   * Set of characters in the value that MUST be escaped in the string
   * representation.
   */
  public static final Set<Character> ESCAPE_CHARSET;

  /** Escape character */
  public static final char ESCAPE_CHAR = '\\';

  /** Hash code scale factor */
  private static final int HASH_FACTOR = 31;

  /** Attribute type string; either OID or well-known type abbreviation */
  private String type;

  /** Attribute value as a string */
  private String value;


  /** Class initializer */
  static
  {
    // ",", "+", """, "\", "<", ">" or ";"
    ESCAPE_CHARSET = new HashSet<Character>();
    ESCAPE_CHARSET.add(',');
    ESCAPE_CHARSET.add('+');
    ESCAPE_CHARSET.add('"');
    ESCAPE_CHARSET.add('\\');
    ESCAPE_CHARSET.add('<');
    ESCAPE_CHARSET.add('>');
    ESCAPE_CHARSET.add(';');
  }


  /**
   * Creates a new instance with the given type OID and value.
   *
   * @param  typeOid  Attribute type OID.
   * @param  attributeValue  Attribute value.
   */
  public AttributeTypeAndValue(
      final String typeOid,
      final String attributeValue)
  {
    final AttributeType attrType = AttributeType.fromOid(typeOid);
    if (attrType != null) {
      type = attrType.getName();
    } else {
      type = typeOid;
    }
    value = attributeValue;
  }


  /**
   * Creates a new instance with the given well-known attribute type and value.
   *
   * @param  attributeType Attribute type.
   * @param  attributeValue  Attribute value.
   */
  public AttributeTypeAndValue(
      final AttributeType attributeType,
      final String attributeValue)
  {
    type = attributeType.getName();
    value = attributeValue;
  }


  /**
   * @return  Attribute type.
   */
  public String getType()
  {
    return type;
  }


  /**
   * @return  Attribute value.
   */
  public String getValue()
  {
    return value;
  }


  /**
   * Follows the guidelines of RFC 2253 section 2.3 for producing the
   * string representation of the AttributeTypeAndValue type.
   *
   * @return  String representation of RDN.
   */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(70);
    sb.append(type);
    sb.append('=');
    // Start and end of the value have additional considerations
    // - Must escape # or space at start
    // - Must escape space at end
    if ('#' == value.charAt(0) ||
        ' ' == value.charAt(0) ||
        ESCAPE_CHARSET.contains(value.charAt(0)))
    {
      sb.append(ESCAPE_CHAR);
    }
    sb.append(value.charAt(0));
    int i = 1;
    for (; i < value.length() - 1; i++) {
      if (ESCAPE_CHARSET.contains(value.charAt(i))) {
        sb.append(ESCAPE_CHAR);
      }
      sb.append(value.charAt(i));
    }
    if (' ' == value.charAt(i) ||
        ESCAPE_CHARSET.contains(value.charAt(i)))
    {
      sb.append(ESCAPE_CHAR);
    }
    sb.append(value.charAt(i));
    return sb.toString();
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      final AttributeTypeAndValue other = (AttributeTypeAndValue) obj;
      result = type.equals(other.getType()) && value.equals(other.getValue());
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    hash = HASH_FACTOR * hash + type.hashCode();
    hash = HASH_FACTOR * hash + value.hashCode();
    return hash;
  }
}
