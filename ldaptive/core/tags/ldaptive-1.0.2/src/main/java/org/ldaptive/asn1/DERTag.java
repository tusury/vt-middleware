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

/**
 * Describes the tag of a DER-encoded type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface DERTag
{

  /** Constructed tags should have the 6th bit set. */
  int ASN_CONSTRUCTED = 0x20;


  /**
   * Gets the decimal value of the tag.
   *
   * @return  decimal tag number.
   */
  int getTagNo();


  /**
   * Gets the name of the tag.
   *
   * @return  tag name.
   */
  String name();


  /**
   * Determines whether the tag is constructed or primitive.
   *
   * @return  true if constructed, false if primitive.
   */
  boolean isConstructed();


  /**
   * Gets the value of this tag for encoding.
   *
   * @return  byte value of this tag
   */
  int getTagByte();
}
