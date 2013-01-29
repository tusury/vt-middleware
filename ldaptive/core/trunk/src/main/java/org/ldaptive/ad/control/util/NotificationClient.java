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
package org.ldaptive.ad.control.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Request;
import org.ldaptive.SearchEntry;
import org.ldaptive.SearchRequest;
import org.ldaptive.ad.control.NotificationControl;
import org.ldaptive.async.AbandonOperation;
import org.ldaptive.async.AbandonRequest;
import org.ldaptive.async.AsyncRequest;
import org.ldaptive.async.AsyncSearchOperation;
import org.ldaptive.handler.AsyncRequestHandler;
import org.ldaptive.handler.HandlerResult;
import org.ldaptive.handler.SearchEntryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client that simplifies using the notification control.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NotificationClient
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Connection to invoke the search operation on. */
  private final Connection connection;


  /**
   * Creates a new notification client.
   *
   * @param  conn  to execute the search operation on
   */
  public NotificationClient(final Connection conn)
  {
    connection = conn;
  }


  /**
   * Performs a search operation with the {@link NotificationControl}. The
   * supplied request is modified in the following way:
   *
   * <ul>
   *   <li>{@link SearchRequest#setControls(
   *     org.ldaptive.control.RequestControl...)} is invoked with {@link
   *     NotificationControl}</li>
   *   <li>{@link SearchRequest#setSearchEntryHandlers(SearchEntryHandler...)}
   *     is invoked with a custom handler that places notification data in a
   *     blocking queue.</li>
   * </ul>
   *
   * <p>The search request object should not be reused for any other search
   * operations.</p>
   *
   * @param  request  search request to execute
   *
   * @return  blocking queue to wait for search entries
   *
   * @throws  LdapException  if the search fails
   */
  @SuppressWarnings("unchecked")
  public BlockingQueue<NotificationItem> execute(final SearchRequest request)
    throws LdapException
  {
    final BlockingQueue<NotificationItem> queue =
      new LinkedBlockingQueue<NotificationItem>();

    final AsyncSearchOperation search = new AsyncSearchOperation(connection);
    request.setControls(new NotificationControl());
    request.setSearchEntryHandlers(
      new SearchEntryHandler() {
        @Override
        public HandlerResult<SearchEntry> process(
          final Connection conn,
          final SearchRequest request,
          final SearchEntry entry)
          throws LdapException
        {
          try {
            logger.debug("received {}", entry);
            queue.put(new NotificationItem(entry));
          } catch (Exception e) {
            logger.warn("Unable to enqueue entry {}", entry);
          }
          return new HandlerResult<SearchEntry>(null);
        }

        @Override
        public void initializeRequest(final SearchRequest request) {}
      });
    request.setAsyncRequestHandlers(
      new AsyncRequestHandler() {
        @Override
        public HandlerResult<AsyncRequest> process(
          final Connection conn,
          final Request request,
          final AsyncRequest asyncRequest)
          throws LdapException
        {
          try {
            logger.debug("received {}", asyncRequest);
            queue.put(new NotificationItem(asyncRequest));
          } catch (Exception e) {
            logger.warn("Unable to enqueue async request {}", asyncRequest);
          }
          return new HandlerResult<AsyncRequest>(null);
        }
      });

    search.execute(request);
    return queue;
  }


  /**
   * Invokes an abandon operation on the supplied ldap message id. Convenience
   * method supplied to abandon async search operations.
   *
   * @param  messageId  of the operation to abandon
   *
   * @throws  LdapException  if the abandon operation fails
   */
  public void abandon(final int messageId)
    throws LdapException
  {
    final AbandonOperation abandon = new AbandonOperation(connection);
    abandon.execute(new AbandonRequest(messageId));
  }


  /**
   * Contains data returned when using the notification control.
   */
  public static class NotificationItem
  {

    /** Async request from the search operation. */
    private final AsyncRequest asyncRequest;

    /** Entry contained in this sync repl item. */
    private final SearchEntry searchEntry;


    /**
     * Creates a new notification item.
     *
     * @param  request  that represents this item
     */
    public NotificationItem(final AsyncRequest request)
    {
      asyncRequest = request;
      searchEntry = null;
    }


    /**
     * Creates a new notification item.
     *
     * @param  entry  that represents this item
     */
    public NotificationItem(final SearchEntry entry)
    {
      asyncRequest = null;
      searchEntry = entry;
    }


    /**
     * Returns whether this item represents an async request.
     *
     * @return  whether this item represents an async request
     */
    public boolean isAsyncRequest()
    {
      return asyncRequest != null;
    }


    /**
     * Returns the async request contained in this item or null if this item
     * does not contain an async request.
     *
     * @return  async request
     */
    public AsyncRequest getAsyncRequest()
    {
      return asyncRequest;
    }


    /**
     * Returns whether this item represents a search entry.
     *
     * @return  whether this item represents a search entry
     */
    public boolean isSearchEntry()
    {
      return searchEntry != null;
    }


    /**
     * Returns the search entry contained in this item or null if this item does
     * not contain a search entry.
     *
     * @return  search entry
     */
    public SearchEntry getEntry()
    {
      return searchEntry;
    }


    /** {@inheritDoc} */
    @Override
    public String toString()
    {
      String s;
      if (isAsyncRequest()) {
        s = String.format(
          "[%s@%d::asyncRequest=%s]",
          getClass().getName(),
          hashCode(),
          asyncRequest);
      } else if (isSearchEntry()) {
        s = String.format(
          "[%s@%d::seachEntry=%s]",
          getClass().getName(),
          hashCode(),
          searchEntry);
      } else {
        s = String.format("[%s@%d]", getClass().getName(), hashCode());
      }
      return s;
    }
  }
}
