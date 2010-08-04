/*
  $Id: Dictionary.java 1252 2010-04-16 21:24:23Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1252 $
  Updated: $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
*/
package edu.vt.middleware.dictionary;

import java.io.IOException;
import java.util.List;

/**
 * Provides an interface for word lists.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public interface WordList extends List<String>
{


  /**
   * Returns whether to lower case when reading words.
   *
   * @return  <code>boolean</code> ignore case
   */
  boolean isLowerCase();


  /**
   * Sets whether to lower case when reading words.
   *
   * @param  b  <code>boolean</code>
   */
  void setLowerCase(final boolean b);


  /**
   * Releases any resources associated with this word list.
   *
   * @throws  IOException  if an error occurs closing this word list
   */
  void close() throws IOException;
}
