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
package org.ldaptive.beans.spring;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import org.ldaptive.SortBehavior;
import org.ldaptive.beans.AbstractClassDescriptor;
import org.ldaptive.beans.Attribute;
import org.ldaptive.beans.AttributeValueMutator;
import org.ldaptive.beans.DnValueMutator;
import org.ldaptive.beans.Entry;
import org.ldaptive.io.GeneralizedTimeValueTranscoder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;

/**
 * Spring implementation of a class descriptor. Uses an {@link
 * StandardEvaluationContext} with SPEL expressions to find property values.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SpringClassDescriptor extends AbstractClassDescriptor
{

  /** Context for evaluating spring expressions. */
  private final StandardEvaluationContext context;

  /** Conversion service for object conversion. */
  private final GenericConversionService conversionService;


  /**
   * Creates a new spring class descriptor.
   *
   * @param  object  to describe
   * @param  converters  to add to the conversion service
   */
  public SpringClassDescriptor(
    final Object object,
    final Converter<?, ?>... converters)
  {
    conversionService = new GenericConversionService();
    DefaultConversionService.addDefaultConverters(conversionService);
    if (converters != null) {
      for (Converter<?, ?> converter : converters) {
        conversionService.addConverter(converter);
      }
    }
    if (!conversionService.canConvert(String.class, Calendar.class)) {
      conversionService.addConverter(new Converter<String, Calendar>()
      {
        @Override
        public Calendar convert(final String s)
        {
          final GeneralizedTimeValueTranscoder transcoder = new
            GeneralizedTimeValueTranscoder();
          return transcoder.decodeStringValue(s);
        }
      });
    }
    if (!conversionService.canConvert(Calendar.class, String.class)) {
      conversionService.addConverter(new Converter<Calendar, String>()
      {
        @Override
        public String convert(final Calendar c)
        {
          final GeneralizedTimeValueTranscoder transcoder = new
            GeneralizedTimeValueTranscoder();
          return transcoder.encodeStringValue(c);
        }
      });
    }

    context = new StandardEvaluationContext(object);
    context.setTypeConverter(new StandardTypeConverter(conversionService));
  }


  /** {@inheritDoc} */
  @Override
  public void initialize(final Class<?> type)
  {
    // check for entry annotation
    final Entry entryAnnotation = AnnotationUtils.findAnnotation(
      type, Entry.class);
    if (entryAnnotation != null) {
      if (!"".equals(entryAnnotation.dn())) {
        final ExpressionParser parser = new SpelExpressionParser();
        try {
          final Expression exp = parser.parseExpression(entryAnnotation.dn());
          setDnValueMutator(new DnValueMutator() {
            @Override
            public String getValue(final Object object)
            {
              return exp.getValue(context, object, String.class);
            }

            @Override
            public void setValue(final Object object, final String value)
            {
              exp.setValue(context, object, value);
            }
          });
        } catch (SpelParseException e) {
          logger.debug(
            "Could not parse dn expression, using SimpleDnValueMutator",
            e);
          setDnValueMutator(new SimpleDnValueMutator(entryAnnotation.dn()));
        }
      }
      for (final Attribute attr : entryAnnotation.attributes()) {
        if ("".equals(attr.property()) && attr.values().length > 0) {
          addAttributeValueMutator(
            new SimpleAttributeValueMutator(
              attr.name(),
              attr.values(),
              attr.binary(),
              attr.sortBehavior()));
        } else {
          final String expr = attr.property().length() > 0 ?
            attr.property() : attr.name();
          final ExpressionParser parser = new SpelExpressionParser();
          final Expression exp = parser.parseExpression(expr);
          // CheckStyle:AnonInnerLength OFF
          addAttributeValueMutator(new AttributeValueMutator()
          {
            @Override
            public String getName()
            {
              return attr.name();
            }

            @Override
            public boolean isBinary()
            {
              return attr.binary();
            }

            @Override
            public SortBehavior getSortBehavior()
            {
              return attr.sortBehavior();
            }

            @Override
            public Collection<String> getStringValues(final Object object)
            {
              final Object converted = exp.getValue(context, object);
              if (Collection.class.isAssignableFrom(converted.getClass())) {
                final Collection<?> c = (Collection<?>) converted;
                final List<String> l = new ArrayList<String>(c.size());
                for (Object o : c) {
                  l.add(conversionService.convert(o, String.class));
                }
                return l;
              } else {
                final List<String> l = new ArrayList<String>(1);
                l.add(conversionService.convert(converted, String.class));
                return l;
              }
            }

            @Override
            public Collection<byte[]> getBinaryValues(final Object object)
            {
              final Object converted = exp.getValue(context, object);
              if (Collection.class.isAssignableFrom(converted.getClass())) {
                final Collection<?> c = (Collection<?>) converted;
                final List<byte[]> l = new ArrayList<byte[]>(c.size());
                for (Object o : c) {
                  l.add(conversionService.convert(o, byte[].class));
                }
                return l;
              } else {
                final List<byte[]> l = new ArrayList<byte[]>(1);
                l.add(conversionService.convert(converted, byte[].class));
                return l;
              }
            }

            @Override
            public void setStringValues(
              final Object object, final Collection<String> values)
            {
              exp.setValue(context, object, values);
            }

            @Override
            public void setBinaryValues(
              final Object object, final Collection<byte[]> values)
            {
              exp.setValue(context, object, values);
            }
          });
          // CheckStyle:AnonInnerLength ON
        }
      }
    }
  }
}
