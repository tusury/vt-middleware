/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of the options in the CHOICE element describing various
 * categories of the <code>GeneralName</code> type defined in section 4.2.1.7 of
 * RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision: 422 $
 */
public enum GeneralNameType {

  /** otherName choice element. */
  OtherName,

  /** rfc822Name choice element. */
  RFC822Name,

  /** dNSName choice element. */
  DNSName,

  /** x400Address choice element. */
  X400Address,

  /** directoryName choice element. */
  DirectoryName,

  /** ediPartyName choice element. */
  EdiPartyName,

  /** uniformResourceIdentifier choice element. */
  UniformResourceIdentifier,

  /** iPAddress choice element. */
  IPAddress,

  /** registeredID choice element. */
  RegisteredID;


  /** Minimum tag number for items in CHOICE definition. */
  public static final int MIN_TAG_NUMBER = 0;

  /** Maximum tag number for items in CHOICE definition. */
  public static final int MAX_TAG_NUMBER = 8;


  /**
   * Gets a name type from the value of the tag in the CHOICE element
   * definition.
   *
   * @param  tagNo  Ordinal position of type in CHOICE definition in RFC 2459.
   *
   * @return  Type corresponding to given tag number.
   *
   * @throws  IllegalArgumentException  If there is not general name type
   * corresponding to the given tag number.
   */
  public static GeneralNameType fromTagNumber(final int tagNo)
  {
    if (tagNo < MIN_TAG_NUMBER || tagNo > MAX_TAG_NUMBER) {
      throw new IllegalArgumentException("Invalid tag number " + tagNo);
    }
    for (GeneralNameType type : values()) {
      if (type.ordinal() == tagNo) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid tag number " + tagNo);
  }
}
