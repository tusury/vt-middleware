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
package edu.vt.middleware.crypt.digest;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

/**
 * <p><code>RipeMD160</code> contains functions for hashing data using the
 * RipeMD160 algorithm. This algorithm outputs a 160 bit hash.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class RipeMD160 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an RipeMD160 digest. */
  public RipeMD160()
  {
    super(new RIPEMD160Digest());
  }


  /**
   * Creates a new RipeMD160 digest that may optionally be initialized with
   * random data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public RipeMD160(final boolean randomize)
  {
    super(new RIPEMD160Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new RipeMD160 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public RipeMD160(final byte[] salt)
  {
    super(new RIPEMD160Digest());
    setSalt(salt);
  }
}
