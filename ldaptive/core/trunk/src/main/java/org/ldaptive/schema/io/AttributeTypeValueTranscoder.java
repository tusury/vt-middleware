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

import java.text.ParseException;
import org.ldaptive.schema.AttributeType;

/**
 * Decodes and encodes an attribute type for use in an ldap attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AttributeTypeValueTranscoder
  extends AbstractSchemaElementValueTranscoder<AttributeType>
{


  /** {@inheritDoc} */
  @Override
  public AttributeType decodeStringValue(final String value)
  {
    try {
      return AttributeType.parse(value);
    } catch (ParseException e) {
      throw new IllegalArgumentException(
        "Could not transcode attribute type",
        e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public Class<AttributeType> getType()
  {
    return AttributeType.class;
  }
}
