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

import java.io.IOException;

/**
 * Base interface for all dictionaries.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Dictionary
{


  /**
   * Prepares this dictionary for use. Resources provided to the dictionary may
   * consumed in order to conserve memory.
   *
   * @throws  IOException  if an error occurs initializing this dictionary
   */
  void initialize() throws IOException;


  /**
   * Returns whether the supplied word exists in the dictionary.
   *
   * @param  word  <code>String</code> to search for
   *
   * @return  <code>boolean</code> - whether word was found
   */
  boolean search(String word);


  /**
   * Releases any resources associated with this dictionary.
   *
   * @throws  IOException  if an error occurs closing this dictionary
   */
  void close() throws IOException;
}
