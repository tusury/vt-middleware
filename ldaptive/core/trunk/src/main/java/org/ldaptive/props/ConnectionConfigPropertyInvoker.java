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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.ldaptive.Credential;
import org.ldaptive.control.RequestControl;
import org.ldaptive.provider.Provider;
import org.ldaptive.sasl.SaslConfig;
import org.ldaptive.ssl.CredentialConfigParser;
import org.ldaptive.ssl.SSLContextInitializer;

/**
 * Handles properties for {@link org.ldaptive.ConnectionConfig}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectionConfigPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new connection config property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public ConnectionConfigPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (SSLSocketFactory.class.isAssignableFrom(type)) {
        if ("null".equals(value)) {
          newValue = null;
        } else {
          // use a credential reader to configure key/trust material
          if (CredentialConfigParser.isCredentialConfig(value)) {
            final CredentialConfigParser configParser =
              new CredentialConfigParser(value);
            newValue = instantiateType(
              SSLSocketFactory.class,
              configParser.getSslSocketFactoryClassName());

            final Object credentialConfig = configParser.initializeType();
            try {
              // set the SSL context initializer using the credential config
              invokeMethod(
                newValue.getClass().getMethod(
                  "setSSLContextInitializer",
                  SSLContextInitializer.class),
                newValue,
                invokeMethod(
                  credentialConfig.getClass().getMethod(
                    "createSSLContextInitializer",
                    new Class<?>[0]),
                  credentialConfig,
                  null));
              // initialize the TLS socket factory.
              invokeMethod(
                newValue.getClass().getMethod("initialize", new Class<?>[0]),
                newValue,
                null);
            } catch (NoSuchMethodException e) {
              throw new IllegalArgumentException(e);
            }
            // use a standard config to initialize the socket factory
          } else if (PropertyValueParser.isConfig(value)) {
            final PropertyValueParser configParser = new PropertyValueParser(
              value);
            newValue = configParser.initializeType();
          } else {
            newValue = instantiateType(SSLSocketFactory.class, value);
          }
        }
      } else if (Provider.class.isAssignableFrom(type)) {
        newValue = createTypeFromPropertyValue(Provider.class, value);
      } else if (SaslConfig.class.isAssignableFrom(type)) {
        if ("null".equals(value)) {
          newValue = null;
        } else {
          if (PropertyValueParser.isParamsOnlyConfig(value)) {
            final PropertyValueParser configParser = new PropertyValueParser(
              value,
              "org.ldaptive.sasl.SaslConfig");
            newValue = configParser.initializeType();
          } else if (PropertyValueParser.isConfig(value)) {
            final PropertyValueParser configParser = new PropertyValueParser(
              value);
            newValue = configParser.initializeType();
          } else {
            newValue = instantiateType(SaslConfig.class, value);
          }
        }
      } else if (RequestControl[].class.isAssignableFrom(type)) {
        newValue = createArrayTypeFromPropertyValue(
          RequestControl.class,
          value);
      } else if (HostnameVerifier.class.isAssignableFrom(type)) {
        newValue = createTypeFromPropertyValue(HostnameVerifier.class, value);
      } else if (Credential.class.isAssignableFrom(type)) {
        newValue = new Credential(value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
