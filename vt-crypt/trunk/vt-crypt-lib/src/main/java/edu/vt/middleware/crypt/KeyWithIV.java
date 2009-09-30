/*
  $Id: KeyWithIV.java 3 2008-11-11 20:58:48Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3 $
  Updated: $Date: 2008-11-11 15:58:48 -0500 (Tue, 11 Nov 2008) $
*/
package edu.vt.middleware.crypt;

import javax.crypto.SecretKey;

/**
 * Container for a symmetric key and IV generated from a passphrase for use in
 * PBE encryption.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class KeyWithIV
{

  /** Symmetric key. */
  private SecretKey secretKey;

  /** Initialization vector. */
  private byte[] iv;


  /**
   * Creates a new instance from the given key and IV.
   *
   * @param  key  Symmetric key.
   * @param  ivBytes  Initialization vector bytes.
   */
  KeyWithIV(final SecretKey key, final byte[] ivBytes)
  {
    this.secretKey = key;
    this.iv = ivBytes;
  }


  /**
   * Gets the symmetric key.
   *
   * @return  Symmetric encryption key.
   */
  public SecretKey getKey()
  {
    return secretKey;
  }


  /**
   * Gets the initialization vector.
   *
   * @return  IV bytes.
   */
  public byte[] getIV()
  {
    return iv;
  }
}
