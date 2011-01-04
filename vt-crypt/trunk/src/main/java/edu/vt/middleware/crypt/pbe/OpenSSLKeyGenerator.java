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

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

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
public class OpenSSLKeyGenerator implements KeyGenerator
{
  /** Size of derived key in bits. */
  private int derivedKeyLength;

  /** Key generation salt data. */
  private byte[] salt;


  /**
   * Performs key generation without a salt value.  This method is intended
   * for compatibility with old OpenSSL versions or modern OpenSSL versions
   * of the enc command with the -nosalt option.
   *
   * @param  keyBitLength  Size of derived keys in bits.
   */
  public OpenSSLKeyGenerator(final int keyBitLength)
  {
    this(keyBitLength, new byte[0]);
  }


  /**
   * Creates a new key generator with the given salt bytes.
   *
   * @param  keyBitLength  Size of derived keys in bits.
   * @param  saltBytes  Key generation function salt data.
   */
  public OpenSSLKeyGenerator(final int keyBitLength, final byte[] saltBytes)
  {
    this.derivedKeyLength = keyBitLength;
    this.salt = saltBytes;
  }


  /** {@inheritDoc} */
  public byte[] generate(final char[] password)
  {
    final OpenSSLPBEParametersGenerator generator =
      new OpenSSLPBEParametersGenerator();
    generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(password), salt);
    final KeyParameter p =
      (KeyParameter) generator.generateDerivedParameters(derivedKeyLength);
    return p.getKey();
  }
}
