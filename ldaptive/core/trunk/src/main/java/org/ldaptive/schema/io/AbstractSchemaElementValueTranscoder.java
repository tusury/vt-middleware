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
package org.ldaptive.schema.io;

import org.ldaptive.LdapUtils;
import org.ldaptive.io.ValueTranscoder;
import org.ldaptive.schema.SchemaElement;

/**
 * Base class for schema element value transcoders.
 *
 * @param  <T>  type of schema element
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class
AbstractSchemaElementValueTranscoder<T extends SchemaElement>
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
  public String encodeStringValue(final T value)
  {
    return value.format();
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encodeBinaryValue(final T value)
  {
    return LdapUtils.utf8Encode(encodeStringValue(value));
  }
}
