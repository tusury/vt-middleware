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
 * Interface describing a type that is simply a collection of other types.
 *
 * @author Middleware
 * @version $Revision$
 * @param <T> Type of object contained in collection.
 *
 */
public interface Collection<T>
{
  /**
   * @return  Array of items in the collection.
   */
  T[] getItems();
}
