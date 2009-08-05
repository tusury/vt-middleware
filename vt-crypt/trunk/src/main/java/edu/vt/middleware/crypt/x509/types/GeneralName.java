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
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of GeneralName type defined in RFC 2459.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class GeneralName
{
  /** Hash code seed value */
  private static final int HASH_SEED = 19;

  /** Hash code scale factor */
  private static final int HASH_FACTOR = 31;

  /** Name */
  private String name;

  /** Type of general name */
  private GeneralNameType type;


  /**
   * Creates a new instance with the given name and type.
   *
   * @param  nameString  String representation of name.
   * @param  nameType  Type of general name.
   */
  public GeneralName(final String nameString, final GeneralNameType nameType)
  {
    if (nameString == null) {
      throw new IllegalArgumentException("Name cannot be null.");
    }
    name = nameString;
    type = nameType;
  }


  /**
   * @return  String representation of name.
   */
  public String getName()
  {
    return name;
  }


  /**
   * @return  Type of name.
   */
  public GeneralNameType getType()
  {
    return type;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return getName();
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      final GeneralName other = (GeneralName) obj;
      result = other.getName().equals(getName()) &&
        other.getType().equals(getType());
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = HASH_SEED;
    hash = HASH_FACTOR * hash + getType().ordinal();
    hash = HASH_FACTOR * hash + getName().hashCode();
    return hash;
  }

}
