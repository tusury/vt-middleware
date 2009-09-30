/*
  $Id: RelativeDistinguishedName.java 578 2009-09-08 19:10:23Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 578 $
  Updated: $Date: 2009-09-08 15:10:23 -0400 (Tue, 08 Sep 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObjectIdentifier;

/**
 * Representation of RelativeDistinguishedName type described in section 2 of
 * RFC 2253.
 *
 * @author Middleware
 * @version $Revision: 578 $
 *
 */
public class RelativeDistinguishedName
  extends AbstractList<AttributeTypeAndValue>
{
  /** Separator character between AttributeTypeAndValue items making up RDN */
  public static final char SEPARATOR_CHAR = '+';


  /**
   * Creates a new instance with the given list of values.
   *
   * @param  listOfValues  List of values for the RDN.
   */
  public RelativeDistinguishedName(
      final List<AttributeTypeAndValue> listOfValues)
  {
    if (listOfValues == null) {
      throw new IllegalArgumentException("List of values cannot be null.");
    }
    if (listOfValues.size() == 0) {
      throw new IllegalArgumentException(
          "List must contain at least one value.");
    }
    items = listOfValues.toArray(
        new AttributeTypeAndValue[listOfValues.size()]);
  }


  /**
   * Creates a new instance with the given array of values.
   *
   * @param  arrayOfValues  Array of values for the RDN.
   */
  public RelativeDistinguishedName(final AttributeTypeAndValue[] arrayOfValues)
  {
    if (arrayOfValues == null) {
      throw new IllegalArgumentException("Array of values cannot be null.");
    }
    if (arrayOfValues.length == 0) {
      throw new IllegalArgumentException(
          "Array must contain at least one value.");
    }
    items = arrayOfValues;
  }


  /**
   * Creates a new instance with a single value.
   *
   * @param  value  The sole value of the RDN.
   */
  public RelativeDistinguishedName(final AttributeTypeAndValue value)
  {
    this(new AttributeTypeAndValue[] {value});
  }


  /**
   * Follows the guidelines of RFC 2253 section 2.2 for producing the
   * string representation of the RelativeDistinguishedName type.
   *
   * @return  String representation of RDN.
   */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(100);
    int i = 0;
    for (AttributeTypeAndValue item : items) {
      if (i++ > 0) {
        sb.append(SEPARATOR_CHAR);
      }
      sb.append(item.toString());
    }
    return sb.toString();
  }


  /**
   * Creates a new instance from an ASN.1 SET of SEQUENCE representing
   * the AttributeTypeAndValue type of section 2 of RFC 2253.
   *
   * @param  set  Set from which to create new RDN instance.
   *
   * @return  New RDN from encoded data.
   */
  public static RelativeDistinguishedName fromASN1Set(final ASN1Set set)
  {
    final List<AttributeTypeAndValue> values =
      new ArrayList<AttributeTypeAndValue>();
    for (int i = 0; i < set.size(); i++) {
      final DEREncodable value = set.getObjectAt(i);
      if (!(value instanceof ASN1Sequence)) {
        throw new IllegalArgumentException("Value must be ASN.1 sequence.");
      }
      final ASN1Sequence seq = (ASN1Sequence) value;
      if (seq.size() != 2) {
        throw new IllegalArgumentException(
          "Illegal sequence size " + seq.size());
      }
      if (!(seq.getObjectAt(0) instanceof DERObjectIdentifier)) {
        throw new IllegalArgumentException("First sequence item must be OID.");
      }
      values.add(
          new AttributeTypeAndValue(
              seq.getObjectAt(0).toString(),
              seq.getObjectAt(1).toString()));
    }
    return new RelativeDistinguishedName(values);
  }
}
