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
package org.ldaptive;

import java.util.Arrays;
import org.ldaptive.handler.OperationResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common implementation for ldap operations.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractOperation<Q extends Request, S>
  implements Operation<Q, S>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Connection to perform operation. */
  private final Connection connection;

  /** Number of times to retry ldap operations. */
  private int operationRetry;

  /** Amount of time in milliseconds to wait before retrying. */
  private long operationRetryWait;

  /** Factor to multiply operation retry wait by. */
  private int operationRetryBackoff;

  /** Handlers to process operation responses. */
  private OperationResponseHandler<S>[] operationResponseHandlers;


  /**
   * Creates a new abstract connection.
   *
   * @param  conn  to use for this operation
   */
  public AbstractOperation(final Connection conn)
  {
    connection = conn;

    final ConnectionConfig cc = conn.getConnectionConfig();
    operationRetry = cc.getOperationRetry();
    operationRetryWait = cc.getOperationRetryWait();
    operationRetryBackoff = cc.getOperationRetryBackoff();
  }


  /**
   * Returns the connection used for this operation.
   *
   * @return  connection
   */
  protected Connection getConnection()
  {
    return connection;
  }


  /**
   * Returns the operation retry. This is the number of times an operation will
   * be retried when it throws OperationException. What constitutes an
   * OperationException is configured per each provider implementation.
   *
   * @return  operation retry
   */
  public int getOperationRetry()
  {
    return operationRetry;
  }


  /**
   * Sets the operation retry.
   *
   * @param  retry  to set
   */
  public void setOperationRetry(final int retry)
  {
    operationRetry = retry;
  }


  /**
   * Returns the operation retry wait. This is the amount of time in
   * milliseconds that the executing thread will sleep before attempting the
   * operation again.
   *
   * @return  operation retry wait
   */
  public long getOperationRetryWait()
  {
    return operationRetryWait;
  }


  /**
   * Sets the operation retry wait.
   *
   * @param  wait  to set
   */
  public void setOperationRetryWait(final long wait)
  {
    operationRetryWait = wait;
  }


  /**
   * Returns the operation retry backoff. This is the factor by which the retry
   * wait will be multiplied in order to progressively delay the amount time
   * between each operation retry.
   *
   * @return  operation retry backoff
   */
  public int getOperationRetryBackoff()
  {
    return operationRetryBackoff;
  }


  /**
   * Sets the operation retry backoff.
   *
   * @param  backoff  to set
   */
  public void setOperationRetryBackoff(final int backoff)
  {
    operationRetryBackoff = backoff;
  }


  /**
   * Returns the operation response handlers.
   *
   * @return  operation response handlers
   */
  public OperationResponseHandler<S>[] getOperationResponseHandlers()
  {
    return operationResponseHandlers;
  }


  /**
   * Sets the operation response handlers.
   *
   * @param  handlers  operation response handlers
   */
  public void setOperationResponseHandlers(
    final OperationResponseHandler<S>... handlers)
  {
    operationResponseHandlers = handlers;
  }


  /**
   * Call the provider specific implementation of this ldap operation.
   *
   * @param  request  ldap request
   *
   * @return  ldap response
   *
   * @throws  LdapException  if the invocation fails
   */
  protected abstract Response<S> invoke(final Q request)
    throws LdapException;


  /** {@inheritDoc} */
  @Override
  public Response<S> execute(final Q request)
    throws LdapException
  {
    logger.debug("execute request={} with connection={}", request, connection);

    Response<S> response = null;
    for (int i = 0; i <= operationRetry || operationRetry == -1; i++) {
      try {
        response = invoke(request);
        break;
      } catch (OperationException e) {
        operationRetry(e, i);
      }
    }
    // execute response handlers
    if (
      getOperationResponseHandlers() != null &&
        getOperationResponseHandlers().length > 0) {
      for (OperationResponseHandler<S> h : getOperationResponseHandlers()) {
        h.process(response);
      }
    }

    logger.debug(
      "execute response={} for request={} with connection={}",
      new Object[] {response, request, connection});
    return response;
  }


  /**
   * Called in response to an operation failure. This method determines whether
   * that operation should be attempted again. If so, the underlying ldap
   * connection is closed and re-opened in preparation for that event. If not,
   * the supplied exception is thrown.
   *
   * @param  e  exception that was thrown
   * @param  count  number of operation attempts
   *
   * @throws  LdapException  if the operation won't be retried
   */
  protected void operationRetry(final LdapException e, final int count)
    throws LdapException
  {
    if (count < operationRetry || operationRetry == -1) {
      logger.warn(
        "Error performing LDAP operation, retrying (attempt {})",
        count,
        e);
      connection.close();
      if (operationRetryWait > 0) {
        long sleepTime = operationRetryWait;
        if (operationRetryBackoff > 0 && count > 0) {
          sleepTime = sleepTime * operationRetryBackoff * count;
        }
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
          logger.debug("Operation retry wait interrupted", ie);
        }
      }
      connection.open();
    } else {
      throw e;
    }
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::connection=%s, operationRetry=%s, operationRetryWait=%s, " +
        "operationRetryBackoff=%s, operationResponseHandlers=%s]",
        getClass().getName(),
        hashCode(),
        connection,
        operationRetry,
        operationRetryWait,
        operationRetryBackoff,
        Arrays.toString(operationResponseHandlers));
  }
}
