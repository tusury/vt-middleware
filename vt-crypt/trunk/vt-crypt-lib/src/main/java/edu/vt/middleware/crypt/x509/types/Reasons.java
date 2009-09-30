/*
  $Id: Reasons.java 427 2009-08-12 16:41:24Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 427 $
  Updated: $Date: 2009-08-12 12:41:24 -0400 (Wed, 12 Aug 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

/**
 * Definitions of the meanings of the bits in the <code>ReasonFlags</code>
 * BIT STRING defined in section 4.2.1.14 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision: 427 $
 *
 */
public enum Reasons
{
  /** Unused */
  Unused(7),

  /** KeyCompromise */
  KeyCompromise(6),

  /** CACompromise */
  CACompromise(5),

  /** AffiliationChanged */
  AffiliationChanged(4),

  /** Superseded */
  Superseded(3),

  /** CessationOfOperation */
  CessationOfOperation(2),

  /** CertificateHold */
  CertificateHold(1);



  /** Bit mask value */
  private int mask;


  /**
   * Creates a bit flag with the given bit mask offset.
   *
   * @param  offset  Bit mask offset.
   */
  Reasons(final int offset)
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
