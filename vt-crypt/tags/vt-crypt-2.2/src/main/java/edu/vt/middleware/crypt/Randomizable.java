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
package edu.vt.middleware.crypt;

/**
 * Describes algorithms that support initialization with an arbitrary amount of
 * random data.
 *
 * @author  Middleware Services
 * @version  $Revision$
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
