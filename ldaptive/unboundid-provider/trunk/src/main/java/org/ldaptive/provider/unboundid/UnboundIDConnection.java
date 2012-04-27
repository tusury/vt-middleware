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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.CRAMMD5BindRequest;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.EXTERNALBindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequestProperties;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SASLBindRequest;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Request;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.sasl.DigestMd5Config;
import org.ldaptive.sasl.GssApiConfig;
import org.ldaptive.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unbound ID provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIDConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Provider configuration. */
  private final UnboundIDProviderConfig config;


  /**
   * Creates a new unboundid ldap connection.
   *
   * @param  lc  ldap connection
   * @param  pc  provider configuration
   */
  public UnboundIDConnection(
    final LDAPConnection lc, final UnboundIDProviderConfig pc)
  {
    connection = lc;
    config = pc;
  }


  /**
   * Returns the underlying ldap connection.
   *
   * @return  ldap connection
   */
  public LDAPConnection getLdapConnection()
  {
    return connection;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    if (connection != null) {
      try {
        connection.close();
      } finally {
        connection = null;
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> bind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response;
    if (request.getSaslConfig() != null) {
      response = saslBind(request);
    } else if (request.getDn() == null && request.getCredential() == null) {
      response = anonymousBind(request);
    } else {
      response = simpleBind(request);
    }
    return response;
  }


  /**
   * Performs an anonymous bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> anonymousBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      SimpleBindRequest sbr;
      if (request.getControls() != null) {
        sbr = new SimpleBindRequest(
          "",
          new byte[0],
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      } else {
        sbr = new SimpleBindRequest();
      }
      sbr.setFollowReferrals(request.getFollowReferrals());

      final BindResult result = connection.bind(sbr);
      response = createResponse(request, null, result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /**
   * Performs a simple bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> simpleBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      SimpleBindRequest sbr;
      if (request.getControls() != null) {
        sbr = new SimpleBindRequest(
          new DN(request.getDn()),
          request.getCredential().getBytes(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      } else {
        sbr = new SimpleBindRequest(
          request.getDn(),
          request.getCredential().getBytes());
      }
      sbr.setFollowReferrals(request.getFollowReferrals());

      final BindResult result = connection.bind(sbr);
      response = createResponse(request, null, result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /**
   * Performs a sasl bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> saslBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      SASLBindRequest sbr;
      final SaslConfig sc = request.getSaslConfig();
      switch (sc.getMechanism()) {

      case EXTERNAL:
        sbr = new EXTERNALBindRequest(
          sc.getAuthorizationId(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
        break;

      case DIGEST_MD5:

        String realm = sc instanceof DigestMd5Config
          ? ((DigestMd5Config) sc).getRealm() : null;
        if (realm == null && request.getDn().contains("@")) {
          realm = request.getDn().substring(request.getDn().indexOf("@") + 1);
        }
        sbr = new DIGESTMD5BindRequest(
          request.getDn(),
          "".equals(sc.getAuthorizationId()) ? null : sc.getAuthorizationId(),
          request.getCredential() != null ? request.getCredential().getBytes()
                                          : null,
          realm,
          config.getControlProcessor().processRequestControls(
            request.getControls()));
        break;

      case CRAM_MD5:
        sbr = new CRAMMD5BindRequest(
          request.getDn(),
          request.getCredential() != null ? request.getCredential().getBytes()
                                          : null,
          config.getControlProcessor().processRequestControls(
            request.getControls()));
        break;

      case GSSAPI:

        final GSSAPIBindRequestProperties props =
          new GSSAPIBindRequestProperties(
            request.getDn(),
            request.getCredential() != null ? request.getCredential()
              .getBytes() : null);
        props.setAuthorizationID(sc.getAuthorizationId());
        props.setRealm(
          sc instanceof GssApiConfig ? ((GssApiConfig) sc).getRealm()
                                         : null);
        sbr = new GSSAPIBindRequest(
          props,
          config.getControlProcessor().processRequestControls(
            request.getControls()));
        break;

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " + sc.getMechanism());
      }

      sbr.setFollowReferrals(request.getFollowReferrals());
      final BindResult result = connection.bind(sbr);
      response = createResponse(request, null, result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final UnboundIDUtils util = new UnboundIDUtils();
      final com.unboundid.ldap.sdk.AddRequest ar =
        new com.unboundid.ldap.sdk.AddRequest(
          new DN(request.getDn()),
          util.fromLdapAttributes(request.getLdapAttributes()),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      ar.setFollowReferrals(request.getFollowReferrals());

      final LDAPResult result = connection.add(ar);
      response = createResponse(request, null, result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Boolean> compare(final CompareRequest request)
    throws LdapException
  {
    Response<Boolean> response = null;
    try {
      com.unboundid.ldap.sdk.CompareRequest cr;
      if (request.getAttribute().isBinary()) {
        cr = new com.unboundid.ldap.sdk.CompareRequest(
          new DN(request.getDn()),
          request.getAttribute().getName(),
          request.getAttribute().getBinaryValue(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      } else {
        cr = new com.unboundid.ldap.sdk.CompareRequest(
          new DN(request.getDn()),
          request.getAttribute().getName(),
          request.getAttribute().getStringValue(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      cr.setFollowReferrals(request.getFollowReferrals());

      final CompareResult result = connection.compare(cr);
      response = createResponse(request, result.compareMatched(), result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> delete(final DeleteRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final com.unboundid.ldap.sdk.DeleteRequest dr =
        new com.unboundid.ldap.sdk.DeleteRequest(
          new DN(request.getDn()),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      dr.setFollowReferrals(request.getFollowReferrals());

      final LDAPResult result = connection.delete(dr);
      response = createResponse(request, null, result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modify(final ModifyRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final UnboundIDUtils bu = new UnboundIDUtils();
      final com.unboundid.ldap.sdk.ModifyRequest mr =
        new com.unboundid.ldap.sdk.ModifyRequest(
          new DN(request.getDn()),
          bu.fromAttributeModification(request.getAttributeModifications()),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      mr.setFollowReferrals(request.getFollowReferrals());

      final LDAPResult result = connection.modify(mr);
      response = createResponse(request, null, result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modifyDn(final ModifyDnRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final DN dn = new DN(request.getDn());
      final DN newDn = new DN(request.getNewDn());
      final com.unboundid.ldap.sdk.ModifyDNRequest mdr =
        new com.unboundid.ldap.sdk.ModifyDNRequest(
          dn,
          newDn.getRDN(),
          request.getDeleteOldRDn(),
          newDn.getParent(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      mdr.setFollowReferrals(request.getFollowReferrals());

      final LDAPResult result = connection.modifyDN(mdr);
      response = createResponse(request, null, result);
    } catch (LDAPException e) {
      processLDAPException(request, e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final UnboundIDSearchIterator i = new UnboundIDSearchIterator(request);
    i.initialize();
    return i;
  }


  /**
   * Creates an operation response with the supplied response data.
   *
   * @param  <T>  type of response
   * @param  request  containing controls
   * @param  result  of the operation
   * @param  ldapResult  provider result
   *
   * @return  operation response
   */
  protected <T> Response<T> createResponse(
    final Request request,
    final T result,
    final LDAPResult ldapResult)
  {
    return new Response<T>(
      result,
      ResultCode.valueOf(ldapResult.getResultCode().intValue()),
      ldapResult.getDiagnosticMessage(),
      ldapResult.getMatchedDN(),
      config.getControlProcessor().processResponseControls(
        request.getControls(), ldapResult.getResponseControls()),
      ldapResult.getReferralURLs());
  }


  /**
   * Determines if the supplied ldap exception should result in an operation
   * retry.
   *
   * @param  request  that produced the exception
   * @param  e  that was produced
   *
   * @throws  LdapException  wrapping the ldap exception
   */
  protected void processLDAPException(
    final Request request, final LDAPException e)
    throws LdapException
  {
    ProviderUtils.throwOperationException(
      config.getOperationRetryResultCodes(),
      e,
      e.getResultCode().intValue(),
      e.getMatchedDN(),
      config.getControlProcessor().processResponseControls(
        request.getControls(), e.getResponseControls()),
      e.getReferralURLs(),
      true);
  }


  /**
   * Search iterator for unbound id search results.
   */
  protected class UnboundIDSearchIterator implements SearchIterator
  {

    /** Search request. */
    private final org.ldaptive.SearchRequest request;

    /** Response data. */
    private org.ldaptive.Response<Void> response;

    /** Search result iterator. */
    private SearchResultIterator resultIterator;


    /**
     * Creates a new unbound id search iterator.
     *
     * @param  sr  search request
     */
    public UnboundIDSearchIterator(final org.ldaptive.SearchRequest sr)
    {
      request = sr;
    }


    /**
     * Initializes this unbound id search iterator.
     *
     * @throws  org.ldaptive.LdapException  if an error occurs
     */
    public void initialize()
      throws org.ldaptive.LdapException
    {
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
        final SearchRequest unboundIdSr = getSearchRequest(sr, i);
        final Control[] c = config.getControlProcessor().processRequestControls(
          sr.getControls());
        unboundIdSr.addControls(c);
        i.setResult(conn.search(unboundIdSr));
      } catch (LDAPSearchException e) {
        final ResultCode rc = ignoreSearchException(
          config.getSearchIgnoreResultCodes(), e);
        if (rc == null) {
          processLDAPException(sr, e);
        }
        i.setResult(createSearchResult(e));
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
     * @throws  LDAPSearchException  if the search request cannot be initialized
     */
    protected SearchRequest getSearchRequest(
      final org.ldaptive.SearchRequest sr, final SearchResultListener listener)
      throws LDAPSearchException
    {
      String[] retAttrs = sr.getReturnAttributes();
      if (retAttrs != null && retAttrs.length == 0) {
        retAttrs = new String[] {"1.1"};
      }

      try {
        final SearchRequest req = new SearchRequest(
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
        req.setFollowReferrals(sr.getFollowReferrals());
        return req;
      } catch (LDAPException e) {
        // thrown if the filter cannot be parsed
        throw new LDAPSearchException(e);
      }
    }


    /**
     * Returns the unbound id search scope for the supplied search scope.
     *
     * @param  ss  search scope
     *
     * @return  unbound id search scope
     */
    protected SearchScope getSearchScope(final org.ldaptive.SearchScope ss)
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
    protected DereferencePolicy getDereferencePolicy(final DerefAliases deref)
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
            result.getDiagnosticMessage(),
            result.getMatchedDN(),
            respControls,
            resultIterator.getReferralURLs());
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
        processLDAPException(request, e);
      }
      return util.toLdapEntry(entry);
    }


    /**
     * Creates a search results from the supplied search exception. Used when
     * exceptions are ignored and a result should be returned to the client.
     *
     * @param  e  to convert to search result
     *
     * @return  search result
     */
    protected SearchResult createSearchResult(final LDAPSearchException e)
    {
      return new SearchResult(
        -1,
        e.getResultCode(),
        e.getDiagnosticMessage(),
        e.getMatchedDN(),
        e.getReferralURLs(),
        e.getEntryCount(),
        e.getReferenceCount(),
        e.getResponseControls());
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
      private final Queue<SearchResultEntry> responseQueue =
        new ConcurrentLinkedQueue<SearchResultEntry>();

      /** Search result. */
      private SearchResult result;

      /** Referral URLs. */
      private final List<String> referralUrls = new ArrayList<String>();


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
       * Returns any referral URLs received from search references.
       *
       * @return  referral urls
       */
      public String[] getReferralURLs()
      {
        return referralUrls.isEmpty() ? null :
          referralUrls.toArray(new String[referralUrls.size()]);
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
        logger.trace("reading search entry: {}", entry);
        responseQueue.add(entry);
      }


      /** {@inheritDoc} */
      @Override
      public void searchReferenceReturned(final SearchResultReference ref)
      {
        logger.trace("reading search reference: {}", ref);
        Collections.addAll(referralUrls, ref.getReferralURLs());
      }
    }
  }
}
