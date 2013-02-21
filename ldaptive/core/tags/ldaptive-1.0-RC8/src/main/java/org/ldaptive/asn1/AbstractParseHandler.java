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
package org.ldaptive.asn1;

/**
 * Parse handler for managing and initializing an object.
 *
 * @param  <T>  type of object initialized by this handler
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractParseHandler<T> implements ParseHandler
{

  /** Object to initialize. */
  private final T object;


  /**
   * Creates a new abstract parse handler.
   *
   * @param  t  object to initialize
   */
  public AbstractParseHandler(final T t)
  {
    object = t;
  }


  /**
   * Returns the object.
   *
   * @return  object
   */
  public T getObject()
  {
    return object;
  }
}
