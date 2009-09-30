/*
  $Id: SHA384.java 3 2008-11-11 20:58:48Z dfisher $

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
import org.bouncycastle.crypto.digests.SHA384Digest;

/**
 * <p><code>SHA384</code> contains functions for hashing data using the SHA-384
 * algorithm. This algorithm outputs a 384 bit hash.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class SHA384 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an SHA384 digest. */
  public SHA384()
  {
    super(new SHA384Digest());
  }


  /**
   * Creates a new SHA384 digest that may optionally be initialized with random
   * data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public SHA384(final boolean randomize)
  {
    super(new SHA384Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new SHA384 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public SHA384(final byte[] salt)
  {
    super(new SHA384Digest());
    setSalt(salt);
  }
}
