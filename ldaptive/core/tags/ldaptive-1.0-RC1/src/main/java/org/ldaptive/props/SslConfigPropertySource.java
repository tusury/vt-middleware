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

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import org.ldaptive.ssl.SslConfig;

/**
 * Reads properties specific to {@link SslConfig} and returns an initialized
 * object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class SslConfigPropertySource
  extends AbstractPropertySource<SslConfig>
{

  /** Invoker for ssl config. */
  private static final SslConfigPropertyInvoker INVOKER =
    new SslConfigPropertyInvoker(SslConfig.class);


  /**
   * Creates a new ssl config property source using the default properties file.
   *
   * @param  config  ssl config to invoke properties on
   */
  public SslConfigPropertySource(final SslConfig config)
  {
    this(
      config,
      SslConfigPropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new ssl config property source.
   *
   * @param  config  ssl config to invoke properties on
   * @param  is  to read properties from
   */
  public SslConfigPropertySource(final SslConfig config, final InputStream is)
  {
    this(config, loadProperties(is));
  }


  /**
   * Creates a new ssl config property source.
   *
   * @param  config  ssl config to invoke properties on
   * @param  props  to read properties from
   */
  public SslConfigPropertySource(final SslConfig config, final Properties props)
  {
    this(config, PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new sssl config property source.
   *
   * @param  config  ssl config to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public SslConfigPropertySource(
    final SslConfig config,
    final PropertyDomain domain,
    final Properties props)
  {
    super(config, domain, props);
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(INVOKER);
  }


  /**
   * Returns the property names for this property source.
   *
   * @return  all property names
   */
  public static Set<String> getProperties()
  {
    return INVOKER.getProperties();
  }
}
