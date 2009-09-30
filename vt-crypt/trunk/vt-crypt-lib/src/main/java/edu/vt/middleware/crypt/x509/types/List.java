/*
  $Id: List.java 421 2009-08-06 19:20:53Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 421 $
  Updated: $Date: 2009-08-06 15:20:53 -0400 (Thu, 06 Aug 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

/**
 * Interface describing a type that is simply a collection of other types.
 *
 * @author Middleware
 * @version $Revision: 421 $
 * @param <T> Type of object contained in collection.
 *
 */
public interface List<T>
{
  /**
   * @return  Array of items in the collection.
   */
  T[] getItems();
}
