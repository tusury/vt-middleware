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
package edu.vt.middleware.crypt.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

/**
 * Utility class with methods to facilitate common operations on BouncyCastle
 * DEREncodable objects.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public final class DERHelper
{

  /** Hidden constructor of utility class. */
  private DERHelper() {}


  /**
   * Extracts the integer value from a {@link DERInteger}.
   *
   * @param  e  DERInteger instance.
   *
   * @return  Integer value.
   */
  public static int asInt(final DEREncodable e)
  {
    if (!(e instanceof DERInteger)) {
      throw new IllegalArgumentException("Argument must be DERInteger.");
    }
    return ((DERInteger) e).getValue().intValue();
  }


  /**
   * Extracts the octet string (byte array) from a {@link DEROctetString}.
   *
   * @param  e  DEROctetString instance.
   *
   * @return  Bytes of octet string value.
   */
  public static byte[] asOctets(final DEREncodable e)
  {
    if (!(e instanceof DEROctetString)) {
      throw new IllegalArgumentException("Argument must be DEROctetString.");
    }
    return ((DEROctetString) e).getOctets();
  }


  /**
   * Attempts to create a Bouncy Castle <code>DERObject</code> from a byte array
   * representing ASN.1 encoded data.
   *
   * @param  data  ASN.1 encoded data as byte array.
   * @param  discardWrapper  Set to true to decode the octets of a DER octet
   * string as DER encoded data, thereby discarding the wrapping DER octet
   * string, false otherwise. Has no effect on other types of DER-encoded data.
   *
   * @return  DER object.
   *
   * @throws  IOException  On I/O errors.
   */
  public static DERObject toDERObject(
    final byte[] data,
    final boolean discardWrapper)
    throws IOException
  {
    final ByteArrayInputStream in = new ByteArrayInputStream(data);
    try {
      DERObject o = new ASN1InputStream(in).readObject();
      if (discardWrapper && o instanceof ASN1OctetString) {
        o = new ASN1InputStream(((ASN1OctetString) o).getOctets()).readObject();
      }
      return o;
    } finally {
      in.close();
    }
  }


  /**
   * Creates a DER SEQUENCE type from the given DER encodable objects in the
   * order they are listed.
   *
   * @param  items  One or more DER encodable items.
   *
   * @return  DER SEQUENCE over given items.
   */
  public static DERSequence sequence(final DEREncodable ... items)
  {
    final ASN1EncodableVector v = new ASN1EncodableVector();
    for (DEREncodable item : items) {
      v.add(item);
    }
    return new DERSequence(v);
  }
}
