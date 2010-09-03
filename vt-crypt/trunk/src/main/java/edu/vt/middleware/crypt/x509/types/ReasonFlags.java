/*
  $Id$

  Copyright (C) 2007-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Representation of the <code>ReasonFlags</code> type defined in section
 * 4.2.1.14 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision: 427 $
 */
public class ReasonFlags extends BitString
{

  /**
   * Creates a new instance with the given bit string, where each bit is one
   * of the constants defined in the {@link Reasons} enumeration.
   *
   * @param  bitString  Octets making up reason flags bit string.
   */
  public ReasonFlags(final byte[] bitString)
  {
    if (bitString == null) {
      throw new IllegalArgumentException("Bit string cannot be null.");
    }
    if (bitString.length < 1 || bitString.length > 2) {
      throw new IllegalArgumentException(
        "Reason flags must be 1 or 2 bytes long.");
    }
    octets = bitString;
  }


  /**
   * Creates a new instance from OR-ing {@link Reasons} bitmask values.
   *
   * @param  flags  Bit mask created by OR-ing the bitmask of {@link
   * Reasons} enum values together, e.g.
   * <code>
   * Reasons.KeyCompromise.getMask()|Reasons.CessationOfOperation.getMask()
   * </code>
   */
  public ReasonFlags(final int flags)
  {
    octets = getBytes(flags);
  }


  /**
   * Creates a new instance that has all the given {@link Reasons} bits set.
   *
   * @param  reasons  Array of {@link Reasons} describing all key usage bits to
   * be set.
   */
  public ReasonFlags(final Reasons[] reasons)
  {
    int flags = 0;
    for (Reasons bit : reasons) {
      flags |= bit.getMask();
    }
    octets = getBytes(flags);
  }


  /**
   * Gets a collection of the bits set for this instance.
   *
   * @return  Collection of {@link Reasons} representing the bits that are
   * set for this instance.
   */
  public Collection<Reasons> getReasons()
  {
    final java.util.List<Reasons> bits = new ArrayList<Reasons>(
      Reasons.values().length);
    final int intValue = intValue();
    for (Reasons bit : Reasons.values()) {
      if ((bit.getMask() & intValue) != 0) {
        bits.add(bit);
      }
    }
    return bits;
  }


  /**
   * @return  List of all reason bits set in this object, e.g. [KeyCompromise,
   * CessationOfOperation].
   */
  @Override
  public String toString()
  {
    return getReasons().toString();
  }
}
