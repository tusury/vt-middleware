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

import org.ldaptive.LdapUtils;

/**
 * Value transcoder which decodes and encodes to a String and therefore the
 * binary methods simply delegate to the string methods.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractStringValueTranscoder<T>
  implements ValueTranscoder<T>
{


  /** {@inheritDoc} */
  @Override
  public T decodeBinaryValue(final byte[] value)
  {
    return decodeStringValue(LdapUtils.utf8Encode(value));
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encodeBinaryValue(final T value)
  {
    return LdapUtils.utf8Encode(encodeStringValue(value));
  }
}
