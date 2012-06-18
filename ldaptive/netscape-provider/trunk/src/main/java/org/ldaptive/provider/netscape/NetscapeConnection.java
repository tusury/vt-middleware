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
package org.ldaptive.provider.netscape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.security.auth.callback.CallbackHandler;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPConstraints;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPExtendedOperation;
import netscape.ldap.LDAPRebind;
import netscape.ldap.LDAPRebindAuth;
import netscape.ldap.LDAPReferralException;
import netscape.ldap.LDAPResponse;
import netscape.ldap.LDAPResponseListener;
import netscape.ldap.LDAPSearchConstraints;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPUrl;
import netscape.ldap.LDAPv2;
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
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.extended.ExtendedRequest;
import org.ldaptive.extended.ExtendedResponse;
import org.ldaptive.extended.ExtendedResponseFactory;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netscape provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Provider configuration. */
  private final NetscapeProviderConfig config;

  /** Operation time limit. */
  private int timeLimit;


  /**
   * Creates a new netscape ldap connection.
   *
   * @param  lc  ldap connection
   * @param  pc  provider configuration
   */
  public NetscapeConnection(
    final LDAPConnection lc, final NetscapeProviderConfig pc)
  {
    connection = lc;
    config = pc;
  }


  /**
   * Returns the operation time limit in milliseconds.
   *
   * @return  operation time limit
   */
  public int getTimeLimit()
  {
    return timeLimit;
  }


  /**
   * Sets the time limit.
   *
   * @param  limit  time in milliseconds
   */
  public void setTimeLimit(final int limit)
  {
    timeLimit = limit;
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
        if (connection.isConnected()) {
          connection.disconnect();
        }
      } catch (LDAPException e) {
        logger.warn("Error closing connection", e);
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
      final LDAPResponseListener listener = connection.bind(
        null,
        null,
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = createResponse(request, null, r);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      final LDAPResponseListener listener = connection.bind(
        request.getDn(),
        request.getCredential().getString(),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = createResponse(request, null, r);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      final SaslConfig sc = request.getSaslConfig();
      switch (sc.getMechanism()) {

      case EXTERNAL:
        connection.bind(
          null,
          new String[] {"EXTERNAL"},
          null,
          (CallbackHandler) null);
        break;

      case DIGEST_MD5:
        throw new UnsupportedOperationException("DIGEST-MD5 not supported");

      case CRAM_MD5:
        throw new UnsupportedOperationException("CRAM-MD5 not supported");

      case GSSAPI:
        throw new UnsupportedOperationException("GSSAPI not supported");

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " + sc.getMechanism());
      }
      response = new Response<Void>(null, ResultCode.SUCCESS);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      final NetscapeUtils util = new NetscapeUtils();
      final LDAPResponseListener listener = connection.add(
        new LDAPEntry(
          request.getDn(),
          util.fromLdapAttributes(request.getLdapAttributes())),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = createResponse(request, null, r);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      final NetscapeUtils util = new NetscapeUtils();
      final LDAPResponseListener listener = connection.compare(
        request.getDn(),
        util.fromLdapAttribute(request.getAttribute()),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = createResponse(
        request, ResultCode.COMPARE_TRUE.value() == r.getResultCode(), r);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      final LDAPResponseListener listener = connection.delete(
        request.getDn(),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = createResponse(request, null, r);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      final NetscapeUtils util = new NetscapeUtils();
      final LDAPResponseListener listener = connection.modify(
        request.getDn(),
        util.fromAttributeModification(request.getAttributeModifications()),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = createResponse(request, null, r);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      final String[] dn = request.getNewDn().split(",", 2);
      connection.rename(
        request.getDn(),
        dn[0],
        dn[1],
        request.getDeleteOldRDn(),
        getLDAPConstraints(request));
      response = new Response<Void>(null, ResultCode.SUCCESS);
    } catch (LDAPException e) {
      processLDAPException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final NetscapeSearchIterator i = new NetscapeSearchIterator(request);
    i.initialize();
    return i;
  }


  /** {@inheritDoc} */
  @Override
  public Response<?> extendedOperation(final ExtendedRequest request)
    throws LdapException
  {
    Response<?> response = null;
    try {
      final LDAPExtendedOperation op = connection.extendedOperation(
        new LDAPExtendedOperation(request.getOID(), request.encode()),
        getLDAPConstraints(request));
      final ExtendedResponse<?> extRes =
        ExtendedResponseFactory.createExtendedResponse(
          request.getOID(), op.getID(), op.getValue());
      response = new Response<Object>(extRes.getValue(), ResultCode.SUCCESS);
    } catch (LDAPException e) {
      processLDAPException(e);
    }
    return response;
  }


  /**
   * Creates a LDAP constraints from the supplied request.
   *
   * @param  request  to read properties from
   *
   * @return  ldap constraints
   */
  protected LDAPConstraints getLDAPConstraints(final Request request)
  {
    final LDAPConstraints cons = new LDAPConstraints();
    initializeLDAPConstraints(request, cons);
    return cons;
  }


  /**
   * Configures the supplied ldap constraints using the supplied request and
   * provider configuration.
   *
   * @param  request  to read properties froim
   * @param  cons  to configure
   */
  protected void initializeLDAPConstraints(
    final Request request, final LDAPConstraints cons)
  {
    cons.setTimeLimit(timeLimit);
    cons.setServerControls(
      config.getControlProcessor().processRequestControls(
        request.getControls()));
    if (request.getFollowReferrals()) {
      cons.setReferrals(request.getFollowReferrals());
      if (connection.getConstraints().getRebindProc() == null) {
        // configure a default rebind that uses the existing credentials
        if (connection.getAuthenticationDN() != null) {
          cons.setRebindProc(new LDAPRebind() {
            @Override
            public LDAPRebindAuth getRebindAuthentication(
              final String host, final int port)
            {
              return new LDAPRebindAuth(
                connection.getAuthenticationDN(),
                connection.getAuthenticationPassword());
            }
          });
        }
      }
    }
  }


  /**
   * Creates an operation response with the supplied response data.
   *
   * @param  <T>  type of response
   * @param  request  containing controls
   * @param  result  of the operation
   * @param  ldapResponse  provider response
   *
   * @return  operation response
   */
  protected <T> Response<T> createResponse(
    final Request request,
    final T result,
    final LDAPResponse ldapResponse)
  {
    return new Response<T>(
      result,
      ResultCode.valueOf(ldapResponse.getResultCode()),
      ldapResponse.getErrorMessage(),
      ldapResponse.getMatchedDN(),
      config.getControlProcessor().processResponseControls(
        request.getControls(), ldapResponse.getControls()),
      ldapResponse.getReferrals());
  }


  /**
   * Determines if the supplied ldap exception should result in an operation
   * retry.
   *
   * @param  e  that was produced
   *
   * @throws  LdapException  wrapping the ldap exception
   */
  protected void processLDAPException(final LDAPException e)
    throws LdapException
  {
    ProviderUtils.throwOperationException(
      config.getOperationRetryResultCodes(),
      e,
      e instanceof LDAPReferralException ?
        ResultCode.REFERRAL.value() : e.getLDAPResultCode(),
      e.getMatchedDN(),
      null,
      null,
      true);
  }


  /**
   * Search iterator for netscape search results.
   */
  protected class NetscapeSearchIterator implements SearchIterator
  {

    /** Search request. */
    private final SearchRequest request;

    /** Response data. */
    private Response<Void> response;

    /** Response result code. */
    private ResultCode responseResultCode;

    /** Referral URLs. */
    private String[] referralUrls;

    /** Ldap search results. */
    private LDAPSearchResults results;


    /**
     * Creates a new netscape search iterator.
     *
     * @param  sr  search request
     */
    public NetscapeSearchIterator(final SearchRequest sr)
    {
      request = sr;
    }


    /**
     * Initializes this netscape search iterator.
     *
     * @throws  LdapException  if an error occurs
     */
    public void initialize()
      throws LdapException
    {
      try {
        results = search(connection, request);
      } catch (LDAPException e) {
        processLDAPException(e);
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
    protected LDAPSearchResults search(
      final LDAPConnection conn,
      final SearchRequest sr)
      throws LDAPException
    {
      String[] retAttrs = sr.getReturnAttributes();
      if (retAttrs != null && retAttrs.length == 0) {
        retAttrs = new String[] {"1.1"};
      }
      return
        conn.search(
          sr.getBaseDn(),
          getSearchScope(sr.getSearchScope()),
          sr.getSearchFilter() != null ?
            sr.getSearchFilter().format() : null,
          retAttrs,
          sr.getTypesOnly(),
          getLDAPSearchConstraints(request));
    }


    /**
     * Returns a netscape search request object configured with the supplied
     * search request.
     *
     * @param  sr  search request containing configuration to create netscape
     * search request
     *
     * @return  search request
     *
     * @throws  LDAPException  if the search request cannot be initialized
     */
    protected LDAPSearchConstraints getLDAPSearchConstraints(
      final SearchRequest sr)
      throws LDAPException
    {
      final LDAPSearchConstraints cons = new LDAPSearchConstraints();
      initializeLDAPConstraints(sr, cons);
      cons.setDereference(getDereference(request.getDerefAliases()));
      cons.setMaxResults((int) request.getSizeLimit());
      cons.setServerTimeLimit((int) request.getTimeLimit());
      return cons;
    }


    /**
     * Returns the netscape search scope for the supplied search scope.
     *
     * @param  ss  search scope
     *
     * @return  netscape search scope
     */
    protected int getSearchScope(final SearchScope ss)
    {
      int scope = LDAPv2.SCOPE_SUB;
      if (ss == SearchScope.OBJECT) {
        scope = LDAPv2.SCOPE_BASE;
      } else if (ss == SearchScope.ONELEVEL) {
        scope = LDAPv2.SCOPE_ONE;
      } else if (ss == SearchScope.SUBTREE) {
        scope = LDAPv2.SCOPE_SUB;
      }
      return scope;
    }


    /**
     * Returns the netscape deference policy for the supplied deref aliases.
     *
     * @param  da  deref aliases
     *
     * @return  netscape deref constant
     */
    protected int getDereference(final DerefAliases da)
    {
      int deref = LDAPv2.DEREF_NEVER;
      if (da == DerefAliases.ALWAYS) {
        deref = LDAPv2.DEREF_ALWAYS;
      } else if (da == DerefAliases.FINDING) {
        deref = LDAPv2.DEREF_FINDING;
      } else if (da == DerefAliases.NEVER) {
        deref = LDAPv2.DEREF_NEVER;
      } else if (da == DerefAliases.SEARCHING) {
        deref = LDAPv2.DEREF_SEARCHING;
      }
      return deref;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
      throws LdapException
    {
      if (results == null || response != null) {
        return false;
      }

      boolean more = false;
      try {
        more = results.hasMoreElements();
        if (!more) {
          final ResponseControl[] respControls =
            config.getControlProcessor().processResponseControls(
              request.getControls(),
              results.getResponseControls());
          final boolean searchAgain = ControlProcessor.searchAgain(
            respControls);
          if (searchAgain) {
            results = search(connection, request);
            more = results.hasMoreElements();
          }
          if (!more) {
            response = new Response<Void>(
              null,
              responseResultCode != null ? responseResultCode
                : ResultCode.SUCCESS,
              null,
              null,
              respControls,
              referralUrls);
          }
        }
      } catch (LDAPException e) {
        final ResponseControl[] respControls =
          config.getControlProcessor().processResponseControls(
            request.getControls(), results.getResponseControls());
        final ResultCode rc = ignoreSearchException(
          config.getSearchIgnoreResultCodes(), e);
        if (rc == null) {
          processLDAPException(e);
        }
        response = new Response<Void>(
          null, rc, null, null, respControls, referralUrls);
      }
      return more;
    }


    /** {@inheritDoc} */
    @Override
    public LdapEntry next()
      throws LdapException
    {
      final NetscapeUtils bu = new NetscapeUtils(request.getSortBehavior());
      bu.setBinaryAttributes(request.getBinaryAttributes());

      LdapEntry le = null;
      try {
        final LDAPEntry entry = results.next();
        logger.trace("reading search entry: {}", entry);
        le = bu.toLdapEntry(entry);
      } catch (LDAPReferralException e) {
        logger.trace(
          "reading search reference: {}", Arrays.toString(e.getURLs()));
        final List<String> urls = new ArrayList<String>();
        for (LDAPUrl url : e.getURLs()) {
          urls.add(url.getUrl());
        }
        referralUrls = LdapUtils.concatArrays(
          urls.toArray(new String[urls.size()]), referralUrls);
        responseResultCode = ResultCode.valueOf(e.getLDAPResultCode());
      } catch (LDAPException e) {
        final ResultCode rc = ignoreSearchException(
          config.getSearchIgnoreResultCodes(), e);
        if (rc == null) {
          processLDAPException(e);
        }
        response = new Response<Void>(
          null,
          rc,
          null,
          null,
          config.getControlProcessor().processResponseControls(
            request.getControls(), results.getResponseControls()),
          referralUrls);
      }
      return le;
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
          if (e.getLDAPResultCode() == rc.value()) {
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
    public Response<Void> getResponse()
    {
      return response;
    }


    /** {@inheritDoc} */
    @Override
    public void close()
      throws LdapException {}
  }
}
