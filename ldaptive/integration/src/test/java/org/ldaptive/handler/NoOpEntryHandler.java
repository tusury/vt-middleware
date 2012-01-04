/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.handler;

import org.ldaptive.LdapUtil;

/**
 * Entry handler that does nothing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoOpEntryHandler extends AbstractLdapEntryHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 887;


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(HASH_CODE_SEED, (Object) null);
  }
}
