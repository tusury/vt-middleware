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

import java.util.List;

/**
 * Representation of the <code>GeneralNames</code> type defined in section
 * 4.2.1.7 of RFC 2459, which simply stores a list of {@link GeneralName} items.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class GeneralNameList extends AbstractList<GeneralName>
{
  /** Hash code seed value */
  private static final int HASH_SEED = 21;


  /**
   * Constructs a new instance from the given list of names.
   *
   * @param  listOfNames  List of names.
   */
  public GeneralNameList(final List<GeneralName> listOfNames)
  {
    if (listOfNames == null) {
      throw new IllegalArgumentException("List of names cannot be null.");
    }
    items = listOfNames.toArray(new GeneralName[listOfNames.size()]);
  }


  /**
   * Constructs a new instance from the given array of names.
   *
   * @param  arrayOfNames  Array of names.
   */
  public GeneralNameList(final GeneralName[] arrayOfNames)
  {
    if (arrayOfNames == null) {
      throw new IllegalArgumentException("Array of names cannot be null.");
    }
    items = arrayOfNames;
  }


  /** {@inheritDoc} */
  @Override
  protected int getHashSeed()
  {
    return HASH_SEED;
  }
}
