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
package org.ldaptive.provider.unboundid;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchScope;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReferralBehavior;
import org.ldaptive.ResultCode;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search iterator for unbound id search results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIDSearchIterator implements SearchIterator
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request. */
  private final org.ldaptive.SearchRequest request;

  /** Provider configuration. */
  private final UnboundIDProviderConfig config;

  /** Response data. */
  private org.ldaptive.Response<Void> response;

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Search result iterator. */
  private SearchResultIterator resultIterator;


  /**
   * Creates a new unbound id search iterator.
   *
   * @param  sr  search request
   * @param  pc  provider configuration
   */
  public UnboundIDSearchIterator(
    final org.ldaptive.SearchRequest sr,
    final UnboundIDProviderConfig pc)
  {
    request = sr;
    config = pc;
  }


  /**
   * Initializes this unbound id search iterator.
   *
   * @param  conn  to search with
   *
   * @throws  org.ldaptive.LdapException  if an error occurs
   */
  public void initialize(final LDAPConnection conn)
    throws org.ldaptive.LdapException
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
    final LDAPConnection conn,
    final org.ldaptive.SearchRequest sr)
    throws LdapException
  {
    final SearchResultIterator i = new SearchResultIterator();
    try {
      final SearchRequest unboundIdSr = getSearchRequest(request, i);
      final Control[] c = config.getControlProcessor().processRequestControls(
        request.getControls());
      unboundIdSr.addControls(c);
      i.setResult(connection.search(unboundIdSr));
    } catch (LDAPException e) {
      final ResultCode rc = ignoreSearchException(
        config.getSearchIgnoreResultCodes(), e);
      if (rc == null) {
        ProviderUtils.throwOperationException(
          config.getOperationRetryResultCodes(),
          e,
          e.getResultCode().intValue(),
          config.getControlProcessor().processResponseControls(
            request.getControls(), e.getResponseControls()),
          true);
      }
      i.setResult(
        new SearchResult(
          -1,
          e.getResultCode(),
          e.getDiagnosticMessage(),
          e.getMatchedDN(),
          e.getReferralURLs(),
          0,
          0,
          e.getResponseControls()));
    }
    return i;
  }


  /**
   * Returns an unbound id search request object configured with the supplied
   * search request.
   *
   * @param  sr  search request containing configuration to create unbound id
   * search request
   * @param  listener  search result listener
   *
   * @return  search request
   *
   * @throws  LDAPException  if the search request cannot be initialized
   */
  protected SearchRequest getSearchRequest(
    final org.ldaptive.SearchRequest sr, final SearchResultListener listener)
    throws LDAPException
  {
    String[] retAttrs = sr.getReturnAttributes();
    if (retAttrs != null && retAttrs.length == 0) {
      retAttrs = new String[] {"1.1"};
    }

    return new SearchRequest(
      listener,
      sr.getBaseDn(),
      getSearchScope(sr.getSearchScope()),
      getDereferencePolicy(sr.getDerefAliases()),
      (int) sr.getSizeLimit(),
      (int) sr.getTimeLimit(),
      sr.getTypesOnly(),
      sr.getSearchFilter() != null ?
        sr.getSearchFilter().format() : null,
      retAttrs);
  }


  /**
   * Returns the unbound id search scope for the supplied search scope.
   *
   * @param  ss  search scope
   *
   * @return  unbound id search scope
   */
  protected static SearchScope getSearchScope(
    final org.ldaptive.SearchScope ss)
  {
    SearchScope scope = null;
    if (ss == org.ldaptive.SearchScope.OBJECT) {
      scope = SearchScope.BASE;
    } else if (ss == org.ldaptive.SearchScope.ONELEVEL) {
      scope = SearchScope.ONE;
    } else if (ss == org.ldaptive.SearchScope.SUBTREE) {
      scope = SearchScope.SUB;
    }
    return scope;
  }


  /**
   * Returns the unbound id deference policy for the supplied deref aliases.
   *
   * @param  deref  deref aliases
   *
   * @return  dereference policy
   */
  protected static DereferencePolicy getDereferencePolicy(
    final DerefAliases deref)
  {
    DereferencePolicy policy = DereferencePolicy.NEVER;
    if (deref == DerefAliases.ALWAYS) {
      policy = DereferencePolicy.ALWAYS;
    } else if (deref == DerefAliases.FINDING) {
      policy = DereferencePolicy.FINDING;
    } else if (deref == DerefAliases.NEVER) {
      policy = DereferencePolicy.NEVER;
    } else if (deref == DerefAliases.SEARCHING) {
      policy = DereferencePolicy.SEARCHING;
    }
    return policy;
  }


  /** {@inheritDoc} */
  @Override
  public boolean hasNext()
    throws org.ldaptive.LdapException
  {
    if (resultIterator == null || response != null) {
      return false;
    }
    boolean more = resultIterator.hasNext();
    if (!more) {
      final SearchResult result = resultIterator.getResult();
      final ResponseControl[] respControls =
        config.getControlProcessor().processResponseControls(
          request.getControls(), result.getResponseControls());
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
    throws org.ldaptive.LdapException
  {
    final UnboundIDUtils util = new UnboundIDUtils(request.getSortBehavior());
    util.setBinaryAttributes(request.getBinaryAttributes());
    SearchResultEntry entry = null;
    try {
      entry = resultIterator.getSearchResultEntry();
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
    final LDAPException e)
  {
    ResultCode ignore = null;
    if (ignoreResultCodes != null && ignoreResultCodes.length > 0) {
      for (ResultCode rc : ignoreResultCodes) {
        if (e.getResultCode().intValue() == rc.value()) {
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
   * Search results listener for storing entries returned by the search
   * operation.
   */
  private class SearchResultIterator implements SearchResultListener
  {

    /** serial version uid. */
    private static final long serialVersionUID = -6869001221533530602L;

    /** Search results. */
    private Queue<SearchResultEntry> responseQueue =
      new ConcurrentLinkedQueue<SearchResultEntry>();

    /** Search result. */
    private SearchResult result;

    /** Ldap search exception to report. */
    private LDAPException searchException;

    /** Runtime exception to report. */
    private RuntimeException runtimeException;


    /**
     * Returns the next search result entry from the queue.
     *
     * @return  search result entry
     *
     * @throws  LDAPException  if an ldap search error needs to be
     * reported
     * @throws  RuntimeException  if an unsupported operation was attempted
     */
    public SearchResultEntry getSearchResultEntry()
      throws LDAPException
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
    public SearchResult getResult()
    {
      return result;
    }


    /**
     * Sets the result of the search.
     *
     * @param  sr  search result
     */
    public void setResult(final SearchResult sr)
    {
      result = sr;
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
    public void searchEntryReturned(final SearchResultEntry entry)
    {
      responseQueue.add(entry);
    }


    /** {@inheritDoc} */
    @Override
    public void searchReferenceReturned(final SearchResultReference ref)
    {
      if (request.getReferralBehavior() == ReferralBehavior.FOLLOW) {
        runtimeException = new UnsupportedOperationException(
          "Referral following not supported");
      } else  if (request.getReferralBehavior() == ReferralBehavior.THROW) {
        searchException = new LDAPException(
          com.unboundid.ldap.sdk.ResultCode.REFERRAL,
          "Encountered referral: " + ref);
      }
    }
  }
}
