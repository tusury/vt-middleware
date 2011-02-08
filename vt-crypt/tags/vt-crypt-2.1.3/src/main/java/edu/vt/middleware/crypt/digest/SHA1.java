/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.digest;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA1Digest;

/**
 * <p><code>SHA1</code> contains functions for hashing data using the SHA-1
 * algorithm. This algorithm outputs a 160 bit hash.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class SHA1 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an SHA1 digest. */
  public SHA1()
  {
    super(new SHA1Digest());
  }


  /**
   * Creates a new SHA1 digest that may optionally be initialized with random
   * data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public SHA1(final boolean randomize)
  {
    super(new SHA1Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new SHA1 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public SHA1(final byte[] salt)
  {
    super(new SHA1Digest());
    setSalt(salt);
  }
}
