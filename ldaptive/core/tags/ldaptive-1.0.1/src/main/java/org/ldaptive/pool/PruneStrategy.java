/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.pool;

/**
 * Provides an interface for pruning connections from the pool.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface PruneStrategy
{


  /**
   * Invoked to determine whether a connection should be pruned from the pool.
   *
   * @param  conn  that is available for pruning
   *
   * @return  whether the connection should be pruned
   */
  boolean prune(PooledConnectionProxy conn);


  /**
   * Returns the number of statistics to store for this prune strategy. See
   * {@link PooledConnectionStatistics}.
   *
   * @return  number of statistics to store
   */
  int getStatisticsSize();


  /**
   * Returns the interval at which the prune task will be executed in seconds.
   *
   * @return  prune period in seconds
   */
  long getPrunePeriod();
}
