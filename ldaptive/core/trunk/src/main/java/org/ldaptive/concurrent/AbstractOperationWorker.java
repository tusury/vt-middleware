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
package org.ldaptive.concurrent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.ldaptive.LdapException;
import org.ldaptive.Operation;
import org.ldaptive.Request;
import org.ldaptive.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for worker operations. If no {@link ExecutorService} is provided
 * a cached thread pool is used by default.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractOperationWorker<Q extends Request, S>
  implements OperationWorker<Q, S>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** operation to execute. */
  private final Operation<Q, S> operation;

  /** to submit operations to. */
  private final ExecutorService service;


  /**
   * Creates a new abstract operation worker.
   *
   * @param  op  operation
   */
  public AbstractOperationWorker(final Operation<Q, S> op)
  {
    this(op, Executors.newCachedThreadPool());
  }


  /**
   * Creates a new abstract operation worker.
   *
   * @param  op  operation
   * @param  es  executor service
   */
  public AbstractOperationWorker(
    final Operation<Q, S> op, final ExecutorService es)
  {
    operation = op;
    service = es;
  }


  /**
   * Execute an ldap operation on a separate thread.
   *
   * @param  request  containing the data required by this operation
   *
   * @return  future response for this operation
   */
  public Future<Response<S>> execute(final Q request)
  {
    return service.submit(createCallable(operation, request));
  }


  /**
   * Execute an ldap operation for each request on a separate thread.
   *
   * @param  requests  containing the data required by this operation
   *
   * @return  future responses for this operation
   */
  public Collection<Future<Response<S>>> execute(final Q... requests)
  {
    final List<Future<Response<S>>> results =
      new LinkedList<Future<Response<S>>>();
    for (Q request : requests) {
      results.add(service.submit(createCallable(operation, request)));
    }
    return results;
  }


  /**
   * Execute an ldap operation for each request on a separate thread and waits
   * for all operations to complete.
   *
   * @param  requests  containing the data required by this operation
   *
   * @return  responses for this operation
   */
  public Collection<Response<S>> executeToCompletion(final Q... requests)
  {
    final CompletionService<Response<S>> cs =
      new ExecutorCompletionService<Response<S>>(service);
    final List<Response<S>> results = new LinkedList<Response<S>>();
    for (Q request : requests) {
      cs.submit(createCallable(operation, request));
    }
    for (int i = 0; i < requests.length; i++) {
      try {
        results.add(cs.take().get());
      } catch (ExecutionException e) {
        logger.debug("ExecutionException thrown, ignoring", e);
      } catch (InterruptedException e) {
        logger.warn("InterrupedException thrown, ignoring", e);
      }
    }
    return results;
  }


  /**
   * Returns a {@link Callable} that executes the supplied request with the
   * supplied operation.
   *
   * @param  <Q>  type of ldap request
   * @param  <S>  type of ldap response
   * @param  operation  to execute
   * @param  request  to pass to the operation
   *
   * @return  callable for the supplied operation and request
   */
  public static <Q extends Request, S> Callable<Response<S>> createCallable(
    final Operation<Q, S> operation, final Q request)
  {
    return new Callable<Response<S>>() {
      public Response<S> call()
        throws LdapException
      {
        return operation.execute(request);
      }
    };
  }
}
