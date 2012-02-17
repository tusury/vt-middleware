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
package org.ldaptive.servlets;

import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.LdapResult;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.pool.ConnectionPoolType;
import org.ldaptive.pool.PoolException;
import org.ldaptive.pool.PooledConnectionFactory;
import org.ldaptive.props.PooledConnectionFactoryPropertySource;
import org.ldaptive.props.PropertySource.PropertyDomain;
import org.ldaptive.props.SearchRequestPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for ldap search servlets.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractServlet extends HttpServlet
{

  /** Type of pool used, value is {@value}. */
  public static final String POOL_TYPE = "poolType";

  /** serial version uid. */
  private static final long serialVersionUID = -6245486456044663458L;

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Connections for searching. */
  private PooledConnectionFactory connectionFactory;

  /** Search request for storing search properties. */
  private SearchRequest searchRequest;


  /**
   * Initialize this servlet.
   *
   * @param  config  servlet configuration
   *
   * @throws  ServletException  if an error occurs
   */
  public void init(final ServletConfig config)
    throws ServletException
  {
    super.init(config);

    searchRequest = new SearchRequest();
    final SearchRequestPropertySource srSource =
      new SearchRequestPropertySource(searchRequest, createProperties(config));
    srSource.initialize();
    logger.debug("searchRequest = {}", searchRequest);

    connectionFactory = new PooledConnectionFactory();
    final PooledConnectionFactoryPropertySource cfPropSource =
      new PooledConnectionFactoryPropertySource(
        connectionFactory, createProperties(config));
    cfPropSource.setPoolType(
      ConnectionPoolType.valueOf(getInitParameter(POOL_TYPE)));
    cfPropSource.initialize();
    logger.debug("connectionFactory = {}", connectionFactory);
  }


  /**
   * Returns context specific properties based on the supplied JAAS options.
   *
   * @param  options  to read properties from
   *
   * @return  properties
   */
  protected static Properties createProperties(final ServletConfig config)
  {
    final Properties p = new Properties();
    final Enumeration<?> e = config.getInitParameterNames();
    while (e.hasMoreElements()) {
      final String name = (String) e.nextElement();
      // if property name contains a dot, it isn't an ldaptive property
      // else add the domain to the ldaptive properties
      if (name.indexOf(".") != -1) {
        p.setProperty(name, config.getInitParameter(name));
      } else {
        p.setProperty(
          PropertyDomain.LDAP.value() + name,
          config.getInitParameter(name));
      }
    }
    return p;
  }


  /**
   * Performs an ldap search uses this servlet's connection pool.
   *
   * @param  query  to execute
   * @param  attrs  to return
   *
   * @return  ldap result
   *
   * @throws  LdapException  if an error occurs
   */
  protected LdapResult search(final String query, final String[] attrs)
    throws LdapException
  {
    LdapResult result = null;
    if (query != null) {
      try {
        Connection conn = null;
        try {
          conn = connectionFactory.getConnection();

          final SearchOperation search = new SearchOperation(conn);
          final SearchRequest sr = SearchRequest.newSearchRequest(
            searchRequest);
          sr.setSearchFilter(new SearchFilter(query));
          sr.setReturnAttributes(attrs);
          result = search.execute(sr).getResult();
        } finally {
          if (conn != null) {
            conn.close();
          }
        }
      } catch (PoolException e) {
        logger.error("Error using LDAP pool", e);
      }
    }
    return result;
  }


  /**
   * Called by the servlet container to indicate to a servlet that the servlet
   * is being taken out of service.
   */
  public void destroy()
  {
    try {
      connectionFactory.getConnectionPool().close();
    } finally {
      super.destroy();
    }
  }
}
