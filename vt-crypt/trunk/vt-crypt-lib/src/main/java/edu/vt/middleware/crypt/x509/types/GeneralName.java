/*
  $Id: GeneralName.java 427 2009-08-12 16:41:24Z marvin.addison $

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
 * Representation of the <code>GeneralName</code> type defined in
 * section 4.2.1.7 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision: 427 $
 *
 */
public class GeneralName
{
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


  /**
   * @return Value of {@link #getName()}.
   */
  @Override
  public String toString()
  {
    return name;
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
      result = other.getName().equals(name) &&
        other.getType().equals(type);
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    hash = HASH_FACTOR * hash + type.ordinal();
    hash = HASH_FACTOR * hash + name.hashCode();
    return hash;
  }
}
