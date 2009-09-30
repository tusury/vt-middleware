/*
  $Id: MD4.java 3 2008-11-11 20:58:48Z dfisher $

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
import edu.vt.middleware.crypt.CryptException;
import org.bouncycastle.crypto.digests.MD4Digest;

/**
 * <p><code>MD4</code> contains functions for hashing data using the MD4
 * algorithm. This algorithm outputs a 128 bit hash.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */

public class MD4 extends DigestAlgorithm
{

  /** Creates an uninitialized instance of an MD4 digest. */
  public MD4()
  {
    super(new MD4Digest());
  }


  /**
   * Creates a new MD4 digest that may optionally be initialized with random
   * data.
   *
   * @param  randomize  True to randomize initial state of digest, false
   * otherwise.
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or the provider is not available in the environment
   */
  public MD4(final boolean randomize)
    throws CryptException
  {
    super(new MD4Digest());
    if (randomize) {
      setRandomProvider(new SecureRandom());
      setSalt(getRandomSalt());
    }
  }


  /**
   * Creates a new MD4 digest and initializes it with the given salt.
   *
   * @param  salt  Salt data used to initialize digest computation.
   *
   * @throws  CryptException  if the algorithm is not available from any
   * provider or the provider is not available in the environment
   */
  public MD4(final byte[] salt)
    throws CryptException
  {
    super(new MD4Digest());
    setSalt(salt);
  }
}
