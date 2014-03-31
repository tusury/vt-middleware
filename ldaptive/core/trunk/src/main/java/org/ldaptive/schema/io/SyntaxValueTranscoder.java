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
import org.ldaptive.schema.Syntax;

/**
 * Decodes and encodes an attribute syntax for use in an ldap attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SyntaxValueTranscoder
  extends AbstractSchemaElementValueTranscoder<Syntax>
{


  /** {@inheritDoc} */
  @Override
  public Syntax decodeStringValue(final String value)
  {
    try {
      return Syntax.parse(value);
    } catch (ParseException e) {
      throw new IllegalArgumentException(
        "Could not transcode attribute syntax",
        e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public Class<Syntax> getType()
  {
    return Syntax.class;
  }
}
