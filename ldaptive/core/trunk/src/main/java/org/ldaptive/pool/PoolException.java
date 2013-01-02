/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.pool;

import org.ldaptive.LdapException;

/**
 * Base exception thrown when a pool operation fails.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PoolException extends LdapException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 6320399208563015506L;


  /**
   * Creates a new pool exception.
   *
   * @param  msg  describing this exception
   */
  public PoolException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new pool exception.
   *
   * @param  e  pooling specific exception
   */
  public PoolException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new pool exception.
   *
   * @param  msg  describing this exception
   * @param  e  pooling specific exception
   */
  public PoolException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
