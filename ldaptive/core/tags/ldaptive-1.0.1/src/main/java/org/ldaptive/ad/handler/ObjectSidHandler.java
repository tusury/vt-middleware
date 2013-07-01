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
import org.ldaptive.ad.SecurityIdentifier;

/**
 * Processes an objectSid attribute by converting it from binary to it's string
 * form. See http://msdn.microsoft.com/en-us/library/windows/desktop/
 * ms679024(v=vs.85).aspx.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ObjectSidHandler extends AbstractBinaryAttributeHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 1801;

  /** objectSid attribute name. */
  private static final String ATTRIBUTE_NAME = "objectSid";


  /** Creates a new object sid handler. */
  public ObjectSidHandler()
  {
    setAttributeName(ATTRIBUTE_NAME);
  }


  /**
   * Creates a new object sid handler.
   *
   * @param  attrName  name of the attribute which is encoded as an objectSid
   */
  public ObjectSidHandler(final String attrName)
  {
    setAttributeName(attrName);
  }


  /** {@inheritDoc} */
  @Override
  protected String convertValue(final byte[] value)
  {
    return SecurityIdentifier.toString(value);
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtils.computeHashCode(HASH_CODE_SEED, getAttributeName());
  }
}
