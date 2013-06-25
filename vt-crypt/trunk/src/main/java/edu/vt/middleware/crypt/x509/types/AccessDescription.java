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
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of the <code>AccessDescription</code> type described in
 * section 4.2.2.1 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AccessDescription
{

  /** Hash code scale factor. */
  private static final int HASH_FACTOR = 31;

  /** Access method. */
  private AccessMethod accessMethod;

  /** Location. */
  private GeneralName accessLocation;


  /**
   * Creates a new instance with the given access method and location.
   *
   * @param  method  Access method; cannot be null.
   * @param  location  Access location; cannot be null.
   */
  public AccessDescription(
    final AccessMethod method,
    final GeneralName location)
  {
    if (method == null) {
      throw new IllegalArgumentException("Access method cannot be null.");
    }
    if (location == null) {
      throw new IllegalArgumentException("Access location cannot be null.");
    }
    accessMethod = method;
    accessLocation = location;
  }


  /** @return  Access method. */
  public AccessMethod getAccessMethod()
  {
    return accessMethod;
  }


  /** @return  Access location. */
  public GeneralName getAccessLocation()
  {
    return accessLocation;
  }


  /**
   * @return  String indicating the type of qualifier followed by a string
   * representation of the qualifier.
   */
  @Override
  public String toString()
  {
    return String.format("%s:%s", accessMethod, accessLocation);
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      final AccessDescription other = (AccessDescription) obj;
      result = accessMethod.equals(other.getAccessMethod()) &&
        accessLocation.equals(other.getAccessLocation());
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    hash = HASH_FACTOR * hash + accessMethod.hashCode();
    hash = HASH_FACTOR * hash + accessLocation.hashCode();
    return hash;
  }
}
