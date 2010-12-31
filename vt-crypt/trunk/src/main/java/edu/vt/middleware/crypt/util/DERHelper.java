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

import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERInteger;
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
}
