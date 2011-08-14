/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.List;

/**
 * Represents the sequence of <code>KeyPurposeId</code> types that are contained
 * in the <code>ExtendedKeyUsage</code> extension field described in section
 * 4.2.1.13 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision: 427 $
 */
public class KeyPurposeIdList extends AbstractList<KeyPurposeId>
{

  /**
   * Constructs a new instance from the given list of key purpose identifiers.
   *
   * @param  listOfKeyPurposeIds  List of key purpose identifiers.
   */
  public KeyPurposeIdList(final List<KeyPurposeId> listOfKeyPurposeIds)
  {
    if (listOfKeyPurposeIds == null) {
      throw new IllegalArgumentException(
        "List of key purpose IDs cannot be null.");
    }
    items = listOfKeyPurposeIds.toArray(
      new KeyPurposeId[listOfKeyPurposeIds.size()]);
  }


  /**
   * Constructs a new instance from the given array of key purpose identifiers.
   *
   * @param  arrayOfKeyPurposeIds  Array of key purpose identifiers.
   */
  public KeyPurposeIdList(final KeyPurposeId[] arrayOfKeyPurposeIds)
  {
    if (arrayOfKeyPurposeIds == null) {
      throw new IllegalArgumentException(
        "Array of key purpose IDs  cannot be null.");
    }
    items = arrayOfKeyPurposeIds;
  }
}
