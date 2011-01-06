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
package edu.vt.middleware.crypt.pbe;

/**
 * Generates secret keys from passwords for password-based encryption schemes.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public interface KeyGenerator
{
  /**
   * Generates a symmetric key from a password for use in password-based
   * encryption schemes.
   *
   * @param  password  Password used as basis for generated key.
   * @param  size  Size of generated key in bits, unless otherwise noted.
   *
   * @return  Secret key bytes.
   */
  byte[] generate(char[] password, int size);
}
