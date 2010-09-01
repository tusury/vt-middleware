/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.dictionary;

/**
 * Base interface for all dictionaries.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Dictionary
{


  /**
   * Returns whether the supplied word exists in the dictionary.
   *
   * @param  word  <code>String</code> to search for
   *
   * @return  <code>boolean</code> - whether word was found
   */
  boolean search(String word);
}
