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
package edu.vt.middleware.crypt.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;

/**
 * Utility class with methods to facilitate common operations on BouncyCastle
 * DEREncodable objects.
 *
 * @author Middleware
 * @version $Revision$
 *
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
   * @param  discardWrapper  In some cases the value of the encoded data may
   * itself be encoded data, where the latter encoded data is desired. Recall
   * ASN.1 data is of the form {TAG, SIZE, DATA}. Set this flag to true to skip
   * the first two bytes, e.g. TAG and SIZE, and treat the remaining bytes as
   * the encoded data.
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
    final ByteArrayInputStream inBytes = new ByteArrayInputStream(data);
    int size = data.length;
    if (discardWrapper) {
      inBytes.skip(2);
      size = data.length - 2;
    }

    final ASN1InputStream in = new ASN1InputStream(inBytes, size);
    try {
      return in.readObject();
    } finally {
      in.close();
    }
  }
}
