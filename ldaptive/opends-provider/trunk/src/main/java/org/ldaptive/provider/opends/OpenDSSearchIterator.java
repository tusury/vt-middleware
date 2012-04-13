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
package org.ldaptive.provider.opends;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReferralBehavior;
import org.ldaptive.ResultCode;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.opends.sdk.Connection;
import org.opends.sdk.DereferenceAliasesPolicy;
import org.opends.sdk.ErrorResultException;
import org.opends.sdk.SearchResultHandler;
import org.opends.sdk.SearchScope;
import org.opends.sdk.controls.Control;
import org.opends.sdk.requests.Requests;
import org.opends.sdk.requests.SearchRequest;
import org.opends.sdk.responses.Result;
import org.opends.sdk.responses.SearchResultEntry;
import org.opends.sdk.responses.SearchResultReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search iterator for opends search results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDSSearchIterator implements SearchIterator
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request. */
  private final org.ldaptive.SearchRequest request;

  /** Provider configuration. */
  private final OpenDSProviderConfig config;

  /** Response data. */
  private org.ldaptive.Response<Void> response;

  /** Ldap connection. */
  private Connection connection;

  /** Search result iterator. */
  private SearchResultIterator resultIterator;


  /**
   * Creates a new opends search iterator.
   *
   * @param  sr  search request
   * @param  pc  provider configuration
   */
  public OpenDSSearchIterator(
    final org.ldaptive.SearchRequest sr, final OpenDSProviderConfig pc)
  {
    request = sr;
    config = pc;
  }


  /**
   * Initializes this opends search iterator.
   *
   * @param  conn  to search with
   *
   * @throws  LdapException  if an error occurs
   */
  public void initialize(final Connection conn)
    throws LdapException
  {
    connection = conn;
    resultIterator = search(connection, request);
  }


  /**
   * Executes an ldap search.
   *
   * @param  conn  to search with
   * @param  sr  to read properties from
   *
   * @return  ldap search results
   *
   * @throws  LdapException  if an error occurs
   */
  protected SearchResultIterator search(
    final Connection conn, final org.ldaptive.SearchRequest sr)
    throws LdapException
  {
    final SearchRequest opendsSr = getSearchRequest(sr);
    if (sr.getControls() != null) {
      for (Control c :
           config.getControlProcessor().processRequestControls(
             sr.getControls())) {
        opendsSr.addControl(c);
      }
    }
    final SearchResultIterator i = new SearchResultIterator();
    try {
      conn.search(opendsSr, i);
    } catch (ErrorResultException e) {
      final ResultCode rc = ignoreSearchException(
        config.getSearchIgnoreResultCodes(), e);
      if (rc == null) {
        ProviderUtils.throwOperationException(
          config.getOperationRetryResultCodes(),
          e,
          e.getResult().getResultCode().intValue(),
          config.getControlProcessor().processResponseControls(
            request.getControls(),
            e.getResult().getControls().toArray(new Control[0])),
          true);
      }
    } catch (InterruptedException e) {
      throw new LdapException(e);
    }
    return i;
  }


  /**
   * Returns an opends search request object configured with the supplied
   * search request.
   *
   * @param  sr  search request containing configuration to create opends
   * search request
   *
   * @return  search request
   */
  protected SearchRequest getSearchRequest(
    final org.ldaptive.SearchRequest sr)
  {
    String[] retAttrs = sr.getReturnAttributes();
    if (retAttrs != null && retAttrs.length == 0) {
      retAttrs = new String[] {"1.1"};
    } else if (retAttrs == null) {
      retAttrs = new String[0];
    }

    final SearchRequest opendsSr = Requests.newSearchRequest(
      sr.getBaseDn(),
      getSearchScope(sr.getSearchScope()),
      sr.getSearchFilter() != null ? sr.getSearchFilter().format() : null,
      retAttrs);
    opendsSr.setDereferenceAliasesPolicy(
      getDereferencePolicy(sr.getDerefAliases()));
    opendsSr.setSizeLimit((int) sr.getSizeLimit());
    opendsSr.setTimeLimit((int) sr.getTimeLimit());
    opendsSr.setTypesOnly(sr.getTypesOnly());
    return opendsSr;
  }


  /**
   * Returns the opends search scope for the supplied search scope.
   *
   * @param  ss  search scope
   *
   * @return  opends search scope
   */
  protected static SearchScope getSearchScope(
    final org.ldaptive.SearchScope ss)
  {
    SearchScope scope = null;
    if (ss == org.ldaptive.SearchScope.OBJECT) {
      scope = SearchScope.BASE_OBJECT;
    } else if (ss == org.ldaptive.SearchScope.ONELEVEL) {
      scope = SearchScope.SINGLE_LEVEL;
    } else if (ss == org.ldaptive.SearchScope.SUBTREE) {
      scope = SearchScope.WHOLE_SUBTREE;
    }
    return scope;
  }


  /**
   * Returns the opends deference policy for the supplied deref aliases.
   *
   * @param  deref  deref aliases
   *
   * @return  dereference policy
   */
  protected static DereferenceAliasesPolicy getDereferencePolicy(
    final DerefAliases deref)
  {
    DereferenceAliasesPolicy policy = DereferenceAliasesPolicy.NEVER;
    if (deref == DerefAliases.ALWAYS) {
      policy = DereferenceAliasesPolicy.ALWAYS;
    } else if (deref == DerefAliases.FINDING) {
      policy = DereferenceAliasesPolicy.FINDING_BASE;
    } else if (deref == DerefAliases.NEVER) {
      policy = DereferenceAliasesPolicy.NEVER;
    } else if (deref == DerefAliases.SEARCHING) {
      policy = DereferenceAliasesPolicy.IN_SEARCHING;
    }
    return policy;
  }


  /** {@inheritDoc} */
  @Override
  public boolean hasNext()
    throws LdapException
  {
    if (resultIterator == null || response != null) {
      return false;
    }

    boolean more = resultIterator.hasNext();
    if (!more) {
      final Result result = resultIterator.getResult();
      final ResponseControl[] respControls =
        config.getControlProcessor().processResponseControls(
          request.getControls(), result.getControls().toArray(new Control[0]));
      final boolean searchAgain = ControlProcessor.searchAgain(respControls);
      if (searchAgain) {
        resultIterator = search(connection, request);
        more = resultIterator.hasNext();
      }
      if (!more) {
        response = new org.ldaptive.Response<Void>(
          null,
          ResultCode.valueOf(result.getResultCode().intValue()),
          respControls);
      }
    }
    return more;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry next()
    throws LdapException
  {
    final OpenDSUtils util = new OpenDSUtils(request.getSortBehavior());
    util.setBinaryAttributes(request.getBinaryAttributes());
    SearchResultEntry entry = null;
    try {
      entry = resultIterator.getSearchResultEntry();
    } catch (ErrorResultException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResult().getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          e.getResult().getControls().toArray(new Control[0])),
        true);
    }
    return util.toLdapEntry(entry);
  }


  /**
   * Determines whether the supplied ldap exception should be ignored.
   *
   * @param  ignoreResultCodes  to match against the exception
   * @param  e  ldap exception to match
   *
   * @return  result code that should be ignored or null
   */
  protected ResultCode ignoreSearchException(
    final ResultCode[] ignoreResultCodes,
    final ErrorResultException e)
  {
    ResultCode ignore = null;
    if (ignoreResultCodes != null && ignoreResultCodes.length > 0) {
      for (ResultCode rc : ignoreResultCodes) {
        if (e.getResult().getResultCode().intValue() == rc.value()) {
          logger.debug("Ignoring ldap exception", e);
          ignore = rc;
          break;
        }
      }
    }
    return ignore;
  }


  /** {@inheritDoc} */
  @Override
  public org.ldaptive.Response<Void> getResponse()
  {
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public void close() throws LdapException {}


  /**
   * Search results handler for storing entries returned by the search
   * operation.
   */
  private class SearchResultIterator implements SearchResultHandler
  {

    /** Search results. */
    private final Queue<SearchResultEntry> responseQueue =
      new ConcurrentLinkedQueue<SearchResultEntry>();

    /** Search result. */
    private Result result;

    /** Ldap search exception to report. */
    private ErrorResultException searchException;

    /** Runtime exception to report. */
    private RuntimeException runtimeException;


    /**
     * Returns the next search result entry from the queue.
     *
     * @return  search result entry
     *
     * @throws  ErrorResultException  if an ldap search error needs to be
     * reported
     * @throws  RuntimeException  if an unsupported operation was attempted
     */
    public SearchResultEntry getSearchResultEntry()
      throws ErrorResultException
    {
      if (runtimeException != null) {
        throw runtimeException;
      } else if (searchException != null) {
        throw searchException;
      }
      return responseQueue.poll();
    }


    /**
     * Returns the result of the search.
     *
     * @return  search result
     */
    public Result getResult()
    {
      return result;
    }


    /**
     * Whether the response queue is empty.
     *
     * @return  whether the response queue is empty
     */
    public boolean hasNext()
    {
      return !responseQueue.isEmpty();
    }


    /** {@inheritDoc} */
    @Override
    public void handleErrorResult(final ErrorResultException e)
    {
      result = e.getResult();
    }


    /** {@inheritDoc} */
    @Override
    public void handleResult(final Result r)
    {
      result = r;
    }


    /** {@inheritDoc} */
    @Override
    public boolean handleEntry(final SearchResultEntry entry)
    {
      responseQueue.add(entry);
      return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean handleReference(final SearchResultReference ref)
    {
      if (request.getReferralBehavior() == ReferralBehavior.FOLLOW) {
        runtimeException = new UnsupportedOperationException(
          "Referral following not supported");
      } else  if (request.getReferralBehavior() == ReferralBehavior.THROW) {
        searchException = ErrorResultException.newErrorResult(
          org.opends.sdk.ResultCode.valueOf(ResultCode.REFERRAL.value()),
          "Encountered referral: " + ref);
      }
      return true;
    }
  }
}
