/*
  $Id: KeyUsageBits.java 424 2009-08-11 17:26:49Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 424 $
  Updated: $Date: 2009-08-11 13:26:49 -0400 (Tue, 11 Aug 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of the bit meanings in the <code>KeyUsage</code>
 * BIT STRING type defined in section 4.2.1.3 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision: 424 $
 *
 */
public enum KeyUsageBits
{
  /** digitalSignature bit */
  DigitalSignature(7),

  /** nonRepudiation bit */
  NonRepudiation(6),

  /** keyEncipherment bit */
  KeyEncipherment(5),

  /** dataEncipherment bit */
  DataEncipherment(4),

  /** keyAgreement bit */
  KeyAgreement(3),

  /** keyCertSign bit */
  KeyCertSign(2),

  /** cRLSign bit */
  CRLSign(1),

  /** encipherOnly bit */
  EncipherOnly(0),

  /** decipherOnly bit */
  DecipherOnly(15);


  /** Bit mask value */
  private int mask;


  /**
   * Creates a bit flag with the given bit mask offset.
   *
   * @param  offset  Bit mask offset.
   */
  KeyUsageBits(final int offset)
  {
    mask = 1 << offset;
  }


  /**
   * @return  Bit mask value.
   */
  public int getMask()
  {
    return mask;
  }

}
