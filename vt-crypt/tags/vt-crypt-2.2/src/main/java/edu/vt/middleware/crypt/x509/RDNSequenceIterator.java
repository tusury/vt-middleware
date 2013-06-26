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
package edu.vt.middleware.crypt.x509;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.vt.middleware.crypt.util.DERHelper;
import edu.vt.middleware.crypt.x509.types.RelativeDistinguishedName;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObject;

/**
 * Iterates over an ASN.1 encoded RelativeDistinguishedName sequence in reverse
 * order as specified in section 2.1 of RFC 2253 for converting an ASN.1
 * representation of an RDN sequence to a string.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class RDNSequenceIterator
  implements Iterator<RelativeDistinguishedName>,
    Iterable<RelativeDistinguishedName>
{

  /** RDN sequence. */
  private final ASN1Sequence sequence;

  /** Current iterator position. */
  private int position;


  /**
   * Creates a new instance from the bytes comprising an encoded ASN.1 sequence
   * of RDN types.
   *
   * @param  encodedRDNSequence  ASN.1 encoded sequence as a byte array.
   */
  public RDNSequenceIterator(final byte[] encodedRDNSequence)
  {
    this(toASN1Sequence(encodedRDNSequence));
  }


  /**
   * Creates a new instance from an ASN.1 sequence of RDNs.
   *
   * @param  rdnSequence  ASN.1 sequence containing AttributeValueAndType items
   * representing RDNs.
   */
  public RDNSequenceIterator(final ASN1Sequence rdnSequence)
  {
    sequence = rdnSequence;
    position = sequence.size() - 1;
  }


  /** {@inheritDoc} */
  public boolean hasNext()
  {
    return position > -1;
  }


  /** {@inheritDoc} */
  public RelativeDistinguishedName next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException("Reached end of iterator.");
    }

    final DEREncodable enc = sequence.getObjectAt(position--);
    if (!(enc instanceof ASN1Set)) {
      throw new IllegalStateException("Next item is not an ASN.1 set.");
    }
    return RelativeDistinguishedName.fromASN1Set((ASN1Set) enc);
  }


  /**
   * Not supported.
   *
   * @throws  UnsupportedOperationException  In all cases.
   */
  public void remove()
  {
    throw new UnsupportedOperationException("Remove not supported.");
  }


  /** {@inheritDoc} */
  public Iterator<RelativeDistinguishedName> iterator()
  {
    return this;
  }


  /**
   * Creates an ASN.1 sequence from the given byte array.
   *
   * @param  data  Encoded bytes of an ASN.1 sequence.
   *
   * @return  ASN.1 sequence object.
   *
   * @throws  IllegalArgumentException  If data is not encoded bytes of an ASN.1
   * sequence.
   */
  private static ASN1Sequence toASN1Sequence(final byte[] data)
  {
    DERObject obj;
    try {
      obj = DERHelper.toDERObject(data, false);
    } catch (IOException e) {
      throw new IllegalArgumentException(
        "Error creating ASN.1 sequence from encoded bytes.",
        e);
    }
    if (obj instanceof ASN1Sequence) {
      return (ASN1Sequence) obj;
    } else {
      throw new IllegalArgumentException(
        "Encoded data is not an ASN.1 sequence.");
    }
  }
}
