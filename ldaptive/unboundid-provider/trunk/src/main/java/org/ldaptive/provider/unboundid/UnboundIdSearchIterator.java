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

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.EntrySourceException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPEntrySource;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultReferenceEntrySourceException;
import com.unboundid.ldap.sdk.SearchScope;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReferralBehavior;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchFilter;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search iterator for unbound id search results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIdSearchIterator implements SearchIterator
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request. */
  private org.ldaptive.SearchRequest request;

  /** Control processor. */
  private ControlProcessor<Control> controlProcessor;

  /** Response data. */
  private org.ldaptive.Response<Void> response;

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Ldap entry source. */
  private LDAPEntrySource source;

  /** Last entry read from the entry source. */
  private Entry entry;

  /** Codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Operation response timeout. */
  private long responseTimeout;


  /**
   * Creates a new unbound id search iterator.
   *
   * @param  sr  search request
   * @param  processor  control processor
   */
  public UnboundIdSearchIterator(
    final org.ldaptive.SearchRequest sr,
    final ControlProcessor<Control> processor)
  {
    request = sr;
    controlProcessor = processor;
  }


  /**
   * Returns the ldap result codes to retry operations on.
   *
   * @return  result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the ldap result codes to retry operations on.
   *
   * @param  codes  result codes
   */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    operationRetryResultCodes = codes;
  }


  /**
   * Returns the response time out in milliseconds.
   *
   * @return  response time out
   */
  public long getResponseTimeout()
  {
    return responseTimeout;
  }


  /**
   * Sets the response time out.
   *
   * @param  timeout  time in milliseconds
   */
  public void setResponseTimeout(final long timeout)
  {
    responseTimeout = timeout;
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
    try {
      source = search(connection, request);
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    }
  }


  /**
   * Executes an ldap search.
   *
   * @param  conn  to search with
   * @param  sr  to read properties from
   *
   * @return  ldap search results
   *
   * @throws  LDAPException  if an error occurs
   */
  protected LDAPEntrySource search(
    final LDAPConnection conn,
    final org.ldaptive.SearchRequest sr)
    throws LDAPException
  {
    final SearchRequest unboundIdSr = getSearchRequest(request);
    final Control[] c = controlProcessor.processRequestControls(
      request.getControls());
    unboundIdSr.addControls(c);
    return new LDAPEntrySource(connection, unboundIdSr, false);
  }


  /**
   * Returns an unbound id search request object configured with the supplied
   * search request.
   *
   * @param  sr  search request containing configuration to create unbound id
   * search request
   *
   * @return  search request
   *
   * @throws  LDAPException  if the search request cannot be initialized
   */
  protected SearchRequest getSearchRequest(
    final org.ldaptive.SearchRequest sr)
    throws LDAPException
  {
    String[] retAttrs = sr.getReturnAttributes();
    if (retAttrs != null && retAttrs.length == 0) {
      retAttrs = new String[] {"1.1"};
    }

    final SearchRequest unboundIdSr = new SearchRequest(
      sr.getBaseDn(),
      getSearchScope(sr.getSearchScope()),
      getDereferencePolicy(sr.getDerefAliases()),
      (int) sr.getSizeLimit(),
      (int) sr.getTimeLimit(),
      sr.getTypesOnly(),
      SearchFilter.format(sr.getSearchFilter()),
      retAttrs);
    unboundIdSr.setResponseTimeoutMillis(responseTimeout);
    return unboundIdSr;
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
    if (source == null || response != null) {
      return false;
    }
    while (entry == null) {
      try {
        entry = source.nextEntry();
        if (entry == null) {
          final SearchResult result = source.getSearchResult();
          final org.ldaptive.control.ResponseControl[] respControls =
            controlProcessor.processResponseControls(
              request.getControls(),
              result.getResponseControls());
          final boolean searchAgain = ControlProcessor.searchAgain(
            respControls);
          if (searchAgain) {
            source = search(connection, request);
          } else {
            UnboundIdUtil.throwOperationException(
              operationRetryResultCodes,
              result.getResultCode());
            response = new org.ldaptive.Response<Void>(
              null,
              ResultCode.valueOf(result.getResultCode().intValue()),
              respControls);
            break;
          }
        }
      } catch (SearchResultReferenceEntrySourceException e) {
        if (request.getReferralBehavior() == ReferralBehavior.FOLLOW) {
          throw new UnsupportedOperationException(
            "Referral following not supported");
        } else if (request.getReferralBehavior() == ReferralBehavior.IGNORE) {
          e.getSearchReference();
        } else {
          throw new LdapException(
            "Encountered referral: " + e.getSearchReference(),
            ResultCode.REFERRAL);
        }
      } catch (EntrySourceException e) {
        if (!e.mayContinueReading()) {
          final SearchResult result = source.getSearchResult();
          UnboundIdUtil.throwOperationException(
            operationRetryResultCodes,
            result.getResultCode());

          final org.ldaptive.control.ResponseControl[] respControls =
            controlProcessor.processResponseControls(
              request.getControls(),
              result.getResponseControls());
          response = new org.ldaptive.Response<Void>(
            null,
            ResultCode.valueOf(result.getResultCode().intValue()),
            respControls);
          break;
        }
      } catch (LDAPException e) {
        UnboundIdUtil.throwOperationException(
          operationRetryResultCodes,
          e,
          controlProcessor);
      }
    }
    return entry != null;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry next()
    throws org.ldaptive.LdapException
  {
    final UnboundIdUtil bu = new UnboundIdUtil(request.getSortBehavior());
    bu.setBinaryAttributes(request.getBinaryAttributes());

    final LdapEntry le = bu.toLdapEntry(entry);
    entry = null;
    return le;
  }


  /** {@inheritDoc} */
  @Override
  public org.ldaptive.Response<Void> getResponse()
  {
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    source.close();
  }
}
