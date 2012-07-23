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
package org.ldaptive.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.ldaptive.AbstractSearchOperation;
import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Response;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.provider.SearchIterator;

/**
 * Executes an asynchronous ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AsyncSearchOperation extends AbstractSearchOperation<SearchRequest>
{

  /** Single thread executor to submit async operations to. */
  private final ExecutorService executorService =
    Executors.newSingleThreadExecutor();


  /**
   * Creates a new async search operation.
   *
   * @param  conn  connection
   */
  public AsyncSearchOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  public FutureResponse<SearchResult> execute(final SearchRequest request)
    throws LdapException
  {
    final Future<Response<SearchResult>> future = executorService.submit(
      new Callable<Response<SearchResult>>() {
        @Override
        public Response<SearchResult> call()
          throws LdapException
        {
          return AsyncSearchOperation.super.execute(request);
        }
      });
    return new FutureResponse<SearchResult>(future);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<SearchResult> invoke(final SearchRequest request)
    throws LdapException
  {
    final SearchIterator si =
      getConnection().getProviderConnection().searchAsync(request);
    final SearchResult result = readResult(request, si);
    final Response<Void> response = si.getResponse();
    return
      new Response<SearchResult>(
        result,
        response.getResultCode(),
        response.getMessage(),
        response.getMatchedDn(),
        response.getControls(),
        response.getReferralURLs(),
        response.getMessageId());
  }
}
