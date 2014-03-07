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
import org.ldaptive.schema.MatchingRule;

/**
 * Decodes and encodes a matching rule for use in an ldap attribute value.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class MatchingRuleValueTranscoder
  extends AbstractSchemaElementValueTranscoder<MatchingRule>
{


  /** {@inheritDoc} */
  @Override
  public MatchingRule decodeStringValue(final String value)
  {
    try {
      return MatchingRule.parse(value);
    } catch (ParseException e) {
      throw new IllegalArgumentException(
        "Could not transcode matching rule", e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public Class<MatchingRule> getType()
  {
    return MatchingRule.class;
  }
}
