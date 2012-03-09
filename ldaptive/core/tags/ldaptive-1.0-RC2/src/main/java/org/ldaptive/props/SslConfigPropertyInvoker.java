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
package org.ldaptive.props;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import org.ldaptive.ssl.CredentialConfig;
import org.ldaptive.ssl.SslConfig;

/**
 * Handles properties for {@link SslConfig}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SslConfigPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new ssl config property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public SslConfigPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (CredentialConfig.class.isAssignableFrom(type)) {
        if ("null".equals(value)) {
          newValue = null;
        } else {
          if (CredentialConfigParser.isCredentialConfig(value)) {
            final CredentialConfigParser configParser =
              new CredentialConfigParser(value);
            newValue = configParser.initializeType();
          } else if (PropertyValueParser.isConfig(value)) {
            final PropertyValueParser configParser = new PropertyValueParser(
              value);
            newValue = configParser.initializeType();
          } else {
            newValue = instantiateType(SslConfig.class, value);
          }
        }
      } else if (HostnameVerifier.class.isAssignableFrom(type)) {
        newValue = createTypeFromPropertyValue(HostnameVerifier.class, value);
      } else if (HandshakeCompletedListener[].class.isAssignableFrom(type)) {
        newValue = createArrayTypeFromPropertyValue(
          HandshakeCompletedListener.class, value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
