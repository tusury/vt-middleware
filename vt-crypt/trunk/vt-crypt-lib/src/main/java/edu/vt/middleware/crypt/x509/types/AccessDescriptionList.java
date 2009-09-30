/*
  $Id: AccessDescriptionList.java 428 2009-08-12 18:12:49Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 428 $
  Updated: $Date: 2009-08-12 14:12:49 -0400 (Wed, 12 Aug 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.List;

/**
 * Representation of the SEQUENCE of <code>AccessDescription</code> types that
 * is the value of the <code>AuthorityInformationAccess</code> extension field
 * defined in section 4.2.2.1 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision: 428 $
 *
 */
public class AccessDescriptionList extends AbstractList<AccessDescription>
{
  /**
   * Constructs a new instance from the given list of access descriptions.
   *
   * @param  listOfAccessDescriptions List of access descriptions.
   */
  public AccessDescriptionList(
    final List<AccessDescription> listOfAccessDescriptions)
  {
    if (listOfAccessDescriptions == null) {
      throw new IllegalArgumentException(
        "List of access descriptions cannot be null.");
    }
    items = listOfAccessDescriptions.toArray(
      new AccessDescription[listOfAccessDescriptions.size()]);
  }


  /**
   * Constructs a new instance from the given array of access descriptions.
   *
   * @param  arrayOfAccessDescriptions  Array of access descriptions.
   */
  public AccessDescriptionList(
    final AccessDescription[] arrayOfAccessDescriptions)
  {
    if (arrayOfAccessDescriptions == null) {
      throw new IllegalArgumentException(
        "Array of access descriptions cannot be null.");
    }
    items = arrayOfAccessDescriptions;
  }
}
