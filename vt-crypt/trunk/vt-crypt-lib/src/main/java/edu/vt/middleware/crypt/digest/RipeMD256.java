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
import org.bouncycastle.crypto.digests.RIPEMD256Digest;

/**
 * <p><code>RipeMD256</code> contains functions for hashing data using the
 * RipeMD256 algorithm. This algorithm outputs a 256 bit hash, but offers the
 * same level of security as RipeMD128.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class RipeMD256 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an RipeMD256 digest. */
  public RipeMD256()
  {
    super(new RIPEMD256Digest());
  }


  /**
   * Creates a new RipeMD256 digest that may optionally be initialized with
   * random data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public RipeMD256(final boolean randomize)
  {
    super(new RIPEMD256Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new RipeMD256 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public RipeMD256(final byte[] salt)
  {
    super(new RIPEMD256Digest());
    setSalt(salt);
  }
}
