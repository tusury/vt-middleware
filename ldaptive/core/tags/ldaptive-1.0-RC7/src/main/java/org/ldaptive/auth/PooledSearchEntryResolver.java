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
package org.ldaptive.auth;

import java.util.Arrays;
import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchResult;
import org.ldaptive.pool.PooledConnectionFactory;
import org.ldaptive.pool.PooledConnectionFactoryManager;

/**
 * Looks up the LDAP entry associated with a user using a pool of LDAP
 * connections. Resolution will not occur using the connection that the user
 * attempted to bind on.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PooledSearchEntryResolver extends AbstractSearchEntryResolver
  implements PooledConnectionFactoryManager
{

  /** Connection factory. */
  private PooledConnectionFactory factory;


  /** Default constructor. */
  public PooledSearchEntryResolver() {}


  /**
   * Creates a new pooled search entry resolver.
   *
   * @param  cf  connection factory
   */
  public PooledSearchEntryResolver(final PooledConnectionFactory cf)
  {
    setConnectionFactory(cf);
  }


  /**
   * Creates a new pooled search entry resolver.
   *
   * @param  cf  connection factory
   * @param  attrs  to return
   */
  public PooledSearchEntryResolver(
    final PooledConnectionFactory cf,
    final String... attrs)
  {
    setConnectionFactory(cf);
    setReturnAttributes(attrs);
  }


  /** {@inheritDoc} */
  @Override
  public PooledConnectionFactory getConnectionFactory()
  {
    return factory;
  }


  /** {@inheritDoc} */
  @Override
  public void setConnectionFactory(final PooledConnectionFactory cf)
  {
    factory = cf;
  }


  /** {@inheritDoc} */
  @Override
  protected SearchResult performLdapSearch(
    final Connection conn,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    Connection pooledConn = null;
    try {
      pooledConn = factory.getConnection();

      final SearchOperation op = createSearchOperation(conn);
      return
        op.execute(createSearchRequest(ac, getReturnAttributes())).getResult();
    } finally {
      if (pooledConn != null) {
        pooledConn.close();
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::factory=%s, returnAttributes=%s]",
        getClass().getName(),
        hashCode(),
        factory,
        Arrays.toString(getReturnAttributes()));
  }
}
