/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.io;

/**
 * Decodes and encodes a string for use in an ldap attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class StringValueTranscoder extends AbstractStringValueTranscoder<String>
{


  /** {@inheritDoc} */
  @Override
  public String decodeStringValue(final String value)
  {
    return value;
  }


  /** {@inheritDoc} */
  @Override
  public String encodeStringValue(final String value)
  {
    return value;
  }


  /** {@inheritDoc} */
  @Override
  public Class<String> getType()
  {
    return String.class;
  }
}
