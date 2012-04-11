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
package org.ldaptive.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.ldaptive.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a basic implementation for other connection factories to inherit.
 *
 * @param  <T>  type of provider config for this connection factory
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractConnectionFactory<T extends ProviderConfig>
  implements ConnectionFactory<T>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** LDAP URL for connections. */
  private final String ldapUrl;

  /** Provider configuration. */
  private final T providerConfig;

  /** Number of connections made. */
  private ConnectionCount connectionCount = new ConnectionCount();


  /**
   * Creates a new abstract connection factory. Once invoked the supplied
   * provider config is made immutable. See
   * {@link ProviderConfig#makeImmutable()}.
   *
   * @param  url  of the ldap to connect to
   * @param  config  provider configuration
   */
  public AbstractConnectionFactory(final String url, final T config)
  {
    if (url == null) {
      throw new IllegalArgumentException("LDAP URL cannot be null");
    }
    ldapUrl = url;
    providerConfig = config;
    providerConfig.makeImmutable();
  }


  /** {@inheritDoc} */
  @Override
  public T getProviderConfig()
  {
    return providerConfig;
  }


  /**
   * Returns the connection count.
   *
   * @return  connection count
   */
  protected ConnectionCount getConnectionCount()
  {
    return connectionCount;
  }


  /**
   * Sets the connection count.
   *
   * @param  cc  connection count
   */
  protected void setConnectionCount(final ConnectionCount cc)
  {
    connectionCount = cc;
  }


  /** {@inheritDoc} */
  @Override
  public Connection create()
    throws LdapException
  {
    LdapException lastThrown = null;
    final String[] urls = parseLdapUrl(
      ldapUrl, providerConfig.getConnectionStrategy());
    Connection conn = null;
    for (String url : urls) {
      try {
        logger.trace(
          "[{}] Attempting connection to {} for strategy {}",
          new Object[] {
            connectionCount,
            url,
            providerConfig.getConnectionStrategy(),
          });
        conn = createInternal(url);
        connectionCount.incrementCount();
        lastThrown = null;
        break;
      } catch (ConnectionException e) {
        lastThrown = e;
        logger.debug("Error connecting to LDAP URL: {}", url, e);
      }
    }
    if (lastThrown != null) {
      throw lastThrown;
    }
    return conn;
  }


  /**
   * Create the provider connection and prepare the connection for use.
   *
   * @param  url  to connect to
   *
   * @return  provider connection
   *
   * @throws  LdapException  if a connection cannot be established
   */
  protected abstract Connection createInternal(final String url)
    throws LdapException;


  /**
   * Parses the supplied ldap url and splits it into separate URLs if it is
   * space delimited.
   *
   * @param  url  to parse
   * @param  strategy  of ordered array to return
   *
   * @return  array of ldap URLs
   */
  protected String[] parseLdapUrl(
    final String url,
    final ConnectionStrategy strategy)
  {
    String[] urls = null;
    if (strategy == ConnectionStrategy.DEFAULT) {
      urls = new String[] {url};
    } else if (strategy == ConnectionStrategy.ACTIVE_PASSIVE) {
      final List<String> l = splitLdapUrl(url);
      urls = l.toArray(new String[l.size()]);
    } else if (strategy == ConnectionStrategy.ROUND_ROBIN) {
      final List<String> l = splitLdapUrl(url);
      for (int i = 0; i < connectionCount.getCount() % l.size(); i++) {
        l.add(l.remove(0));
      }
      urls = l.toArray(new String[l.size()]);
    } else if (strategy == ConnectionStrategy.RANDOM) {
      final List<String> l = splitLdapUrl(url);
      Collections.shuffle(l);
      urls = l.toArray(new String[l.size()]);
    }
    return urls;
  }


  /**
   * Takes a space delimited string of URLs and returns a list of URLs.
   *
   * @param  url  to split
   *
   * @return  list of URLs
   */
  private List<String> splitLdapUrl(final String url)
  {
    final List<String> urls = new ArrayList<String>();
    if (url != null) {
      final StringTokenizer st = new StringTokenizer(url);
      while (st.hasMoreTokens()) {
        urls.add(st.nextToken());
      }
    } else {
      urls.add(null);
    }
    return urls;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::ldapUrl=%s, config=%s, connectionCount=%s]",
        getClass().getName(),
        hashCode(),
        ldapUrl,
        providerConfig,
        connectionCount);
  }


  /** Provides an object to track the connection count. */
  private class ConnectionCount
  {

    /** connection count. */
    private int count;


    /**
     * Returns the connection count.
     *
     * @return  count
     */
    public int getCount()
    {
      return count;
    }


    /** Increments the connection count. */
    public void incrementCount()
    {
      count++;
      // reset the count if it exceeds the size of an integer
      if (count < 0) {
        count = 0;
      }
    }


    /** {@inheritDoc} */
    @Override
    public String toString()
    {
      return Integer.toString(count);
    }
  }
}
