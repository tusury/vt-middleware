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
package edu.vt.middleware.crypt.pbe;

import edu.vt.middleware.crypt.digest.MD5;

/**
 * Implements a password generation function compatible with the enc operation
 * of OpenSSL in PBE mode.  The function is based on a variant of the PBKDF1 key
 * generation function described in PKCS#5v2, but uses an invariant MD5 hash
 * and a fixed iteration count of 1.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class OpenSSLKeyGenerator extends PBKDF1KeyGenerator
{

  /**
   * Performs key generation without a salt value.  This method is intended
   * for compatibility with old OpenSSL versions or modern OpenSSL versions
   * of the enc command with the -nosalt option.
   */
  public OpenSSLKeyGenerator()
  {
    this(new byte[0]);
  }


  /**
   * Creates a new key generator with the given salt bytes.
   *
   * @param  salt  Key generation function salt data.
   */
  public OpenSSLKeyGenerator(final byte[] salt)
  {
    super(new MD5(), salt, 1);
  }
}
