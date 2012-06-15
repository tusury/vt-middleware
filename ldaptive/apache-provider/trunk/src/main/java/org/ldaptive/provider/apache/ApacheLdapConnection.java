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
package org.ldaptive.provider.apache;

import java.io.IOException;
import java.util.Map;
import org.apache.directory.ldap.client.api.CramMd5Request;
import org.apache.directory.ldap.client.api.DigestMd5Request;
import org.apache.directory.ldap.client.api.GssApiRequest;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.shared.ldap.model.cursor.SearchCursor;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.exception.LdapOperationException;
import org.apache.directory.shared.ldap.model.message.AddRequestImpl;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.AliasDerefMode;
import org.apache.directory.shared.ldap.model.message.BindRequestImpl;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareRequestImpl;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.message.Referral;
import org.apache.directory.shared.ldap.model.message.ResultResponse;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.model.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;
import org.apache.directory.shared.ldap.model.message.SearchScope;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.LdapUtils;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Request;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.control.RequestControl;
import org.ldaptive.extended.ExtendedRequest;
import org.ldaptive.extended.ExtendedResponseFactory;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache LDAP provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LdapNetworkConnection connection;

  /** Provider configuration. */
  private final ApacheLdapProviderConfig config;


  /**
   * Creates a new apache ldap connection.
   *
   * @param  lc  ldap connection
   * @param  pc  provider configuration
   */
  public ApacheLdapConnection(
    final LdapNetworkConnection lc, final ApacheLdapProviderConfig pc)
  {
    connection = lc;
    config = pc;
  }


  /**
   * Returns the underlying ldap connection.
   *
   * @return  ldap connection
   */
  public LdapNetworkConnection getLdapConnection()
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
        if (connection.isConnected()) {
          connection.unBind();
        }
      } catch (
        org.apache.directory.shared.ldap.model.exception.LdapException e) {
        logger.error("Error unbinding from LDAP", e);
      }
      try {
        connection.close();
      } catch (IOException e) {
        throw new LdapException(e);
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
    if (request.getFollowReferrals()) {
      throw new UnsupportedOperationException(
        "Referral following not supported");
    }
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
      final BindRequestImpl bri = new BindRequestImpl();
      if (request.getControls() != null) {
        bri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }

      final BindResponse br = connection.bind(bri);
      throwOperationException(request, br);
      response = createResponse(request, null, br);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
      throw new LdapException(e);
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
      final BindRequestImpl bri = new BindRequestImpl();
      if (request.getControls() != null) {
        bri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      bri.setVersion3(true);
      bri.setSimple(true);
      bri.setDn(new Dn(request.getDn()));
      bri.setCredentials(request.getCredential().getBytes());

      final BindResponse br = connection.bind(bri);
      throwOperationException(request, br);
      response = createResponse(request, null, br);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
      throw new LdapException(e);
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
      BindResponse br;
      switch (request.getSaslConfig().getMechanism()) {

      case EXTERNAL:
        throw new UnsupportedOperationException("SASL External not supported");

      case DIGEST_MD5:

        final DigestMd5Request digestMd5Request = ApacheLdapSaslUtils
          .createDigestMd5Request(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(digestMd5Request);
        break;

      case CRAM_MD5:

        final CramMd5Request cramMd5Request = ApacheLdapSaslUtils
          .createCramMd5Request(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(cramMd5Request);
        break;

      case GSSAPI:

        final GssApiRequest gssApiRequest = ApacheLdapSaslUtils
          .createGssApiRequest(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(gssApiRequest);
        break;

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " +
          request.getSaslConfig().getMechanism());
      }
      throwOperationException(request, br);
      response = createResponse(request, null, br);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    if (request.getFollowReferrals()) {
      throw new UnsupportedOperationException(
        "Referral following not supported");
    }
    Response<Void> response = null;
    try {
      final ApacheLdapUtils bu = new ApacheLdapUtils();
      final AddRequestImpl ari = new AddRequestImpl();
      if (request.getControls() != null) {
        ari.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      ari.setEntry(
        bu.fromLdapEntry(
          new LdapEntry(request.getDn(), request.getLdapAttributes())));

      final AddResponse ar = connection.add(ari);
      throwOperationException(request, ar);
      response = createResponse(request, null, ar);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Boolean> compare(final CompareRequest request)
    throws LdapException
  {
    if (request.getFollowReferrals()) {
      throw new UnsupportedOperationException(
        "Referral following not supported");
    }
    Response<Boolean> response = null;
    try {
      final CompareRequestImpl cri = new CompareRequestImpl();
      if (request.getControls() != null) {
        cri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      cri.setName(new Dn(request.getDn()));
      cri.setAttributeId(request.getAttribute().getName());
      if (request.getAttribute().isBinary()) {
        cri.setAssertionValue(request.getAttribute().getBinaryValue());
      } else {
        cri.setAssertionValue(request.getAttribute().getStringValue());
      }

      final CompareResponse cr = connection.compare(cri);
      throwOperationException(request, cr);
      response = createResponse(request, cr.isTrue(), cr);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> delete(final DeleteRequest request)
    throws LdapException
  {
    if (request.getFollowReferrals()) {
      throw new UnsupportedOperationException(
        "Referral following not supported");
    }
    Response<Void> response = null;
    try {
      final DeleteRequestImpl dri = new DeleteRequestImpl();
      if (request.getControls() != null) {
        dri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      dri.setName(new Dn(request.getDn()));

      final DeleteResponse dr = connection.delete(dri);
      throwOperationException(request, dr);
      response = createResponse(request, null, dr);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modify(final ModifyRequest request)
    throws LdapException
  {
    if (request.getFollowReferrals()) {
      throw new UnsupportedOperationException(
        "Referral following not supported");
    }
    Response<Void> response = null;
    try {
      final ApacheLdapUtils bu = new ApacheLdapUtils();
      final ModifyRequestImpl mri = new ModifyRequestImpl();
      if (request.getControls() != null) {
        mri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      mri.setName(new Dn(request.getDn()));
      for (
        Modification m :
        bu.fromAttributeModification(request.getAttributeModifications())) {
        mri.addModification(m);
      }

      final ModifyResponse mr = connection.modify(mri);
      throwOperationException(request, mr);
      response = createResponse(request, null, mr);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modifyDn(final ModifyDnRequest request)
    throws LdapException
  {
    if (request.getFollowReferrals()) {
      throw new UnsupportedOperationException(
        "Referral following not supported");
    }
    Response<Void> response = null;
    try {
      final Dn dn = new Dn(request.getDn());
      final Dn newDn = new Dn(request.getNewDn());
      final ModifyDnRequestImpl mdri = new ModifyDnRequestImpl();
      if (request.getControls() != null) {
        mdri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      mdri.setName(dn);
      mdri.setNewRdn(newDn.getRdn());
      mdri.setNewSuperior(newDn.getParent());
      mdri.setDeleteOldRdn(request.getDeleteOldRDn());

      final ModifyDnResponse mdr = connection.modifyDn(mdri);
      throwOperationException(request, mdr);
      response = createResponse(request, null, mdr);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final ApacheLdapSearchIterator i = new ApacheLdapSearchIterator(request);
    i.initialize();
    return i;
  }


  /** {@inheritDoc} */
  @Override
  public Response<org.ldaptive.extended.ExtendedResponse> extendedOperation(
    final ExtendedRequest request)
    throws LdapException
  {
    if (request.getFollowReferrals()) {
      throw new UnsupportedOperationException(
        "Referral following not supported");
    }
    Response<org.ldaptive.extended.ExtendedResponse> response = null;
    try {
      final ExtendedResponse er = connection.extended(
        request.getOID(), request.encode());
      throwOperationException(request, er);
      // only supports response without any value
      response = createResponse(
        request,
        ExtendedResponseFactory.createExtendedResponse(
          request.getOID(), er.getResponseName(), null),
        er);
    } catch (LdapOperationException e) {
      processLdapOperationException(e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /**
   * Determines if the supplied response should result in an operation retry.
   *
   * @param  request  that produced the exception
   * @param  resultResponse  provider response
   *
   * @throws  LdapException  wrapping the ldap exception
   */
  protected void throwOperationException(
    final Request request, final ResultResponse resultResponse)
    throws LdapException
  {
    final LdapResult ldapResult = resultResponse.getLdapResult();
    final Referral ref = ldapResult.getReferral();
    ProviderUtils.throwOperationException(
      config.getOperationRetryResultCodes(),
      String.format(
        "Ldap returned result code: %s", ldapResult.getResultCode()),
      ldapResult.getResultCode().getResultCode(),
      ldapResult.getMatchedDn().getName(),
      processResponseControls(
        config.getControlProcessor(), request.getControls(), resultResponse),
      ref != null ?
        ref.getLdapUrls().toArray(new String[ref.getReferralLength()]) : null,
      false);
  }


  /**
   * Creates an operation response with the supplied response data.
   *
   * @param  <T>  type of response
   * @param  request  containing controls
   * @param  result  of the operation
   * @param  resultResponse  provider response
   *
   * @return  operation response
   */
  protected <T> Response<T> createResponse(
    final Request request,
    final T result,
    final ResultResponse resultResponse)
  {
    final LdapResult ldapResult = resultResponse.getLdapResult();
    final Referral ref = ldapResult.getReferral();
    return new Response<T>(
      result,
      ResultCode.valueOf(ldapResult.getResultCode().getValue()),
      ldapResult.getDiagnosticMessage(),
      ldapResult.getMatchedDn().getName(),
      processResponseControls(
        config.getControlProcessor(), request.getControls(), resultResponse),
      ref != null ?
        ref.getLdapUrls().toArray(new String[ref.getReferralLength()]) : null);
  }


  /**
   * Determines if the supplied ldap exception should result in an operation
   * retry.
   *
   * @param  e  that was produced
   *
   * @throws  LdapException  wrapping the ldap exception
   */
  protected void processLdapOperationException(final LdapOperationException e)
    throws LdapException
  {
    ProviderUtils.throwOperationException(
      config.getOperationRetryResultCodes(),
      e,
      e.getResultCode().getResultCode(),
      e.getResolvedDn().getName(),
      null,
      null,
      true);
  }


  /**
   * Retrieves the response controls from the supplied response and processes
   * them with the supplied control processor.
   *
   * @param  processor  control processor
   * @param  requestControls  that produced this response
   * @param  response  to get controls from
   *
   * @return  response controls
   */
  public org.ldaptive.control.ResponseControl[] processResponseControls(
    final ControlProcessor<Control> processor,
    final RequestControl[] requestControls,
    final Message response)
  {
    return
      processor.processResponseControls(
        requestControls,
        getResponseControls(response));
  }


  /**
   * Retrieves the response controls from the supplied response.
   *
   * @param  response  to get controls from
   *
   * @return  response controls
   */
  public Control[] getResponseControls(final Message response)
  {
    Control[] ctls = null;
    if (response != null) {
      final Map<String, Control> respControls = response.getControls();
      ctls = respControls.values().toArray(new Control[respControls.size()]);
    }
    return ctls;
  }


  /**
   * Search iterator for apache ldap search results.
   */
  protected class ApacheLdapSearchIterator implements SearchIterator
  {

    /** Search request. */
    private final org.ldaptive.SearchRequest request;

    /** Response data. */
    private org.ldaptive.Response<Void> response;

    /** Referral URLs. */
    private String[] referralUrls;

    /** Ldap search cursor. */
    private SearchCursor cursor;


    /**
     * Creates a new apache ldap search iterator.
     *
     * @param  sr  search request
     */
    public ApacheLdapSearchIterator(final org.ldaptive.SearchRequest sr)
    {
      request = sr;
    }


    /**
     * Initializes this apache ldap search iterator.
     *
     * @throws  org.ldaptive.LdapException  if an error occurs
     */
    public void initialize()
      throws org.ldaptive.LdapException
    {
      boolean closeCursor = false;
      try {
        cursor = search(connection, request);
      } catch (LdapOperationException e) {
        closeCursor = true;
        processLdapOperationException(e);
      } catch (RuntimeException e) {
        closeCursor = true;
        throw e;
      } catch (Exception e) {
        closeCursor = true;
        throw new org.ldaptive.LdapException(e);
      } finally {
        if (closeCursor) {
          try {
            if (cursor != null) {
              cursor.close();
            }
          } catch (Exception e) {
            logger.debug("Error closing search cursor", e);
          }
        }
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
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     * if an error occurs
     */
    protected SearchCursor search(
      final LdapConnection conn,
      final org.ldaptive.SearchRequest sr)
      throws org.apache.directory.shared.ldap.model.exception.LdapException
    {
      final SearchRequest apacheSr = getSearchRequest(sr);
      final Control[] c = config.getControlProcessor().processRequestControls(
        sr.getControls());
      if (c != null) {
        apacheSr.addAllControls(c);
      }
      return conn.search(apacheSr);
    }


    /**
     * Returns an apache ldap search request object configured with the supplied
     * search request.
     *
     * @param  sr  search request containing configuration to create apache ldap
     * search request
     *
     * @return  search request
     *
     * @throws  org.apache.directory.shared.ldap.model.exception.LdapException
     * if the search request cannot be initialized
     */
    protected SearchRequest getSearchRequest(
      final org.ldaptive.SearchRequest sr)
      throws org.apache.directory.shared.ldap.model.exception.LdapException
    {
      final SearchRequest apacheSr = new SearchRequestImpl();
      if (sr.getReturnAttributes() != null) {
        if (sr.getReturnAttributes().length == 0) {
          apacheSr.addAttributes("1.1");
        } else {
          apacheSr.addAttributes(sr.getReturnAttributes());
        }
      }
      apacheSr.setBase(new Dn(sr.getBaseDn()));

      final AliasDerefMode deref = getAliasDerefMode(sr.getDerefAliases());
      if (deref != null) {
        apacheSr.setDerefAliases(deref);
      }
      if (sr.getSearchFilter() != null) {
        apacheSr.setFilter(sr.getSearchFilter().format());
      }

      final SearchScope searchScope = getSearchScope(sr.getSearchScope());
      if (searchScope != null) {
        apacheSr.setScope(searchScope);
      }
      apacheSr.setSizeLimit(sr.getSizeLimit());
      apacheSr.setTimeLimit(Long.valueOf(sr.getTimeLimit()).intValue());
      apacheSr.setTypesOnly(sr.getTypesOnly());
      return apacheSr;
    }


    /**
     * Returns the apache ldap search scope for the supplied search scope.
     *
     * @param  ss  search scope
     *
     * @return  apache ldap search scope
     */
    protected SearchScope getSearchScope(
      final org.ldaptive.SearchScope ss)
    {
      SearchScope scope = null;
      if (ss == org.ldaptive.SearchScope.OBJECT) {
        scope = SearchScope.OBJECT;
      } else if (ss == org.ldaptive.SearchScope.ONELEVEL) {
        scope = SearchScope.ONELEVEL;
      } else if (ss == org.ldaptive.SearchScope.SUBTREE) {
        scope = SearchScope.SUBTREE;
      }
      return scope;
    }


    /**
     * Returns the apache ldap alias deref mode for the supplied deref aliases.
     *
     * @param  deref  deref aliases
     *
     * @return  apache ldap alias deref mode
     */
    protected AliasDerefMode getAliasDerefMode(final DerefAliases deref)
    {
      AliasDerefMode mode = null;
      if (deref == DerefAliases.ALWAYS) {
        mode = AliasDerefMode.DEREF_ALWAYS;
      } else if (deref == DerefAliases.FINDING) {
        mode = AliasDerefMode.DEREF_FINDING_BASE_OBJ;
      } else if (deref == DerefAliases.NEVER) {
        mode = AliasDerefMode.NEVER_DEREF_ALIASES;
      } else if (deref == DerefAliases.SEARCHING) {
        mode = AliasDerefMode.DEREF_IN_SEARCHING;
      }
      return mode;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
      throws org.ldaptive.LdapException
    {
      if (cursor == null || response != null) {
        return false;
      }

      boolean more = false;
      try {
        more = cursor.next();
        if (!more) {
          final SearchResultDone done = cursor.getSearchResultDone();
          final org.ldaptive.control.ResponseControl[] respControls =
            processResponseControls(
              config.getControlProcessor(), request.getControls(), done);
          final boolean searchAgain = ControlProcessor.searchAgain(
            respControls);
          if (searchAgain) {
            cursor = search(connection, request);
            more = cursor.next();
          }
          if (!more) {
            throwOperationException(request, done);
            final LdapResult ldapResult = done.getLdapResult();
            final Referral ref = ldapResult.getReferral();
            if (ref != null) {
              if (request.getFollowReferrals()) {
                throw new UnsupportedOperationException(
                  "Referral following not supported");
              }
              referralUrls = LdapUtils.concatArrays(ref.getLdapUrls().toArray(
                new String[ref.getReferralLength()]), referralUrls);
            }
            response = new Response<Void>(
              null,
              ResultCode.valueOf(ldapResult.getResultCode().getValue()),
              ldapResult.getDiagnosticMessage(),
              ldapResult.getMatchedDn().getName(),
              respControls,
              referralUrls);
          }
        }
      } catch (LdapOperationException e) {
        processLdapOperationException(e);
      } catch (org.ldaptive.LdapException e) {
        throw e;
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new org.ldaptive.LdapException(e);
      }
      return more;
    }


    /** {@inheritDoc} */
    @Override
    public LdapEntry next()
      throws org.ldaptive.LdapException
    {
      final ApacheLdapUtils bu = new ApacheLdapUtils(request.getSortBehavior());
      bu.setBinaryAttributes(request.getBinaryAttributes());

      LdapEntry le = null;
      try {
        if (cursor.isEntry()) {
          final Entry entry = cursor.getEntry();
          logger.trace("reading search entry: {}", entry);
          le = bu.toLdapEntry(entry);
        } else if (cursor.isReferral()) {
          if (request.getFollowReferrals()) {
            throw new UnsupportedOperationException(
              "Referral following not supported");
          }
          final Referral ref = cursor.getReferral();
          logger.trace("reading search reference: {}", ref);
          referralUrls = LdapUtils.concatArrays(ref.getLdapUrls().toArray(
            new String[ref.getReferralLength()]), referralUrls);
        } else if (cursor.isIntermediate()) {
          throw new UnsupportedOperationException("Intermediate response");
        }
      } catch (LdapOperationException e) {
        processLdapOperationException(e);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new org.ldaptive.LdapException(e);
      }
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
      throws org.ldaptive.LdapException {}
  }
}
