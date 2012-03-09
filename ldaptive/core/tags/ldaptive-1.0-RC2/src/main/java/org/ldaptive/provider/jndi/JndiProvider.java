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
package org.ldaptive.provider.jndi;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;
import org.ldaptive.ssl.TLSSocketFactory;
import org.ldaptive.ssl.ThreadLocalTLSSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exposes a connection factory for creating connections with JNDI.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiProvider implements Provider<JndiProviderConfig>
{

  /**
   * The value of this property is a fully qualified class name of the factory
   * class which creates the initial context for the LDAP service provider. The
   * value of this constant is {@value}.
   */
  public static final String CONTEXT_FACTORY = "java.naming.factory.initial";

  /**
   * The value of this property is a string that specifies the protocol version
   * for the provider. The value of this constant is {@value}.
   */
  public static final String VERSION = "java.naming.ldap.version";

  /**
   * The value of this property is a URL string that specifies the hostname and
   * port number of the LDAP server, and the root distinguished name of the
   * naming context to use. The value of this constant is {@value}.
   */
  public static final String PROVIDER_URL = "java.naming.provider.url";

  /**
   * The value of this property is a string that specifies the security protocol
   * for the provider to use. The value of this constant is {@value}.
   */
  public static final String PROTOCOL = "java.naming.security.protocol";

  /**
   * The value of this property is a string identifying the class name of a
   * socket factory. The value of this constant is {@value}.
   */
  public static final String SOCKET_FACTORY = "java.naming.ldap.factory.socket";

  /**
   * The value of this property is a string that specifies the time in
   * milliseconds that a connection attempt will abort if the connection cannot
   * be made. The value of this constant is {@value}.
   */
  public static final String CONNECT_TIMEOUT =
    "com.sun.jndi.ldap.connect.timeout";

  /**
   * The value of this property is a string that specifies the time in
   * milliseconds that an operation will abort if a response is not received.
   * The value of this constant is {@value}.
   */
  public static final String READ_TIMEOUT = "com.sun.jndi.ldap.read.timeout";

  /**
   * The value of this property is a java.io.OutputStream object into which a
   * hexadecimal dump of the incoming and outgoing LDAP ASN.1 BER packets is
   * written. The value of this constant is {@value}.
   */
  public static final String TRACE = "com.sun.jndi.ldap.trace.ber";

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Provider configuration. */
  private JndiProviderConfig config = new JndiProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory<JndiProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    ConnectionFactory<JndiProviderConfig> cf = null;
    config.makeImmutable();
    if (cc.getUseStartTLS()) {
      // hostname verification always occurs for startTLS after the handshake
      SSLSocketFactory factory = config.getSslSocketFactory();
      if (factory == null &&
          cc.getSslConfig() != null && !cc.getSslConfig().isEmpty()) {
        final TLSSocketFactory sf = new TLSSocketFactory();
        sf.setSslConfig(cc.getSslConfig());
        try {
          sf.initialize();
        } catch (GeneralSecurityException e) {
          throw new IllegalArgumentException(e);
        }
        factory = sf;
      }
      cf = new JndiStartTLSConnectionFactory(
        cc.getLdapUrl(),
        createEnvironment(cc, null),
        factory,
        config.getHostnameVerifier());
    } else {
      SSLSocketFactory factory = config.getSslSocketFactory();
      if (factory == null &&
          (cc.getUseSSL() ||
           cc.getLdapUrl().toLowerCase().contains("ldaps://"))) {
        // LDAPS hostname verification does not occur by default
        // set a default hostname verifier
        final LdapURL ldapUrl = new LdapURL(cc.getLdapUrl());
        factory = ThreadLocalTLSSocketFactory.getHostnameVerifierFactory(
          cc.getSslConfig(), ldapUrl.getEntriesAsString());
      }
      cf = new JndiConnectionFactory(
        cc.getLdapUrl(),
        createEnvironment(
          cc, factory != null ? factory.getClass().getName() : null));
    }
    cf.setProviderConfig(config);
    return cf;
  }


  /** {@inheritDoc} */
  @Override
  public JndiProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final JndiProviderConfig jpc)
  {
    config = jpc;
  }


  /** {@inheritDoc} */
  @Override
  public JndiProvider newInstance()
  {
    return new JndiProvider();
  }


  /**
   * Returns the configuration environment for a JNDI ldap context using the
   * properties found in the supplied connection config.
   *
   * @param  cc  connection config
   * @param  factory  class name of the socket factory to use for LDAPS
   *
   * @return  JNDI ldap context environment
   */
  protected Map<String, Object> createEnvironment(
    final ConnectionConfig cc, final String factory)
  {
    final Map<String, Object> env = new HashMap<String, Object>();
    env.put(CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(VERSION, "3");
    if (cc.getUseSSL()) {
      env.put(PROTOCOL, "ssl");
    }
    if (factory != null &&
        (cc.getUseSSL() ||
         cc.getLdapUrl().toLowerCase().contains("ldaps://"))) {
      env.put(JndiProvider.SOCKET_FACTORY, factory);
    }
    if (cc.getConnectTimeout() > 0) {
      env.put(CONNECT_TIMEOUT, Long.toString(cc.getConnectTimeout()));
    }
    if (cc.getResponseTimeout() > 0) {
      env.put(READ_TIMEOUT, Long.toString(cc.getResponseTimeout()));
    }
    if (config.getTracePackets() != null) {
      env.put(JndiProvider.TRACE, config.getTracePackets());
    }
    if (!config.getProperties().isEmpty()) {
      for (Map.Entry<String, Object> entry :
           config.getProperties().entrySet()) {
        env.put(entry.getKey(), entry.getValue());
      }
    }
    return env;
  }
}
