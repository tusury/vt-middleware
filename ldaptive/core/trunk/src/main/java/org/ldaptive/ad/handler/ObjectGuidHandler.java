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
package org.ldaptive.ad.handler;

import org.ldaptive.LdapUtils;
import org.ldaptive.ad.GlobalIdentifier;

/**
 * Processes the objectGuid attribute by converting it from binary to it's
 * string form.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ObjectGuidHandler extends AbstractBinaryAttributeHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 1823;

  /** objectGuid attribute name. */
  private static final String ATTRIBUTE_NAME = "objectGUID";


  /**
   * Creates a new object guid handler.
   */
  public ObjectGuidHandler()
  {
    setAttributeName(ATTRIBUTE_NAME);
  }


  /** {@inheritDoc} */
  @Override
  protected String convertValue(final byte[] value)
  {
    return GlobalIdentifier.toString(value);
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtils.computeHashCode(HASH_CODE_SEED, getAttributeName());
  }
}
