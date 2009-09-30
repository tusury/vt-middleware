/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.digest;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.MD5Digest;

/**
 * <p><code>MD5</code> contains functions for hashing data using the MD5
 * algorithm. This algorithm outputs a 128 bit hash.</p>
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class MD5 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an MD5 digest. */
  public MD5()
  {
    super(new MD5Digest());
  }


  /**
   * Creates a new MD5 digest that may optionally be initialized with random
   * data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public MD5(final boolean randomize)
  {
    super(new MD5Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new MD5 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public MD5(final byte[] salt)
  {
    super(new MD5Digest());
    setSalt(salt);
  }
}
