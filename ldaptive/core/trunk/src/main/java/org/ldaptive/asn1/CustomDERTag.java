/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.asn1;

/**
 * Describes the tag of an application-specific, context-specific, or private
 * DER type where the tag name may be specified for clarity in application code.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CustomDERTag extends AbstractDERTag
{
  /** Tag name. */
  private final String tagName;


  /**
   * Creates a new custom DER tag.
   *
   * @param  number  of the tag
   * @param  name  of the tag
   * @param  isConstructed  whether this tag is primitive or constructed
   */
  public CustomDERTag(
      final int number,
      final String name,
      final boolean isConstructed)
  {
    super(number, isConstructed);
    tagName = name;
  }


  /** {@inheritDoc} */
  @Override
  public String name()
  {
    return tagName;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return String.format("%s(%s)", name(), getTagNo());
  }
}
