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
package org.ldaptive.provider;

/**
 * Interface to describe various connection strategies. Each strategy returns an
 * ordered list of URLs to attempt when opening a connection.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ConnectionStrategy
{

  /** default strategy. */
  ConnectionStrategy DEFAULT =
    new ConnectionStrategies.DefaultConnectionStrategy();

  /** active-passive strategy. */
  ConnectionStrategy ACTIVE_PASSIVE =
    new ConnectionStrategies.ActivePassiveConnectionStrategy();

  /** round robin strategy. */
  ConnectionStrategy ROUND_ROBIN =
    new ConnectionStrategies.RoundRobinConnectionStrategy();

  /** random strategy. */
  ConnectionStrategy RANDOM =
    new ConnectionStrategies.RandomConnectionStrategy();


  /**
   * Parses the supplied ldap url and splits it into separate URLs if it is
   * space delimited.
   *
   * @param  url  to parse
   * @param  connectionCount  number of times the provider connection factory
   * has created a connection
   *
   * @return  array of ldap URLs
   */
  String[] parseLdapUrl(String url, int connectionCount);
}
