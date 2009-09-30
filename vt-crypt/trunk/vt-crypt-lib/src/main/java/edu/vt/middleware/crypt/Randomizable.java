/*
  $Id: Randomizable.java 3 2008-11-11 20:58:48Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3 $
  Updated: $Date: 2008-11-11 15:58:48 -0500 (Tue, 11 Nov 2008) $
*/
package edu.vt.middleware.crypt;

/**
 * Describes algorithms that support initialization with an arbitrary amount of
 * random data.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public interface Randomizable
{

  /**
   * Gets the number of random bytes used for calculations that need random
   * data.
   *
   * @return  Number of bytes of random data.
   */
  int getRandomByteSize();


  /**
   * Sets the number of random bytes used for calculations that need random
   * data.
   *
   * @param  size  Number of bytes to obtain from random provider.
   */
  void setRandomByteSize(int size);
}
