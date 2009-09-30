/*
  $Id: MD2.java 3 2008-11-11 20:58:48Z dfisher $

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
import org.bouncycastle.crypto.digests.MD2Digest;

/**
 * <p><code>MD2</code> contains functions for hashing data using the MD2
 * algorithm. This algorithm outputs a 128 bit hash.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class MD2 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an MD2 digest. */
  public MD2()
  {
    super(new MD2Digest());
  }


  /**
   * Creates a new MD2 digest that may optionally be initialized with random
   * data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public MD2(final boolean randomize)
  {
    super(new MD2Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new MD2 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public MD2(final byte[] salt)
  {
    super(new MD2Digest());
    setSalt(salt);
  }
}
