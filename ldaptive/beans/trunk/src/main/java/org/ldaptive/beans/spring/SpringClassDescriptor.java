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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.ldaptive.SortBehavior;
import org.ldaptive.beans.AbstractClassDescriptor;
import org.ldaptive.beans.Attribute;
import org.ldaptive.beans.AttributeValueMutator;
import org.ldaptive.beans.DnValueMutator;
import org.ldaptive.beans.Entry;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

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
  private final EvaluationContext evaluationContext;


  /**
   * Creates a new spring class descriptor.
   *
   * @param  context  to use for SPEL evaluation
   */
  public SpringClassDescriptor(final EvaluationContext context)
  {
    evaluationContext = context;
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
              return exp.getValue(evaluationContext, object, String.class);
            }

            @Override
            public void setValue(final Object object, final String value)
            {
              exp.setValue(evaluationContext, object, value);
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
              return getValues(object, String.class);
            }

            @Override
            public Collection<byte[]> getBinaryValues(final Object object)
            {
              return getValues(object, byte[].class);
            }

            @SuppressWarnings("unchecked")
            protected <T> Collection<T> getValues(
              final Object object,
              final Class<T> type)
            {
              Collection<T> values = null;
              final Object converted = exp.getValue(evaluationContext, object);
              if (converted != null) {
                if (converted.getClass().isArray()) {
                  final int length = Array.getLength(converted);
                  values = createCollection(List.class, length);
                  for (int i = 0; i < length; i++) {
                    final Object o = Array.get(converted, i);
                    values.add(
                      (T) evaluationContext.getTypeConverter().convertValue(
                        o,
                        TypeDescriptor.valueOf(o.getClass()),
                        TypeDescriptor.valueOf(type)));
                  }
                } else if (Collection.class.isAssignableFrom(
                           converted.getClass())) {
                  final Collection<?> col = (Collection<?>) converted;
                  values = createCollection(converted.getClass(), col.size());
                  for (Object o : col) {
                    values.add(
                      (T) evaluationContext.getTypeConverter().convertValue(
                        o,
                        TypeDescriptor.valueOf(o.getClass()),
                        TypeDescriptor.valueOf(type)));
                  }
                } else {
                  values = createCollection(List.class, 1);
                  values.add(
                    (T) evaluationContext.getTypeConverter().convertValue(
                      converted,
                      TypeDescriptor.valueOf(converted.getClass()),
                      TypeDescriptor.valueOf(type)));
                }
              }
              return values;
            }

            @Override
            public void setStringValues(
              final Object object, final Collection<String> values)
            {
              exp.setValue(evaluationContext, object, values);
            }

            @Override
            public void setBinaryValues(
              final Object object, final Collection<byte[]> values)
            {
              exp.setValue(evaluationContext, object, values);
            }
          });
          // CheckStyle:AnonInnerLength ON
        }
      }
    }
  }


  /**
   * Creates a best fit collection for the supplied type.
   *
   * @param  <T>  collection type
   * @param  type  of collection to create
   * @param  size  of the collection
   *
   * @return  collection
   */
  protected <T> Collection<T> createCollection(
    final Class<?> type,
    final int size)
  {
    if (type == Collection.class || List.class.isAssignableFrom(type)) {
      List<T> l;
      if (LinkedList.class.isAssignableFrom(type)) {
        l = new LinkedList<T>();
      } else {
        l = new ArrayList<T>(size);
      }
      return l;
    } else if (Set.class.isAssignableFrom(type)) {
      Set<T> s;
      if (LinkedHashSet.class.isAssignableFrom(type)) {
        s = new LinkedHashSet<T>(size);
      } else if (TreeSet.class.isAssignableFrom(type)) {
        s = new TreeSet<T>();
      } else {
        s = new HashSet<T>(size);
      }
      return s;
    } else {
      throw new IllegalArgumentException(
        "Unsupported collection type: " + type);
    }
  }
}
