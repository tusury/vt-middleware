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

/**
 * Interface describing a type that is simply a collection of other types.
 *
 * @param  <T>  Type of object contained in collection.
 *
 * @author  Middleware Services
 * @version  $Revision: 421 $
 */
public interface List<T>
{

  /** @return  Array of items in the collection. */
  T[] getItems();
}
