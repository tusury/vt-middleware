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
package org.ldaptive.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.ldaptive.SortBehavior;
import org.ldaptive.io.ValueTranscoder;

/**
 * Annotation to describe LDAP attribute data on a bean.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Attribute
{

  /** Attribute name. */
  String name() default "";

  /** Attribute values. Mutually exclusive with {@link #property()}. */
  String[] values() default {};

  /** Name of the method or field that maps to this attribute. Mutually
   * exclusive with {@link #values()}. */
  String property() default "";

  /** Whether this attribute is binary. Default is {@value}. */
  boolean binary() default false;

  /** Sort behavior for this attribute. Default is {@value}. */
  SortBehavior sortBehavior() default SortBehavior.UNORDERED;

  /** Transcoder for this attribute. */
  Class<? extends ValueTranscoder<?>> transcoder()
    default NoValueTranscoder.class;


  /** Marker class which indicates no transcoder. */
  static final class NoValueTranscoder implements ValueTranscoder<Object>
  {


    /** {@inheritDoc} */
    @Override
    public Object decodeStringValue(final String value)
    {
      throw new UnsupportedOperationException("Method not supported");
    }


    /** {@inheritDoc} */
    @Override
    public Object decodeBinaryValue(final byte[] value)
    {
      throw new UnsupportedOperationException("Method not supported");
    }


    /** {@inheritDoc} */
    @Override
    public String encodeStringValue(final Object value)
    {
      throw new UnsupportedOperationException("Method not supported");
    }


    /** {@inheritDoc} */
    @Override
    public byte[] encodeBinaryValue(final Object value)
    {
      throw new UnsupportedOperationException("Method not supported");
    }


    /** {@inheritDoc} */
    @Override
    public Class<Object> getType()
    {
      throw new UnsupportedOperationException("Method not supported");
    }
  }
}
