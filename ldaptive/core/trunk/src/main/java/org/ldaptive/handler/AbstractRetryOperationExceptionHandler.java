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
package org.ldaptive.handler;

import java.util.concurrent.TimeUnit;
import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;
import org.ldaptive.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common implementation for retrying after an operation exception.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class
AbstractRetryOperationExceptionHandler<Q extends Request, S>
  implements OperationExceptionHandler<Q, S>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Number of times to retry reopening an ldap connection. */
  private int retry;

  /** Amount of time in seconds to wait before reopen retries. */
  private long retryWait;

  /** Factor to multiply reopen retry wait by. */
  private int retryBackoff;


  /**
   * Returns the retry. This is the number of times the handler will retry when
   * it fails.
   *
   * @return  retry
   */
  public int getRetry()
  {
    return retry;
  }


  /**
   * Sets the number of retries.
   *
   * @param  i  to set
   */
  public void setRetry(final int i)
  {
    logger.trace("setting retry: {}", i);
    retry = i;
  }


  /**
   * Returns the retry wait. This is the amount of time in seconds that the
   * executing thread will sleep before attempting to retry again.
   *
   * @return  retry wait
   */
  public long getRetryWait()
  {
    return retryWait;
  }


  /**
   * Sets the retry wait. Time must be >= 0.
   *
   * @param  time  in seconds
   */
  public void setRetryWait(final long time)
  {
    if (time >= 0) {
      logger.trace("setting retryWait: {}", time);
      retryWait = time;
    }
  }


  /**
   * Returns the retry backoff. This is the factor by which the retry wait will
   * be multiplied in order to progressively delay the amount of time between
   * each retry.
   *
   * @return  retry backoff
   */
  public int getRetryBackoff()
  {
    return retryBackoff;
  }


  /**
   * Sets the retry backoff.
   *
   * @param  backoff  to set
   */
  public void setRetryBackoff(final int backoff)
  {
    logger.trace("setting retryBackoff: {}", backoff);
    retryBackoff = backoff;
  }


  /** {@inheritDoc} */
  @Override
  public HandlerResult<Response<S>> process(
    final Connection conn,
    final Q request,
    final Response<S> response)
    throws LdapException
  {
    for (int i = 0; i <= retry || retry == -1; i++) {
      try {
        processInternal(conn, request, response);
        break;
      } catch (LdapException e) {
        logger.error("unable to process operation exception", e);
        if (!retry(i)) {
          // process failed, throw the original exception
          return new HandlerResult<Response<S>>(null, true);
        }
      }
    }
    return createResult(conn, request, response);
  }


  /**
   * Perform any operations required to recover from the operation exception.
   *
   * @param  conn  connection the operation was executed on
   * @param  request  executed by the operation
   * @param  response  typically null
   *
   * @throws  LdapException  if the reopen fails
   */
  protected abstract void processInternal(
    final Connection conn,
    final Q request,
    final Response<S> response)
    throws LdapException;


  /**
   * Invoked if {@link #processInternal(Connection, Request, Response)}
   * succeeded. Creates a response to the original invocation that failed.
   *
   * @param  conn  connection the operation was executed on
   * @param  request  executed by the operation
   * @param  response  typically null
   *
   * @return  handler result containing a response for the original invocation
   *
   * @throws LdapException  if the operation fails
   */
  protected abstract HandlerResult<Response<S>> createResult(
    final Connection conn,
    final Q request,
    final Response<S> response)
    throws LdapException;


  /**
   * Returns whether the supplied count indicates that the operation should be
   * retried. If a retry wait has been configured, this method will sleep the
   * current thread for the configured time.
   *
   * @param  count  number of times the operation has been retried
   *
   * @return  whether to retry
   */
  protected boolean retry(final int count)
  {
    if (count < retry || retry == -1) {
      logger.warn(
        "Retry attempt {} of {}: wait={}, backoff={}",
        count + 1,
        retry,
        retryWait,
        retryBackoff);
      if (retryWait > 0) {
        long sleepTime = retryWait;
        if (retryBackoff > 0 && count > 0) {
          sleepTime = sleepTime * retryBackoff * count;
        }
        try {
          Thread.sleep(TimeUnit.SECONDS.toMillis(sleepTime));
        } catch (InterruptedException ie) {
          logger.debug("Retry wait interrupted", ie);
        }
      }
      return true;
    }
    return false;
  }
}
