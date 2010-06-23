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
import org.bouncycastle.crypto.digests.WhirlpoolDigest;

/**
 * Implementation of Whirlpool message digest algorithm.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class Whirlpool extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an Whirlpool digest. */
  public Whirlpool()
  {
    super(new WhirlpoolDigest());
  }


  /**
   * Creates a new Whirlpool digest that may optionally be initialized with
   * random data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   */
  public Whirlpool(final boolean randomize)
  {
    super(new WhirlpoolDigest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new Whirlpool digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   */
  public Whirlpool(final byte[] salt)
  {
    super(new WhirlpoolDigest());
    setSalt(salt);
  }
}
