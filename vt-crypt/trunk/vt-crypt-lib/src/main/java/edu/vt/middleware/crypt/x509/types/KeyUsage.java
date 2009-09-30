/*
  $Id: KeyUsage.java 424 2009-08-11 17:26:49Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 424 $
  Updated: $Date: 2009-08-11 13:26:49 -0400 (Tue, 11 Aug 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Representation of the <code>KeyUsage</code> type defined in
 * section 4.2.1.3 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision: 424 $
 *
 */
public class KeyUsage extends BitString
{
  /**
   * Creates a new instance with the given <code>KeyUsage</code> bit string.
   *
   * @param  bitString  Octets making up key usage bit string.
   */
  public KeyUsage(final byte[] bitString)
  {
    if (bitString == null) {
      throw new IllegalArgumentException("Bit string cannot be null.");
    }
    if (bitString.length < 1 || bitString.length > 2) {
      throw new IllegalArgumentException(
        "Key usage must be 1 or 2 bytes long.");
    }
    octets = bitString;
  }


  /**
   * Creates a new instance from OR-ing {@link KeyUsageBits} bitmask values.
   *
   * @param  flags  Bit mask created by OR-ing the bitmask of
   * {@link KeyUsageBits} enum values together, e.g.
   * <code>
   * KeyUsageBits.DigitalSignature.getMask()|KeyUsageBits.KeyCertSign.getMask()
   * </code>
   */
  public KeyUsage(final int flags)
  {
    octets = getBytes(flags);
  }


  /**
   * Creates a new instance that has all the given {@link KeyUsageBits} bits
   * set.
   *
   * @param  bits  Array of {@link KeyUsageBits} describing all key usage bits
   * to be set.
   */
  public KeyUsage(final KeyUsageBits[] bits)
  {
    int flags = 0;
    for (KeyUsageBits bit : bits) {
      flags |= bit.getMask();
    }
    octets = getBytes(flags);
  }


  /**
   * Gets a collection of the bits set for this instance.
   *
   * @return  Collection of {@link KeyUsageBits} items representing the bits
   * that are set for this instance.
   */
  public Collection<KeyUsageBits> getUses()
  {
    final java.util.List<KeyUsageBits> bits = new ArrayList<KeyUsageBits>(
        KeyUsageBits.values().length);
    final int intValue = intValue();
    for (KeyUsageBits bit : KeyUsageBits.values()) {
      if ((bit.getMask() & intValue) != 0) {
        bits.add(bit);
      }
    }
    return bits;
  }


  /**
   * @return  List of all key usage bits set in this object, e.g.
   * [DigitalSignature, KeyCertSign].
   */
  @Override
  public String toString()
  {
    return getUses().toString();
  }
}
