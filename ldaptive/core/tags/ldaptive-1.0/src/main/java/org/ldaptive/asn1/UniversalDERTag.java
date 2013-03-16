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
package org.ldaptive.asn1;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration with common BER/DER universal tag types.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum UniversalDERTag implements DERTag {

  /** BOOLEAN type. */
  BOOL(1, false),

  /** INTEGER type. */
  INT(2, false),

  /** BITSTRING type. */
  BITSTR(3, false),

  /** OCTETSTRING type. */
  OCTSTR(4, false),

  /** OBJECT IDENTIFIER type. */
  OID(6, false),

  /** ENUMERATED type. */
  ENUM(10, false),

  /** SEQUENCE type. */
  SEQ(16, true),

  /** SET type. */
  SET(17, true);


  /** Universal tag class is 00b in first two high-order bytes. */
  public static final int TAG_CLASS = 0;

  /** Maps tag numbers to tags. */
  private static final Map<Integer, UniversalDERTag> TAGNO_MAP =
    new HashMap<Integer, UniversalDERTag>();

  /** Maps tag names to tags. */
  private static final Map<String, UniversalDERTag> TAGNAME_MAP =
    new HashMap<String, UniversalDERTag>();


  /**
   * Initializes tag mapping.
   */
  static {
    for (UniversalDERTag tag : UniversalDERTag.values()) {
      TAGNO_MAP.put(tag.getTagNo(), tag);
      TAGNAME_MAP.put(tag.name(), tag);
    }
  }

  /** Tag number. */
  private final int tagNo;

  /** Flag indicating whether value is primitive or constructed. */
  private final boolean constructed;


  /**
   * Creates a new universal DER tag.
   *
   * @param  number  of the tag
   * @param  isConstructed  whether this tag is primitive or constructed
   */
  UniversalDERTag(final int number, final boolean isConstructed)
  {
    tagNo = number;
    constructed = isConstructed;
  }


  /**
   * Gets the decimal value of the tag.
   *
   * @return  decimal tag number.
   */
  @Override
  public int getTagNo()
  {
    return tagNo;
  }


  /**
   * Determines whether the tag is constructed or primitive.
   *
   * @return  true if constructed, false if primitive.
   */
  @Override
  public boolean isConstructed()
  {
    return constructed;
  }


  /** {@inheritDoc} */
  @Override
  public int getTagByte()
  {
    return constructed ? tagNo | ASN_CONSTRUCTED : tagNo;
  }


  /**
   * Looks up a universal tag from a tag number.
   *
   * @param  number  tag number.
   *
   * @return  tag object corresponding to given number.
   *
   * @throws  IllegalArgumentException  if tag is unknown
   */
  public static UniversalDERTag fromTagNo(final int number)
  {
    final UniversalDERTag derTag = TAGNO_MAP.get(number);
    if (derTag == null) {
      throw new IllegalArgumentException("Unknown tag number: " + number);
    }
    return derTag;
  }


  /**
   * Looks up a universal tag from a tag name. This method differs from {@link
   * #valueOf(String)} in that it does not throw for unknown names.
   *
   * @param  name  tag name.
   *
   * @return  tag object corresponding to given name or null if no tag of the
   * given name is found.
   */
  public static UniversalDERTag fromTagName(final String name)
  {
    return TAGNAME_MAP.get(name);
  }
}
