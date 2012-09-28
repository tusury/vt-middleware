/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import org.ldaptive.handler.OperationExceptionHandler;

/**
 * Provides a wrapper class for testing {@link OperationExceptionHandler}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RetrySearchOperation extends SearchOperation
{

  /** serial version uid. */
  private static final long serialVersionUID = 4247614583961731974L;

  /** exception to rethrow. */
  private final LdapException ex;

  /** whether to perform retries. */
  private boolean allowRetry = true;

  /** retry counter. */
  private int retryCount;

  /** run time counter. */
  private long runTime;

  /** stop counter. */
  private int stopCount;


  /**
   * Creates a new retry search operation.
   *
   * @param  c  connection
   * @param  e  ldap exception
   */
  public RetrySearchOperation(final Connection c, final LdapException e)
  {
    super(c);
    ex = e;
    setOperationExceptionHandler(new RetryExceptionHandler());
  }


  /**
   * Sets whether to allow retry.
   *
   * @param  b  whether to allow retry
   */
  public void setAllowRetry(final boolean b)
  {
    allowRetry = b;
  }


  /**
   * Returns the retry count.
   *
   * @return  retry count
   */
  public int getRetryCount()
  {
    return retryCount;
  }


  /**
   * Returns the run time counter.
   *
   * @return  run time
   */
  public long getRunTime()
  {
    return runTime;
  }


  /**
   * Sets the count at which to stop retries.
   *
   * @param  i  stop count
   */
  public void setStopCount(final int i)
  {
    stopCount = i;
  }


  /** Resets all the counters. */
  public void reset()
  {
    retryCount = 0;
    runTime = 0;
    stopCount = 0;
  }


  /**
   * See {@link ReopenOperationExceptionHandler#setRetry(int)}.
   *
   * @param  i  to set
   */
  public void setReopenRetry(final int i)
  {
    ((ReopenOperationExceptionHandler)
      getOperationExceptionHandler()).setRetry(i);
  }


  /**
   * See {@link ReopenOperationExceptionHandler#setRetryWait(long)}.
   *
   * @param  l  to set
   */
  public void setReopenRetryWait(final long l)
  {
    ((ReopenOperationExceptionHandler)
      getOperationExceptionHandler()).setRetryWait(l);
  }


  /**
   * See {@link ReopenOperationExceptionHandler#setRetryBackoff(int)}.
   *
   * @param  i  to set
   */
  public void setReopenRetryBackoff(final int i)
  {
    ((ReopenOperationExceptionHandler)
      getOperationExceptionHandler()).setRetryBackoff(i);
  }


  /**
   * Calculates the execution time of {@link ReopenOperationExceptionHandler}.
   */
  public class RetryExceptionHandler extends ReopenOperationExceptionHandler
  {


    /** {@inheritDoc} */
    @Override
    public void processInternal(
      final Connection conn,
      final SearchRequest request,
      final Response<SearchResult> response)
      throws LdapException
    {
      if (!allowRetry) {
        return;
      }
      final long t = System.currentTimeMillis();
      super.processInternal(conn, request, response);
      runTime += System.currentTimeMillis() - t;
      throw ex;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean retry(final int count)
    {
      if (stopCount > 0 && retryCount == stopCount) {
        return false;
      }
      boolean b = super.retry(count);
      if (b) {
        retryCount++;
      }
      return b;
    }
  }
}
