/*
  $Id: RipeMD320.java 3 2008-11-11 20:58:48Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3 $
  Updated: $Date: 2008-11-11 15:58:48 -0500 (Tue, 11 Nov 2008) $
*/
package edu.vt.middleware.crypt.digest;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;

/**
 * <p><code>RipeMD320</code> contains functions for hashing data using the
 * RipeMD320 algorithm. This algorithm outputs a 320 bit hash, but offers the
 * same level of security as RipeMD320.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class RipeMD320 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an RipeMD320 digest. */
  public RipeMD320()
  {
    super(new RIPEMD320Digest());
  }


  /**
   * Creates a new RipeMD320 digest that may optionally be initialized with
   * random data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public RipeMD320(final boolean randomize)
  {
    super(new RIPEMD320Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new RipeMD320 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public RipeMD320(final byte[] salt)
  {
    super(new RIPEMD320Digest());
    setSalt(salt);
  }
}
