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
package org.ldaptive.provider.netscape;

import javax.security.auth.callback.CallbackHandler;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPConstraints;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPExtendedOperation;
import netscape.ldap.LDAPMessage;
import netscape.ldap.LDAPRebind;
import netscape.ldap.LDAPRebindAuth;
import netscape.ldap.LDAPReferralException;
import netscape.ldap.LDAPResponse;
import netscape.ldap.LDAPResponseListener;
import netscape.ldap.LDAPSearchConstraints;
import netscape.ldap.LDAPSearchListener;
import netscape.ldap.LDAPSearchResult;
import netscape.ldap.LDAPSearchResultReference;
import netscape.ldap.LDAPv2;
import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Request;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchEntry;
import org.ldaptive.SearchReference;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.async.AsyncRequest;
import org.ldaptive.control.RequestControl;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.extended.ExtendedRequest;
import org.ldaptive.extended.ExtendedResponse;
import org.ldaptive.extended.ExtendedResponseFactory;
import org.ldaptive.extended.UnsolicitedNotificationListener;
import org.ldaptive.provider.ProviderConnection;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchItem;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.provider.SearchListener;
import org.ldaptive.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netscape provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeConnection implements ProviderConnection
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
    final LDAPConnection lc,
    final NetscapeProviderConfig pc)
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
  public void close(final RequestControl[] controls)
    throws LdapException
  {
    if (controls != null) {
      throw new UnsupportedOperationException(
        "Provider does not support unbind with controls");
    }
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
        request,
        ResultCode.COMPARE_TRUE.value() == r.getResultCode(),
        r);
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
  public SearchIterator search(final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final NetscapeSearchIterator i = new NetscapeSearchIterator(request);
    i.initialize();
    return i;
  }


  /** {@inheritDoc} */
  @Override
  public void searchAsync(
    final org.ldaptive.SearchRequest request,
    final SearchListener listener)
    throws LdapException
  {
    final NetscapeAsyncSearchListener l = new NetscapeAsyncSearchListener(
      request,
      listener);
    l.initialize();
  }


  /** {@inheritDoc} */
  @Override
  public void abandon(final int messageId, final RequestControl[] controls)
    throws LdapException
  {
    if (controls != null) {
      throw new UnsupportedOperationException(
        "Provider does not support abandon with controls");
    }
    try {
      connection.abandon(messageId);
    } catch (LDAPException e) {
      processLDAPException(e);
    }
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


  /** {@inheritDoc} */
  @Override
  public void addUnsolicitedNotificationListener(
    final UnsolicitedNotificationListener listener)
  {
    throw new UnsupportedOperationException(
      "Unsolicited notifications not supported");
  }


  /** {@inheritDoc} */
  @Override
  public void removeUnsolicitedNotificationListener(
    final UnsolicitedNotificationListener listener)
  {
    throw new UnsupportedOperationException(
      "Unsolicited notifications not supported");
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
    final Request request,
    final LDAPConstraints cons)
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
          cons.setRebindProc(
            new LDAPRebind() {
              @Override
              public LDAPRebindAuth getRebindAuthentication(
                final String host,
                final int port)
              {
                return
                  new LDAPRebindAuth(
                    connection.getAuthenticationDN(),
                    connection.getAuthenticationPassword());
              }
            });
        }
      }
    }
  }


  /**
   * Determines if the supplied response should result in an operation retry.
   *
   * @param  request  that produced the exception
   * @param  ldapResponse  provider response
   *
   * @throws  LdapException  wrapping the ldap exception
   */
  protected void throwOperationException(
    final Request request,
    final LDAPResponse ldapResponse)
    throws LdapException
  {
    ProviderUtils.throwOperationException(
      config.getOperationExceptionResultCodes(),
      String.format(
        "Ldap returned result code: %s",
        ldapResponse.getResultCode()),
      ldapResponse.getResultCode(),
      ldapResponse.getMatchedDN(),
      config.getControlProcessor().processResponseControls(
        ldapResponse.getControls()),
      ldapResponse.getReferrals(),
      false);
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
    return
      new Response<T>(
        result,
        ResultCode.valueOf(ldapResponse.getResultCode()),
        ldapResponse.getErrorMessage(),
        ldapResponse.getMatchedDN(),
        config.getControlProcessor().processResponseControls(
          ldapResponse.getControls()),
        ldapResponse.getReferrals(),
        ldapResponse.getMessageID());
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
      config.getOperationExceptionResultCodes(),
      e,
      e instanceof LDAPReferralException ? ResultCode.REFERRAL.value()
                                         : e.getLDAPResultCode(),
      e.getMatchedDN(),
      null,
      null,
      true);
  }


  /** Search iterator for netscape search results. */
  protected class NetscapeSearchIterator extends AbstractNetscapeSearch
    implements SearchIterator
  {

    /** Response data. */
    private Response<Void> response;

    /** Ldap search result iterator. */
    private SearchResultIterator resultIterator;


    /**
     * Creates a new netscape search iterator.
     *
     * @param  sr  search request
     */
    public NetscapeSearchIterator(final SearchRequest sr)
    {
      super(sr);
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
        resultIterator = new SearchResultIterator(search(connection, request));
      } catch (LDAPException e) {
        processLDAPException(e);
      }
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
      throws LdapException
    {
      if (resultIterator == null || response != null) {
        return false;
      }

      boolean more = false;
      try {
        more = resultIterator.hasNext();
        if (!more) {
          final LDAPResponse res = resultIterator.getResponse();
          logger.trace("reading search response: {}", res);
          throwOperationException(request, res);
          response = createResponse(request, null, res);
        }
      } catch (LDAPException e) {
        final ResultCode rc = ignoreSearchException(
          config.getSearchIgnoreResultCodes(),
          e);
        if (rc == null) {
          processLDAPException(e);
        }
        response = new Response<Void>(
          null,
          rc,
          e.getLDAPErrorMessage(),
          e.getMatchedDN(),
          null,
          null,
          -1);
      }
      return more;
    }


    /** {@inheritDoc} */
    @Override
    public SearchItem next()
      throws LdapException
    {
      SearchItem si;
      final LDAPMessage message = resultIterator.next();
      if (message instanceof LDAPSearchResult) {
        si = processLDAPSearchResult((LDAPSearchResult) message);
      } else if (message instanceof LDAPSearchResultReference) {
        si = processLDAPSearchResultReference(
          (LDAPSearchResultReference) message);
      } else {
        throw new IllegalStateException("Unknown message: " + message);
      }
      return si;
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


  /** Async search listener for Netscape search results. */
  protected class NetscapeAsyncSearchListener extends AbstractNetscapeSearch
  {

    /** Search result listener. */
    private final SearchListener listener;


    /**
     * Creates a new netscape async search listener.
     *
     * @param  sr  search request
     * @param  sl  search listener
     */
    public NetscapeAsyncSearchListener(
      final SearchRequest sr,
      final SearchListener sl)
    {
      super(sr);
      listener = sl;
    }


    /**
     * Initializes this netscape search listener.
     *
     * @throws  LdapException  if an error occurs
     */
    public void initialize()
      throws LdapException
    {
      try {
        search(connection, request);
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
     * @return  ldap search listener
     *
     * @throws  LDAPException  if an error occurs
     */
    protected LDAPSearchListener search(
      final LDAPConnection conn,
      final SearchRequest sr)
      throws LDAPException
    {
      final SearchResultIterator i = new SearchResultIterator(
        super.search(conn, sr));
      listener.asyncRequestReceived(
        new NetscapeAsyncRequest(i.getLDAPSearchListener()));
      while (i.hasNext()) {
        final LDAPMessage message = i.next();
        if (message instanceof LDAPSearchResult) {
          listener.searchItemReceived(
            processLDAPSearchResult((LDAPSearchResult) message));
        } else if (message instanceof LDAPSearchResultReference) {
          listener.searchItemReceived(
            processLDAPSearchResultReference(
              (LDAPSearchResultReference) message));
        } else {
          throw new IllegalStateException("Unknown message: " + message);
        }
      }

      final Response<Void> response = createResponse(
        request,
        null,
        i.getResponse());
      listener.responseReceived(response);
      return null;
    }
  }


  /** Common search functionality for netscape iterators and listeners. */
  protected abstract class AbstractNetscapeSearch
  {

    /** Search request. */
    protected final SearchRequest request;

    /** Utility class. */
    protected final NetscapeUtils util;


    /**
     * Creates a new abstract netscape search.
     *
     * @param  sr  search request
     */
    public AbstractNetscapeSearch(final SearchRequest sr)
    {
      request = sr;
      util = new NetscapeUtils(request.getSortBehavior());
      util.setBinaryAttributes(request.getBinaryAttributes());
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
    protected LDAPSearchListener search(
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
          sr.getSearchFilter() != null ? sr.getSearchFilter().format() : null,
          retAttrs,
          sr.getTypesOnly(),
          (LDAPSearchListener) null,
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


    /**
     * Processes the response controls on the supplied result and returns a
     * corresponding search item.
     *
     * @param  res  to process
     *
     * @return  search item
     */
    protected SearchItem processLDAPSearchResult(final LDAPSearchResult res)
    {
      logger.trace("reading search result: {}", res);

      ResponseControl[] respControls = null;
      if (res.getControls() != null && res.getControls().length > 0) {
        respControls = config.getControlProcessor().processResponseControls(
          res.getControls());
      }

      final SearchEntry se = util.toSearchEntry(
        res.getEntry(),
        respControls,
        res.getMessageID());
      return new SearchItem(se);
    }


    /**
     * Processes the response controls on the supplied reference and returns a
     * corresponding search item.
     *
     * @param  ref  to process
     *
     * @return  search item
     */
    protected SearchItem processLDAPSearchResultReference(
      final LDAPSearchResultReference ref)
    {
      logger.trace("reading search reference: {}", ref);

      ResponseControl[] respControls = null;
      if (ref.getControls() != null && ref.getControls().length > 0) {
        respControls = config.getControlProcessor().processResponseControls(
          ref.getControls());
      }

      final SearchReference sr = new SearchReference(
        ref.getMessageID(),
        respControls,
        ref.getUrls());
      return new SearchItem(sr);
    }
  }


  /** Iterates over an ldap search listener. */
  protected static class SearchResultIterator
  {

    /** Listener to iterate over. */
    private final LDAPSearchListener listener;

    /** Last response message received from the listener. */
    private LDAPMessage message;

    /** Response available after all messages have been received. */
    private LDAPResponse response;


    /**
     * Create a new ldap search listener iterator.
     *
     * @param  l  ldap search listener
     */
    public SearchResultIterator(final LDAPSearchListener l)
    {
      listener = l;
    }


    /**
     * Returns the ldap search listener.
     *
     * @return  ldap search listener
     */
    public LDAPSearchListener getLDAPSearchListener()
    {
      return listener;
    }


    /**
     * Returns whether the listener has another message to read.
     *
     * @return  whether the listener has another message to read
     *
     * @throws  LDAPException  if an error occurs reading the response
     */
    public boolean hasNext()
      throws LDAPException
    {
      if (response != null) {
        return false;
      }

      boolean more = false;
      message = listener.getResponse();
      if (message != null) {
        if (message instanceof LDAPSearchResult) {
          more = true;
        } else if (message instanceof LDAPSearchResultReference) {
          more = true;
        } else {
          response = (LDAPResponse) message;
        }
      }
      return more;
    }


    /**
     * Returns the next message in the listener.
     *
     * @return  ldap message
     */
    public LDAPMessage next()
    {
      return message;
    }


    /**
     * Returns the search response. Available after all messages have been read
     * from the listener.
     *
     * @return  ldap search response
     */
    public LDAPResponse getResponse()
    {
      return response;
    }
  }


  /** Async request to invoke abandons. */
  protected class NetscapeAsyncRequest implements AsyncRequest
  {

    /** Search listener. */
    private final LDAPSearchListener searchListener;


    /**
     * Creates a new Netscape async request.
     *
     * @param  listener  from an async operation
     */
    public NetscapeAsyncRequest(final LDAPSearchListener listener)
    {
      searchListener = listener;
    }


    /** {@inheritDoc} */
    @Override
    public int getMessageId()
    {
      final int[] ids = searchListener.getMessageIDs();
      if (ids == null || ids.length == 0) {
        return -1;
      }
      return ids[ids.length - 1];
    }


    /** {@inheritDoc} */
    @Override
    public void abandon()
      throws LdapException
    {
      try {
        connection.abandon(searchListener);
      } catch (LDAPException e) {
        processLDAPException(e);
      }
    }


    /** {@inheritDoc} */
    @Override
    public void abandon(final RequestControl[] controls)
      throws LdapException
    {
      throw new UnsupportedOperationException(
        "Cannot abandon operation with request controls");
    }
  }
}
