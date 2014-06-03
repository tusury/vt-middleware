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

import org.ldaptive.io.AbstractStringValueTranscoder;
import org.ldaptive.schema.SchemaElement;

/**
 * Base class for schema element value transcoders.
 *
 * @param  <T>  type of schema element
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class
AbstractSchemaElementValueTranscoder<T extends SchemaElement>
  extends AbstractStringValueTranscoder<T>
{


  /** {@inheritDoc} */
  @Override
  public String encodeStringValue(final T value)
  {
    return value.format();
  }
}
