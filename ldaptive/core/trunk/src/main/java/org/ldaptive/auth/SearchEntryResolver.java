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

import org.ldaptive.Connection;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.ConnectionFactoryManager;
import org.ldaptive.LdapException;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchResult;

/**
 * Looks up the LDAP entry associated with a user. If a connection factory is
 * configured it will be used to perform the search for user. The connection
 * will be opened and closed for each resolution. If no connection factory is
 * configured the search will occur using the connection that the bind was
 * attempted on.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchEntryResolver extends AbstractSearchEntryResolver
  implements ConnectionFactoryManager
{

  /** Connection factory. */
  private ConnectionFactory factory;


  /** Default constructor. */
  public SearchEntryResolver() {}


  /**
   * Creates a new search entry resolver.
   *
   * @param  attrs  to return
   */
  public SearchEntryResolver(final String... attrs)
  {
    setReturnAttributes(attrs);
  }


  /**
   * Creates a new search entry resolver.
   *
   * @param  cf  connection factory
   */
  public SearchEntryResolver(final ConnectionFactory cf)
  {
    setConnectionFactory(cf);
  }


  /**
   * Creates a new search entry resolver.
   *
   * @param  cf  connection factory
   * @param  attrs  to return
   */
  public SearchEntryResolver(final ConnectionFactory cf, final String... attrs)
  {
    setConnectionFactory(cf);
    setReturnAttributes(attrs);
  }


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory getConnectionFactory()
  {
    return factory;
  }


  /** {@inheritDoc} */
  @Override
  public void setConnectionFactory(final ConnectionFactory cf)
  {
    factory = cf;
  }


  /** {@inheritDoc} */
  @Override
  public SearchResult performLdapSearch(
    final Connection conn, final AuthenticationCriteria ac)
    throws LdapException
  {
    if (factory == null) {
      final SearchOperation search = new SearchOperation(conn);
      return search.execute(
        createSearchRequest(ac, getReturnAttributes())).getResult();
    } else {
      Connection factoryConn = null;
      try {
        factoryConn = factory.getConnection();
        factoryConn.open();
        final SearchOperation search = new SearchOperation(factoryConn);
        return search.execute(
          createSearchRequest(ac, getReturnAttributes())).getResult();
      } finally {
        if (factoryConn != null) {
          factoryConn.close();
        }
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
        getReturnAttributes());
  }
}
