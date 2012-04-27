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
package org.ldaptive.provider.opendj;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.DereferenceAliasesPolicy;
import org.forgerock.opendj.ldap.ErrorResultException;
import org.forgerock.opendj.ldap.Modification;
import org.forgerock.opendj.ldap.SearchResultHandler;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.controls.Control;
import org.forgerock.opendj.ldap.requests.GSSAPISASLBindRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.requests.SearchRequest;
import org.forgerock.opendj.ldap.requests.SimpleBindRequest;
import org.forgerock.opendj.ldap.responses.BindResult;
import org.forgerock.opendj.ldap.responses.CompareResult;
import org.forgerock.opendj.ldap.responses.Result;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldap.responses.SearchResultReference;
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
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.sasl.QualityOfProtection;
import org.ldaptive.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenDJ provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDJConnection implements org.ldaptive.provider.Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private Connection connection;

  /** Provider configuration. */
  private final OpenDJProviderConfig config;


  /**
   * Creates a new opendj ldap connection.
   *
   * @param  c  ldap connection
   * @param  pc  provider configuration
   */
  public OpenDJConnection(final Connection c, final OpenDJProviderConfig pc)
  {
    connection = c;
    config = pc;
  }


  /**
   * Returns the underlying ldap connection.
   *
   * @return  ldap connection
   */
  public Connection getLdapConnection()
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
      final SimpleBindRequest sbr = Requests.newSimpleBindRequest();
      if (request.getControls() != null) {
        for (Control c :
             config.getControlProcessor().processRequestControls(
               request.getControls())) {
          sbr.addControl(c);
        }
      }

      final BindResult result = connection.bind(sbr);
      response = createResponse(request, null, result);
    } catch (ErrorResultException e) {
      processErrorResultException(request, e);
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
      final SimpleBindRequest sbr = Requests.newSimpleBindRequest(
        request.getDn(), request.getCredential().getChars());
      if (request.getControls() != null) {
        for (Control c :
             config.getControlProcessor().processRequestControls(
               request.getControls())) {
          sbr.addControl(c);
        }
      }

      final BindResult result = connection.bind(sbr);
      response = createResponse(request, null, result);
    } catch (ErrorResultException e) {
      processErrorResultException(request, e);
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
    /*
    Response<Void> response = null;
    SASLBindRequest sbr = null;
    final ByteStringBuilder builder = new ByteStringBuilder();
    */
    final SaslConfig sc = request.getSaslConfig();
    switch (sc.getMechanism()) {

    case EXTERNAL:
      throw new UnsupportedOperationException("SASL External not supported");
      /*
      sbr = Requests.newExternalSASLBindRequest();
      ((ExternalSASLBindRequest) sbr).setAuthorizationID(
        sc.getAuthorizationId());
      break;
      */

    case DIGEST_MD5:
      throw new UnsupportedOperationException("DIGEST-MD5 not supported");
      // returns a result code 82 (Local Error) when the server returns 49
      /*
      builder.append(
        request.getCredential() != null ?
          request.getCredential().getBytes(): new byte[0]);
      sbr = Requests.newDigestMD5SASLBindRequest(
        request.getDn() != null ? request.getDn() : "",
        builder.toByteString());
      String digestMd5Realm = sc instanceof DigestMd5Config
        ? ((DigestMd5Config) sc).getRealm() : null;
      if (digestMd5Realm == null && request.getDn().contains("@")) {
        digestMd5Realm = request.getDn().substring(
          request.getDn().indexOf("@") + 1);
      }
      if (digestMd5Realm != null) {
        ((DigestMD5SASLBindRequest) sbr).setRealm(digestMd5Realm);
      }
      break;
      */

    case CRAM_MD5:
      throw new UnsupportedOperationException("CRAM-MD5 not supported");
      // LDAP reports: error: SASL bind in progress (tag=99)
      /*
      builder.append(
        request.getCredential() != null ?
          request.getCredential().getBytes(): new byte[0]);
      sbr = Requests.newCRAMMD5SASLBindRequest(
        request.getDn() != null ? request.getDn() : "",
        builder.toByteString());
      break;
      */

    case GSSAPI:
      throw new UnsupportedOperationException("GSSAPI not supported");
      /*
      builder.append(
        request.getCredential() != null ?
          request.getCredential().getBytes() : new byte[0]);
      sbr = Requests.newGSSAPISASLBindRequest(
        request.getDn() != null ? request.getDn() : "",
        builder.toByteString());
      ((GSSAPISASLBindRequest) sbr).setAuthorizationID(
        sc.getAuthorizationId());
      final String gssApiRealm = sc instanceof GssApiConfig
        ? ((GssApiConfig) sc).getRealm() : null;
      if (gssApiRealm != null) {
        ((GSSAPISASLBindRequest) sbr).setRealm(gssApiRealm);
      }
      if (sc.getQualityOfProtection() != null) {
        ((GSSAPISASLBindRequest) sbr).addQOP(
          getQualityOfProtection(sc.getQualityOfProtection()));
      }
      break;
      */

    default:
      throw new IllegalArgumentException(
        "Unknown SASL authentication mechanism: " + sc.getMechanism());
    }

    /*
    if (request.getControls() != null) {
      for (Control c :
           config.getControlProcessor().processRequestControls(
             request.getControls())) {
        sbr.addControl(c);
      }
    }

    try {
      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResult().getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          e.getResult().getControls().toArray(new Control[0])),
        true);
    } catch (InterruptedException e) {
      throw new LdapException(e);
    }
    return response;
    */
  }


  /**
   * Returns the SASL quality of protection string for the supplied enum.
   *
   * @param  qop  quality of protection enum
   *
   * @return  SASL quality of protection string
   */
  protected static String getQualityOfProtection(final QualityOfProtection qop)
  {
    String name;
    switch (qop) {

    case AUTH:
      name = GSSAPISASLBindRequest.QOP_AUTH;
      break;

    case AUTH_INT:
      name = GSSAPISASLBindRequest.QOP_AUTH_INT;
      break;

    case AUTH_CONF:
      name = GSSAPISASLBindRequest.QOP_AUTH_CONF;
      break;

    default:
      throw new IllegalArgumentException(
        "Unknown SASL quality of protection: " + qop);
    }
    return name;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final OpenDJUtils util = new OpenDJUtils();
      final org.forgerock.opendj.ldap.requests.AddRequest ar =
        Requests.newAddRequest(
          util.fromLdapEntry(
            new LdapEntry(request.getDn(), request.getLdapAttributes())));
      if (request.getControls() != null) {
        for (Control c :
             config.getControlProcessor().processRequestControls(
               request.getControls())) {
          ar.addControl(c);
        }
      }

      final Result result = connection.add(ar);
      response = createResponse(request, null, result);
    } catch (ErrorResultException e) {
      processErrorResultException(request, e);
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
      final OpenDJUtils util = new OpenDJUtils();
      org.forgerock.opendj.ldap.requests.CompareRequest cr;
      if (request.getAttribute().isBinary()) {
        cr = Requests.newCompareRequest(
          request.getDn(),
          request.getAttribute().getName(),
          util.fromBinaryValues(request.getAttribute().getBinaryValues())[0]);
      } else {
        cr = Requests.newCompareRequest(
          request.getDn(),
          request.getAttribute().getName(),
          util.fromStringValues(request.getAttribute().getStringValues())[0]);
      }
      if (request.getControls() != null) {
        for (Control c :
             config.getControlProcessor().processRequestControls(
               request.getControls())) {
          cr.addControl(c);
        }
      }

      final CompareResult result = connection.compare(cr);
      response = createResponse(request, result.matched(), result);
    } catch (ErrorResultException e) {
      processErrorResultException(request, e);
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
      final org.forgerock.opendj.ldap.requests.DeleteRequest dr =
        Requests.newDeleteRequest(request.getDn());
      if (request.getControls() != null) {
        for (Control c :
             config.getControlProcessor().processRequestControls(
               request.getControls())) {
          dr.addControl(c);
        }
      }

      final Result result = connection.delete(dr);
      response = createResponse(request, null, result);
    } catch (ErrorResultException e) {
      processErrorResultException(request, e);
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
      final OpenDJUtils util = new OpenDJUtils();
      final org.forgerock.opendj.ldap.requests.ModifyRequest mr =
        Requests.newModifyRequest(request.getDn());
      for (Modification m :
           util.fromAttributeModification(
             request.getAttributeModifications())) {
        mr.addModification(m);
      }
      if (request.getControls() != null) {
        for (Control c :
             config.getControlProcessor().processRequestControls(
               request.getControls())) {
          mr.addControl(c);
        }
      }

      final Result result = connection.modify(mr);
      response = createResponse(request, null, result);
    } catch (ErrorResultException e) {
      processErrorResultException(request, e);
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
      final org.forgerock.opendj.ldap.requests.ModifyDNRequest mdr =
          Requests.newModifyDNRequest(request.getDn(), request.getNewDn());
      mdr.setDeleteOldRDN(request.getDeleteOldRDn());
      if (request.getControls() != null) {
        for (Control c :
             config.getControlProcessor().processRequestControls(
               request.getControls())) {
          mdr.addControl(c);
        }
      }

      final Result result = connection.modifyDN(mdr);
      response = createResponse(request, null, result);
    } catch (ErrorResultException e) {
      processErrorResultException(request, e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final OpenDJSearchIterator i = new OpenDJSearchIterator(request);
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
    final Result ldapResult)
  {
    final List<Control> ctls = ldapResult.getControls();
    final List<String> urls = ldapResult.getReferralURIs();
    return new Response<T>(
      result,
      ResultCode.valueOf(ldapResult.getResultCode().intValue()),
      ldapResult.getDiagnosticMessage(),
      ldapResult.getMatchedDN(),
      config.getControlProcessor().processResponseControls(
        request.getControls(), ctls.toArray(new Control[ctls.size()])),
      urls.toArray(new String[urls.size()]));
  }


  /**
   * Determines if the supplied error result exception should result in an
   * operation retry.
   *
   * @param  request  that produced the exception
   * @param  e  that was produced
   *
   * @throws  LdapException  wrapping the error result exception
   */
  protected void processErrorResultException(
    final Request request, final ErrorResultException e)
    throws LdapException
  {
    final List<Control> ctls = e.getResult().getControls();
    final List<String> urls = e.getResult().getReferralURIs();
    ProviderUtils.throwOperationException(
      config.getOperationRetryResultCodes(),
      e,
      e.getResult().getResultCode().intValue(),
      e.getResult().getMatchedDN(),
      config.getControlProcessor().processResponseControls(
        request.getControls(),
        ctls.toArray(new Control[ctls.size()])),
      urls.toArray(new String[urls.size()]),
      true);
  }


  /**
   * Search iterator for opendj search results.
   */
  protected class OpenDJSearchIterator implements SearchIterator
  {

    /** Search request. */
    private final org.ldaptive.SearchRequest request;

    /** Response data. */
    private org.ldaptive.Response<Void> response;

    /** Search result iterator. */
    private SearchResultIterator resultIterator;


    /**
     * Creates a new opendj search iterator.
     *
     * @param  sr  search request
     */
    public OpenDJSearchIterator(final org.ldaptive.SearchRequest sr)
    {
      request = sr;
    }


    /**
     * Initializes this opendj search iterator.
     *
     * @throws  LdapException  if an error occurs
     */
    public void initialize()
      throws LdapException
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
      final Connection conn, final org.ldaptive.SearchRequest sr)
      throws LdapException
    {
      final SearchRequest opendjSr = getSearchRequest(sr);
      if (sr.getControls() != null) {
        for (Control c :
          config.getControlProcessor().processRequestControls(
            sr.getControls())) {
          opendjSr.addControl(c);
        }
      }
      final SearchResultIterator i = new SearchResultIterator();
      try {
        conn.search(opendjSr, i);
      } catch (ErrorResultException e) {
        final ResultCode rc = ignoreSearchException(
          config.getSearchIgnoreResultCodes(), e);
        if (rc == null) {
          processErrorResultException(request, e);
        }
      }
      return i;
    }


    /**
     * Returns an opendj search request object configured with the supplied
     * search request.
     *
     * @param  sr  search request containing configuration to create opendj
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

      final SearchRequest opendjSr = Requests.newSearchRequest(
        sr.getBaseDn(),
        getSearchScope(sr.getSearchScope()),
        sr.getSearchFilter() != null ? sr.getSearchFilter().format() : null,
        retAttrs);
      opendjSr.setDereferenceAliasesPolicy(
        getDereferencePolicy(sr.getDerefAliases()));
      opendjSr.setSizeLimit((int) sr.getSizeLimit());
      opendjSr.setTimeLimit((int) sr.getTimeLimit());
      opendjSr.setTypesOnly(sr.getTypesOnly());
      return opendjSr;
    }


    /**
     * Returns the opendj search scope for the supplied search scope.
     *
     * @param  ss  search scope
     *
     * @return  opendj search scope
     */
    protected SearchScope getSearchScope(
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
     * Returns the opendj deference policy for the supplied deref aliases.
     *
     * @param  deref  deref aliases
     *
     * @return  dereference policy
     */
    protected DereferenceAliasesPolicy getDereferencePolicy(
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
        final List<Control> ctls = result.getControls();
        final ResponseControl[] respControls =
          config.getControlProcessor().processResponseControls(
            request.getControls(), ctls.toArray(new Control[ctls.size()]));
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
      throws LdapException
    {
      final OpenDJUtils util = new OpenDJUtils(request.getSortBehavior());
      util.setBinaryAttributes(request.getBinaryAttributes());
      final SearchResultEntry entry = resultIterator.getSearchResultEntry();
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

      /** Referral URLs. */
      private final List<String> referralUrls = new ArrayList<String>();


      /**
       * Returns the next search result entry from the queue.
       *
       * @return  search result entry
       */
      public SearchResultEntry getSearchResultEntry()
      {
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
        logger.trace("reading search entry: {}", entry);
        responseQueue.add(entry);
        return true;
      }


      /** {@inheritDoc} */
      @Override
      public boolean handleReference(final SearchResultReference ref)
      {
        logger.trace("reading search reference: {}", ref);
        if (request.getFollowReferrals()) {
          throw new UnsupportedOperationException(
            "Referral following not supported");
        }
        for (String s : ref.getURIs()) {
          referralUrls.add(s);
        }
        return true;
      }
    }
  }
}
